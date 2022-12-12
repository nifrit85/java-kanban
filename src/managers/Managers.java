package managers;

public class Managers {
    public static TaskManager getManager(TypeOfManager typeOfManager, String pathToFile) {
        if (typeOfManager == TypeOfManager.FILE && pathToFile != null) {
            return new FileBackedTasksManager(pathToFile);
        }
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
