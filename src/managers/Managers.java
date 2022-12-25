package managers;

import constants.TypeOfManager;
import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import utilities.FileManager;

import java.io.IOException;
import java.net.URI;

public class Managers {
    public static TaskManager getManager(TypeOfManager typeOfManager, String path) {
        TaskManager manager = new InMemoryTaskManager();
        if (typeOfManager == TypeOfManager.FILE && path != null && FileManager.fileExist(path)) {
            manager = new FileBackedTasksManager(path);
        } else if (typeOfManager == TypeOfManager.HTTP && path != null) {
            try {
                manager = new HttpTaskManager(URI.create(path));
            } catch (IOException | InterruptedException e) {
                e.fillInStackTrace();
            }
        }
        return manager;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
