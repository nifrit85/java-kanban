package managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import constants.Constants;
import exceptions.IntersectionsException;
import servers.KVServer;
import servers.client.KVTaskClient;
import task.EpicTask;
import task.SimpleTask;
import task.SubTask;
import task.Task;
import utilities.MyGsonBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private static final Gson gson = MyGsonBuilder.create();

    public HttpTaskManager(URI path) throws InterruptedException, IOException {
        new KVServer().start();
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
        } catch (InterruptedException e) {
            log.log(Level.WARNING, e.getMessage());
        }
    }

    @Override
    protected void save() {
        try {
            client.put(Constants.HTTP_TASKS, gson.toJson(getSimpleTasks()));
            client.put(Constants.HTTP_EPIC, gson.toJson(getEpicTasks()));
            client.put(Constants.HTTP_SUB, gson.toJson(getSubTasks()));

            saveHistory();
        } catch (IOException | InterruptedException e) {
            log.log(Level.WARNING, e.getMessage());
        }
    }

    private void readSimpleTasks() throws IOException, InterruptedException, IntersectionsException {
        Type type = new TypeToken<Map<Integer, SimpleTask>>() {
        }.getType();
        Map<Integer, SimpleTask> simpleTasks = gson.fromJson(client.load(Constants.HTTP_TASKS), type);
        if (simpleTasks != null) {
            for (Map.Entry<Integer, SimpleTask> taskEntry : simpleTasks.entrySet()) {
                this.simpleTasks.put(taskEntry.getKey(), taskEntry.getValue());
                this.prioritizedTasks.add(taskEntry.getValue());
            }
        }
    }

    private void readEpicTasks() throws IOException, InterruptedException, IntersectionsException {
        Type type = new TypeToken<Map<Integer, EpicTask>>() {
        }.getType();
        Map<Integer, EpicTask> epicTasks = gson.fromJson(client.load(Constants.HTTP_EPIC), type);
        if (epicTasks != null) {
            for (Map.Entry<Integer, EpicTask> taskEntry : epicTasks.entrySet()) {
                this.epicTasks.put(taskEntry.getKey(), taskEntry.getValue());
                this.prioritizedTasks.add(taskEntry.getValue());
            }
        }
    }

    private void readSubTasks() throws IOException, InterruptedException, IntersectionsException {
        Type type = new TypeToken<Map<Integer, SubTask>>() {
        }.getType();
        Map<Integer, SubTask> subTasks = gson.fromJson(client.load(Constants.HTTP_SUB), type);
        if (subTasks != null) {
            for (Map.Entry<Integer, SubTask> taskEntry : subTasks.entrySet()) {
                this.subTasks.put(taskEntry.getKey(), taskEntry.getValue());
                this.prioritizedTasks.add(taskEntry.getValue());
            }
        }
    }

    private void readHistory() throws IOException, InterruptedException {
        Type type = new TypeToken<List<Integer>>() {
        }.getType();
        List<Integer> historyIds = gson.fromJson(client.load(Constants.HTTP_HISTORY), type);
        if (historyIds != null) {
            for (Integer id : historyIds) {
                this.historyManager.add(getTaskByIdInternalUse(id));
            }
        }
    }

    private void saveHistory() throws IOException, InterruptedException {
        //Task абстрактный, так что запомним ID
        List<Integer> historyIds = new ArrayList<>();
        List<Task> history = getHistory();
        for (Task task : history) {
            historyIds.add(task.getId());
        }
        client.put(Constants.HTTP_HISTORY, gson.toJson(historyIds));
    }

}


