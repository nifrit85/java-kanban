package Manager;

import Task.Task;
import java.util.ArrayDeque;
import java.util.Queue;


public class InMemoryHistoryManager implements HistoryManager {

    Queue<Task> history = new ArrayDeque<>();

    @Override
    public void add(Task task) {
        if (task != null){
            if (history.size() == 10) {
                history.poll();
            }
            history.add(task);
        }
    }

    @Override
    public Queue<Task> getHistory() {
        return history;
    }
}
