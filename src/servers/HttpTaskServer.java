package servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import constants.Constants;
import constants.TypeOfMethods;
import constants.TypeOfTask;
import exceptions.IntersectionsException;
import managers.interfaces.TaskManager;
import task.EpicTask;
import task.SimpleTask;
import task.SubTask;
import task.Task;
import utilities.MyGsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.regex.Pattern;

import static jdk.internal.util.xml.XMLStreamWriter.DEFAULT_CHARSET;

public class HttpTaskServer {

    private static final int PORT = 8080;

    private final TaskManager taskManager;

    private final Gson gson = MyGsonBuilder.create();

    private final HttpServer server;


    public HttpTaskServer(TaskManager manager) throws IOException {
        taskManager = manager;
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext(Constants.PATH_SIMPLE, new SimpleTaskHandler());
        server.createContext(Constants.PATH_SUB, new SubTaskHandler());
        server.createContext(Constants.PATH_EPIC, new EpicTaskHandler());
        server.createContext(Constants.PATH_HISTORY, new HistoryHandler());
        server.createContext(Constants.PATH_TASKS, new TasksHandler());
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public static void sendResponse(HttpExchange exchange, String contentType, String response, int rCode) throws IOException {
        byte[] bytes = null;
        int length = 0;
        if (contentType != null) {
            exchange.getResponseHeaders().add(Constants.CONTENT_TYPE, contentType);
        }
        if (response != null) {
            bytes = response.getBytes(DEFAULT_CHARSET);
        }

        exchange.sendResponseHeaders(rCode, length);
        if (bytes != null) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
        exchange.close();
    }

    private void processGet(HttpExchange exchange, TypeOfTask type) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String path = exchange.getRequestURI().getPath();
        //Исключим неверные обращения
        if (!Pattern.matches(Constants.GOOD_PATTERN, path)) {
            sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
            return;
        }
        String response = null;
        int id;
        if (query == null) {
            switch (type) {
                case SIMPLE:
                    response = gson.toJson(taskManager.getSimpleTasks());
                    break;
                case SUB:
                    response = gson.toJson(taskManager.getSubTasks());
                    break;
                case EPIC:
                    response = gson.toJson(taskManager.getEpicTasks());
                    break;
            }
            sendResponse(exchange, Constants.CONTENT_TYPE_JSON, response, HttpURLConnection.HTTP_OK);

        } else if (query.startsWith(Constants.ID_PATTERN) && Pattern.matches(Constants.GOOD_PATTERN_FOR_ID, path)) {
            id = Integer.parseInt(query.substring(3));
            Task task = taskManager.getTaskById(id);
            if (task == null) {
                sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }
            if (task.getTaskType() == type) {
                response = gson.toJson(task);
                sendResponse(exchange, Constants.CONTENT_TYPE_JSON, response, HttpURLConnection.HTTP_OK);
            } else { //Запросили тип задачи не из того Эндпоинта
                sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
            }
            //Отдельная обработка Эпика для подзадачи
        } else if (query.startsWith(Constants.ID_PATTERN) && path.equals("/tasks/subtask/epic/")) {
            id = Integer.parseInt(query.substring(3));
            Task epicTask = taskManager.getTaskById(id);
            if (epicTask == null || epicTask.getTaskType() != TypeOfTask.EPIC) {
                sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }
            Map<Integer, SubTask> subTaskFromEpic = taskManager.getSubTaskFromEpic((EpicTask) epicTask);
            response = gson.toJson(subTaskFromEpic);
            sendResponse(exchange, Constants.CONTENT_TYPE_JSON, response, HttpURLConnection.HTTP_OK);
        } else {
            sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void processPost(HttpExchange exchange, TypeOfTask type) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        boolean isNotFound = false;
        String path = exchange.getRequestURI().getPath();
        //Исключим неверные обращения
        if (!Pattern.matches(Constants.GOOD_PATTERN, path)) {
            sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
            return;
        }

        Task task = null;
        switch (type) {
            case SIMPLE:
                SimpleTask simpleTask = gson.fromJson(body, SimpleTask.class);
                if (simpleTask == null) {
                    sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
                    return;
                }
                task = simpleTask;
                Map<Integer, SimpleTask> simpleTaskMap = taskManager.getSimpleTasks();
                if (simpleTaskMap.containsKey(simpleTask.getId())) {
                    try {
                        taskManager.updateTask(simpleTask);
                        sendResponse(exchange, null, null, HttpURLConnection.HTTP_OK);
                        return;
                    } catch (IntersectionsException e) {
                        sendResponse(exchange, Constants.CONTENT_TYPE_TEXT, e.getMessage(), HttpURLConnection.HTTP_CONFLICT);
                        return;
                    }
                } else isNotFound = true;
                break;
            case SUB:
                SubTask subTask = gson.fromJson(body, SubTask.class);
                if (subTask == null) {
                    sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
                    return;
                }
                task = subTask;
                Map<Integer, SubTask> subTaskMap = taskManager.getSubTasks();
                if (subTaskMap.containsKey(subTask.getId())) {
                    try {
                        taskManager.updateTask(subTask);
                        sendResponse(exchange, null, null, HttpURLConnection.HTTP_OK);
                        return;
                    } catch (IntersectionsException e) {
                        sendResponse(exchange, Constants.CONTENT_TYPE_TEXT, e.getMessage(), HttpURLConnection.HTTP_CONFLICT);
                        return;
                    }
                } else isNotFound = true;
                break;
            case EPIC:
                EpicTask epicTask = gson.fromJson(body, EpicTask.class);
                if (epicTask == null) {
                    sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
                    return;
                }
                task = epicTask;
                Map<Integer, EpicTask> epicTaskMap = taskManager.getEpicTasks();
                if (epicTaskMap.containsKey(epicTask.getId())) {
                    try {
                        taskManager.updateTask(epicTask);
                        sendResponse(exchange, null, null, HttpURLConnection.HTTP_OK);
                        return;
                    } catch (IntersectionsException e) {
                        sendResponse(exchange, Constants.CONTENT_TYPE_TEXT, e.getMessage(), HttpURLConnection.HTTP_CONFLICT);
                        return;
                    }
                } else isNotFound = true;
                break;
        }

        Map<Integer, Task> allTaskMap = taskManager.getTasks();

        if (isNotFound && allTaskMap.containsKey(task.getId())) {//Задача есть, но ошиблись типом
            sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);

        } else { //Новый
            try {
                taskManager.addTask(task, null);
                sendResponse(exchange, null, null, HttpURLConnection.HTTP_OK);
            } catch (IntersectionsException e) {
                sendResponse(exchange, Constants.CONTENT_TYPE_TEXT, e.getMessage(), HttpURLConnection.HTTP_CONFLICT);
            }
        }
    }

