package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import exceptions.IntersectionsException;
import servers.client.KVTaskClient;
import task.EpicTask;
import task.SimpleTask;
import task.SubTask;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.*;
import java.util.logging.Level;

public class HttpTaskManager extends FileBackedTasksManager {

    private static final String TASKS = "tasks";
    private static final String EPIC = "epic";
    private static final String SUB = "subtask";
    private static final String HISTORY = "history";
    private final KVTaskClient client;
    private static final Gson gson = new GsonBuilder().serializeNulls().create();


    public HttpTaskManager(URI path) throws InterruptedException, IOException {
        super(path);
        this.client = new KVTaskClient(path.toString());
        readFile();

    }

    @Override
    protected void readFile() throws IOException {
        try {
            readSimpleTasks();
            readEpicTasks();
            readSubTasks();
            readHistory();
        } catch (InterruptedException | IntersectionsException e) {
            log.log(Level.WARNING, e.getMessage());
        }

    }

    @Override
    protected void save() {
        try {
            client.put(TASKS, gson.toJson(getSimpleTasks()));
            client.put(EPIC, gson.toJson(getEpicTasks()));
            client.put(SUB, gson.toJson(getSubTasks()));
            client.put(HISTORY, gson.toJson(getHistory()));
        } catch (IOException | InterruptedException e) {
            log.log(Level.WARNING, e.getMessage());
        }


    }

    private void readSimpleTasks() throws IOException, InterruptedException, IntersectionsException {
        Type type = new TypeToken<Map<Integer, SimpleTask>>() {
        }.getType();
        Map<Integer, SimpleTask> simpleTasks = gson.fromJson(client.load(TASKS), type);
        if (simpleTasks != null) {
            for (Map.Entry<Integer, SimpleTask> taskEntry : simpleTasks.entrySet()) {
                super.addTask(taskEntry.getValue(), null);
            }
        }
    }

    private void readEpicTasks() throws IOException, InterruptedException, IntersectionsException {
        Type type = new TypeToken<Map<Integer, EpicTask>>() {
        }.getType();
        Map<Integer, EpicTask> epicTasks = gson.fromJson(client.load(EPIC), type);
        if (epicTasks != null) {
            for (Map.Entry<Integer, EpicTask> taskEntry : epicTasks.entrySet()) {
                super.addTask(taskEntry.getValue(), null);
            }
        }
    }

    private void readSubTasks() throws IOException, InterruptedException, IntersectionsException {
        Type type = new TypeToken<Map<Integer, SubTask>>() {
        }.getType();
        Map<Integer, SubTask> subtasks = gson.fromJson(client.load(SUB), type);
        if (subtasks != null) {
            for (Map.Entry<Integer, SubTask> taskEntry : subtasks.entrySet()) {
                SubTask subTask = taskEntry.getValue();
                if (subTask != null) {
                    EpicTask parent = (EpicTask) super.getTaskByIdInternalUse(subTask.getParentID());
                    if (parent != null) {
                        super.addTask(subTask, parent);
                    }
                }
            }
        }
    }

    private void readHistory() throws IOException, InterruptedException {
        Type type = new TypeToken<List<Integer>>() {
        }.getType();
        List<Integer> historyIds = gson.fromJson(client.load(HISTORY), type);
        if (historyIds != null) {
            for (Integer id : historyIds) {
                getTaskById(id); //эмулируем вызов задачи
            }
        }
    }
}


