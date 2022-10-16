import Task.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    @Override
    public void add(Task task) {
        if (history.size() == 10) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}
