package Task;
import java.util.ArrayList;

public class EpicTask extends SimpleTask {
    ArrayList<Integer> subTaskIDs = new ArrayList<>();

    public EpicTask(String name, String description) {
        super(name, description);
    }

    public void addSubTask(SubTask subTask){
        if (subTask != null){
            int subTaskId = subTask.getId();
            if (!this.subTaskIDs.contains(subTaskId)){
                this.subTaskIDs.add(subTaskId);
            }
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
        return super.toString() + ", Sub=" + this.subTaskIDs.toString()  +'}' + System.lineSeparator();
    }
}
