package Task;
import java.util.ArrayList;

public class EpicTask extends Task {
    ArrayList<Integer> subTaskIDs = new ArrayList<>();

    public EpicTask(String name, String description) {
        super(name, description);
    }

    public void addSubTask(int subTaskId) {
            if (!this.subTaskIDs.contains(subTaskId)) {
                this.subTaskIDs.add(subTaskId);
            }
    }

    public void delSubTask(SubTask subTask){
        if (subTask != null){
            Integer subTaskId = subTask.getId();
            this.subTaskIDs.remove(subTaskId);
        }
    }

    public void clearSubTasks(){
        this.subTaskIDs.clear();
    }

    public ArrayList<Integer> getSubTaskIDs() {
        return subTaskIDs;
    }

    @Override
    public String toString() {
        return
                "Epic{" +
                "Id=" + Id +
                ", name='" + name + "' " +
                ", description='" + description + "' " +
                ", status=" + status +
                ", subTaskIDs=" + subTaskIDs.toString() + "}" +
                System.lineSeparator();
    }

    @Override
    public TypeOfTask getTaskType() {
        return TypeOfTask.EPIC;
    }

    @Override
    public String stringForFile() {
        return super.stringForFile() + System.lineSeparator();
    }
}
