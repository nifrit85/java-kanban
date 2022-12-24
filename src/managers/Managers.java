package managers;

import constants.TypeOfManager;
import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import servers.KVServer;
import utilities.FileManager;

import java.io.IOException;
import java.net.URI;

public class Managers {
    public static TaskManager getManager(TypeOfManager typeOfManager, String path) {
        switch (typeOfManager) {
            case MEMORY:
                return new InMemoryTaskManager();
            case FILE:
                if (path != null && FileManager.fileExist(path)) {
                    return new FileBackedTasksManager(path);
                }
                break;
            case HTTP:
                try {
                    if (path != null) return new HttpTaskManager(URI.create(path));
                } catch (IOException | InterruptedException e) {
                    e.fillInStackTrace();
                }
                break;
        }
        //Если не указали тип
        try {
            if (path != null) return new HttpTaskManager(URI.create(path));
            return new HttpTaskManager(URI.create("http://localhost:" + KVServer.PORT));
        } catch (IOException | InterruptedException e) {
            e.fillInStackTrace();
        }
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
