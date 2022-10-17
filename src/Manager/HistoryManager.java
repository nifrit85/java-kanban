package Manager;

import Task.Task;
import java.util.Queue;


public interface HistoryManager {

    void add(Task task);

    Queue<Task> getHistory();
}
