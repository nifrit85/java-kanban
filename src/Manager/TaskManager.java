package Manager;

import Task.EpicTask;
import Task.SimpleTask;
import Task.SubTask;
import Task.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TaskManager {

    Map<Integer, SimpleTask> simpleTasks = new HashMap<>();
    Map<Integer, SubTask> subTasks = new HashMap<>();
    Map<Integer, EpicTask> epicTasks = new HashMap<>();

    void addTask(Task task, EpicTask parent);

    Map<Integer, Task> getTasks();

    void clearTasks();

    void deleteTaskByID(int Id);

    Task getTaskById(int Id);

    void updateTask(Task task);

    Map<Integer, SubTask> getSubTaskFromEpic(EpicTask parent);

    List<Task> getHistory();
}
