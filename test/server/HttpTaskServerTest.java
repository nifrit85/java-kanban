package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import constants.Constants;
import constants.Status;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.HttpTaskServer;
import task.EpicTask;
import task.SimpleTask;
import task.SubTask;
import utilities.MyGsonBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private HttpTaskServer server;
    private InMemoryTaskManager manager;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = MyGsonBuilder.create();

    @BeforeEach
    void BeforeEach() {
        manager = new InMemoryTaskManager();
        try {
            server = new HttpTaskServer(manager);
            server.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        //Один Симпл
        SimpleTask simpleTaskToAdd = new SimpleTask("NameSimple", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 12, 20, 14, 40, 0), Duration.ofHours(10));
        manager.addTask(simpleTaskToAdd, null);

        //Один Эпик
        EpicTask epicTaskToAdd = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        manager.addTask(epicTaskToAdd, null);

        //Три сабтаска для эпика
        SubTask subTaskToAdd = new SubTask("NameSub1", "DescriptionSub1", Status.NEW, LocalDateTime.of(2022, 12, 11, 14, 40, 0), Duration.ofHours(10));
        manager.addTask(subTaskToAdd, epicTaskToAdd);

        subTaskToAdd = new SubTask("NameSub2", "DescriptionSub2", Status.NEW, LocalDateTime.of(2022, 12, 13, 14, 40, 0), Duration.ofHours(20));
        manager.addTask(subTaskToAdd, epicTaskToAdd);

        subTaskToAdd = new SubTask("NameSub3", "DescriptionSub3", Status.NEW, LocalDateTime.of(2022, 12, 15, 14, 40, 0), Duration.ofHours(30));
        manager.addTask(subTaskToAdd, epicTaskToAdd);

        //Один Эпик без подзадач
        epicTaskToAdd = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        manager.addTask(epicTaskToAdd, null);

        //Наполняем историю 1,4
        manager.getTaskById(1);
        manager.getTaskById(4);
    }

    @AfterEach
    void AfterEach() {
        server.stop();
    }

    @Test
    void checkGetTaskGood() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        Type type = new TypeToken<Map<Integer, SimpleTask>>() {
        }.getType();
        Map<Integer, SimpleTask> simpleTasks = gson.fromJson(response.body(), type);
        //Проверим количество записей
        assertEquals(manager.getSimpleTasks().size(), simpleTasks.size());
        //Проверим наполнение
        for (Map.Entry<Integer, SimpleTask> task : manager.getSimpleTasks().entrySet()) {
            assertTrue(simpleTasks.containsKey(task.getKey()));
        }
        //Добавим слеш в конце
        uri = URI.create("http://localhost:8080/tasks/task/");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
    }

    @Test
    void checkGetEpicGood() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        Type type = new TypeToken<Map<Integer, EpicTask>>() {
        }.getType();
        Map<Integer, EpicTask> epicTasks = gson.fromJson(response.body(), type);
        //Проверим количество записей
        assertEquals(manager.getEpicTasks().size(), epicTasks.size());
        //Проверим наполнение
        for (Map.Entry<Integer, EpicTask> task : manager.getEpicTasks().entrySet()) {
            assertTrue(epicTasks.containsKey(task.getKey()));
        }
        //Добавим слеш в конце
        uri = URI.create("http://localhost:8080/tasks/epic/");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
    }

    @Test
    void checkGetSubGood() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        Type type = new TypeToken<Map<Integer, SubTask>>() {
        }.getType();
        Map<Integer, SubTask> subTasks = gson.fromJson(response.body(), type);
        //Проверим количество записей
        assertEquals(manager.getSubTasks().size(), subTasks.size());
        //Проверим наполнение
        for (Map.Entry<Integer, SubTask> task : manager.getSubTasks().entrySet()) {
            assertTrue(subTasks.containsKey(task.getKey()));
        }
        //Добавим слеш в конце
        uri = URI.create("http://localhost:8080/tasks/subtask/");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
    }

    @Test
    void checkBadRequest() throws IOException, InterruptedException {
        //Неверный запрос
        URI uri = URI.create("http://localhost:8080/tasks/task1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.statusCode());

        uri = URI.create("http://localhost:8080/tasks/epic1");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.statusCode());

        uri = URI.create("http://localhost:8080/tasks/subtask1");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.statusCode());

        //Неверный метод
        uri = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder().uri(uri).PUT(HttpRequest.BodyPublishers.noBody()).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_BAD_METHOD, response.statusCode());

        uri = URI.create("http://localhost:8080/tasks/epic");
        request = HttpRequest.newBuilder().uri(uri).PUT(HttpRequest.BodyPublishers.noBody()).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_BAD_METHOD, response.statusCode());

        uri = URI.create("http://localhost:8080/tasks/subtask");
        request = HttpRequest.newBuilder().uri(uri).PUT(HttpRequest.BodyPublishers.noBody()).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_BAD_METHOD, response.statusCode());
    }

    @Test
    void checkGetGoodId() throws IOException, InterruptedException {

        URI uri = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

        SimpleTask simpleTask = (SimpleTask) manager.getTaskById(1);
        SimpleTask simpleTaskFromResponse = gson.fromJson(response.body(), SimpleTask.class);
        assertEquals(simpleTask.toString(), simpleTaskFromResponse.toString());
        //Другая форма запроса
        uri = URI.create("http://localhost:8080/tasks/task?id=1");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

        uri = URI.create("http://localhost:8080/tasks/epic/?id=2");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

        EpicTask epicTask = (EpicTask) manager.getTaskById(2);
        EpicTask epicTaskFromResponse = gson.fromJson(response.body(), EpicTask.class);
        assertEquals(epicTask.toString(), epicTaskFromResponse.toString());

        //Другая форма запроса
        uri = URI.create("http://localhost:8080/tasks/epic?id=2");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

        uri = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

        SubTask subTask = (SubTask) manager.getTaskById(3);
        SubTask subTaskFromResponse = gson.fromJson(response.body(), SubTask.class);
        assertEquals(subTask.toString(), subTaskFromResponse.toString());

        //Другая форма запроса
        uri = URI.create("http://localhost:8080/tasks/subtask?id=3");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
    }

    @Test
    void checkGetBadId() throws IOException, InterruptedException {
        //Неверные типы задач
        URI uri = URI.create("http://localhost:8080/tasks/task/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.statusCode());

        uri = URI.create("http://localhost:8080/tasks/epic/?id=3");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.statusCode());

        uri = URI.create("http://localhost:8080/tasks/subtask/?id=1");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.statusCode());

        //Неверные ID
        uri = URI.create("http://localhost:8080/tasks/task/?id=99");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.statusCode());

        uri = URI.create("http://localhost:8080/tasks/epic/?id=88");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.statusCode());

        uri = URI.create("http://localhost:8080/tasks/subtask/?id=77");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.statusCode());
    }

    @Test
    void checkPostGood() throws IOException, InterruptedException {
        manager.clearTasks();
        //Проверим что нет задач
        assertTrue(manager.getSimpleTasks().isEmpty());

        //Симпл
        SimpleTask simpleTaskToAdd = new SimpleTask("NameSimple", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 12, 20, 14, 40, 0), Duration.ofHours(10));
        simpleTaskToAdd.setID(1);
        String body = gson.toJson(simpleTaskToAdd);

        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(body)).header(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_CREATED, response.statusCode());
        //Проверим соответствует ли наша задача той, что легла через POST
        assertEquals(simpleTaskToAdd.toString(), manager.getTaskById(1).toString());

        //Эпик
        EpicTask epicTaskToAdd = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        epicTaskToAdd.setID(2);
        epicTaskToAdd.setDuration(Duration.ZERO);
        body = gson.toJson(epicTaskToAdd);

        uri = URI.create("http://localhost:8080/tasks/epic");
        request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(body)).header(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_CREATED, response.statusCode());
        //Проверим соответствует ли наша задача той, что легла через POST
        assertEquals(epicTaskToAdd.toString(), manager.getTaskById(2).toString());

        //Сабтаск
        SubTask subTaskToAdd = new SubTask("NameSub1", "DescriptionSub1", Status.NEW, LocalDateTime.of(2022, 12, 11, 14, 40, 0), Duration.ofHours(10));
        subTaskToAdd.setID(3);
        subTaskToAdd.setParent(epicTaskToAdd.getId());
        body = gson.toJson(subTaskToAdd);

        uri = URI.create("http://localhost:8080/tasks/subtask");
        request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(body)).header(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_CREATED, response.statusCode());
        //Проверим соответствует ли наша задача той, что легла через POST
        assertEquals(subTaskToAdd.toString(), manager.getTaskById(3).toString());
    }

    @Test
    void checkPostBad() throws IOException, InterruptedException {

        //Проверим что задачи есть
        assertFalse(manager.getSimpleTasks().isEmpty());

        //Добавим симпл через POST Эпика
        SimpleTask simpleTaskToAdd = new SimpleTask("NameSimple", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 12, 20, 14, 40, 0), Duration.ofHours(10));
        simpleTaskToAdd.setID(1);
        String body = gson.toJson(simpleTaskToAdd);

        URI uri = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(body)).header(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.statusCode());
    }

    @Test
    void checkDeleteGood() throws IOException, InterruptedException {

        URI uri = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        assertNull(manager.getTaskById(1));

        uri = URI.create("http://localhost:8080/tasks/epic/?id=2");
        request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        assertNull(manager.getTaskById(2));

        uri = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        assertNull(manager.getTaskById(3));
    }

    @Test
    void checkDeleteBad() throws IOException, InterruptedException {
        //Неверные типы задач
        URI uri = URI.create("http://localhost:8080/tasks/task/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        assertNotNull(manager.getTaskById(3));

        uri = URI.create("http://localhost:8080/tasks/epic/?id=1");
        request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        assertNotNull(manager.getTaskById(1));

        uri = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        assertNotNull(manager.getTaskById(2));
    }

    @Test
    void checkDeleteAll() throws IOException, InterruptedException {
        assertFalse(manager.getSimpleTasks().isEmpty());
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        assertTrue(manager.getSimpleTasks().isEmpty());

        assertFalse(manager.getEpicTasks().isEmpty());
        uri = URI.create("http://localhost:8080/tasks/epic");
        request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        assertTrue(manager.getEpicTasks().isEmpty());

        assertFalse(manager.getSubTasks().isEmpty());
        uri = URI.create("http://localhost:8080/tasks/subtask");
        request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        assertTrue(manager.getSubTasks().isEmpty());
    }

    @Test
    void checkGetSubFromEpicGood() throws IOException, InterruptedException {
        assertFalse(manager.getSimpleTasks().isEmpty());
        URI uri = URI.create("http://localhost:8080/tasks/subtask/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

        EpicTask epicTask = (EpicTask) manager.getTaskById(2);
        Type type = new TypeToken<Map<Integer, SubTask>>() {
        }.getType();
        Map<Integer, SubTask> subTasks = gson.fromJson(response.body(), type);

        assertEquals(manager.getSubTaskFromEpic(epicTask).toString(), subTasks.toString());
    }

    @Test
    void checkGetSubFromEpicBad() throws IOException, InterruptedException {
        assertFalse(manager.getSimpleTasks().isEmpty());
        URI uri = URI.create("http://localhost:8080/tasks/subtask/epic/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //Проверим код ответа
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.statusCode());
    }
}