    private void processDelete(HttpExchange exchange, TypeOfTask type) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String path = exchange.getRequestURI().getPath();
        if (!Pattern.matches(Constants.GOOD_PATTERN, path)) {
            sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
            return;
        }
        if (query == null) {
            switch (type) {
                case SIMPLE:
                    taskManager.clearSimpleTasks();
                    break;
                case SUB:
                    taskManager.clearSubTasks();
                    break;
                case EPIC:
                    taskManager.clearEpicTasks();
                    break;
            }
            sendResponse(exchange, null, null, HttpURLConnection.HTTP_OK);
        } else if (query.startsWith(Constants.ID_PATTERN)) {
            int id = Integer.parseInt(query.substring(3));
            switch (type) {
                case SIMPLE:
                    taskManager.deleteSimpleTaskByID(id);
                    break;
                case SUB:
                    taskManager.deleteSubTaskByID(id);
                    break;
                case EPIC:
                    taskManager.deleteEpicTaskByID(id);
                    break;
            }
            sendResponse(exchange, null, null, HttpURLConnection.HTTP_OK);
        } else {
            sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    class SimpleTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            switch (TypeOfMethods.valueOf(method)) {
                case GET:
                    processGet(exchange, TypeOfTask.SIMPLE);
                    break;
                case POST:
                    processPost(exchange, TypeOfTask.SIMPLE);
                    break;
                case DELETE:
                    processDelete(exchange, TypeOfTask.SIMPLE);
                    break;
                default:
                    sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
            }
        }
    }

    class SubTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            switch (TypeOfMethods.valueOf(method)) {
                case GET:
                    processGet(exchange, TypeOfTask.SUB);
                    break;
                case POST:
                    processPost(exchange, TypeOfTask.SUB);
                    break;
                case DELETE:
                    processDelete(exchange, TypeOfTask.SUB);
                    break;
                default:
                    sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
            }
        }
    }

    class EpicTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            switch (TypeOfMethods.valueOf(method)) {
                case GET:
                    processGet(exchange, TypeOfTask.EPIC);
                    break;
                case POST:
                    processPost(exchange, TypeOfTask.EPIC);
                    break;
                case DELETE:
                    processDelete(exchange, TypeOfTask.EPIC);
                    break;
                default:
                    sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
            }
        }
    }

    class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();
            String path = exchange.getRequestURI().getPath();

            if (TypeOfMethods.valueOf(method) == TypeOfMethods.GET) {
                if (query != null) {
                    sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
                }
                if (!Pattern.matches(Constants.HISTORY_PATTERN, path)) {
                    sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
                }
                String response = gson.toJson(taskManager.getHistory());
                sendResponse(exchange, Constants.CONTENT_TYPE_JSON, response, HttpURLConnection.HTTP_OK);

            } else sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }


    class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();
            String path = exchange.getRequestURI().getPath();

            if (TypeOfMethods.valueOf(method) == TypeOfMethods.GET) {
                if (query != null) {
                    sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
                }
                if (!Pattern.matches(Constants.TASKS_PATTERN, path)) {
                    sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
                }
                String response = gson.toJson(taskManager.getPrioritizedTasks());
                sendResponse(exchange, Constants.CONTENT_TYPE_JSON, response, HttpURLConnection.HTTP_OK);
            } else sendResponse(exchange, null, null, HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }
}






