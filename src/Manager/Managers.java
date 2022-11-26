package Manager;

public class Managers {
    public static TaskManager getManager(TypeOfManager typeOfManager, String pathToFile) {
        switch (typeOfManager) {
            case FILE:
                if (pathToFile != null) {
                    return new FileBackedTasksManager(pathToFile);
                }
            default:
                return new InMemoryTaskManager();
        }
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
