package managers.interfaces;

import task.EpicTask;
import task.SubTask;
import task.Task;

import java.util.*;

public interface TaskManager {
    Comparator<Task> taskComparator = (t1, t2) -> {
        if (t1.getStartTime() == null && t2.getStartTime() == null) {
            return t2.getId() - t1.getId();
        }

        if (t1.getStartTime() == null) {
            return 1;
        }

        if (t2.getStartTime() == null) {
            return -1;
        }

        if (t1.getStartTime().isEqual(t2.getStartTime())) {
            return t2.getId() - t1.getId();
        }
        return t1.getStartTime().compareTo(t2.getStartTime());
    };



    void addTask(Task task, EpicTask parent);

    Map<Integer, Task> getTasks();

    void clearTasks();

    void deleteTaskByID(int id);

    Task getTaskById(int id);

    void updateTask(Task task);

    Map<Integer, SubTask> getSubTaskFromEpic(EpicTask parent);

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();

    void clearHistory();
}
