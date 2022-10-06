package Task;

public class SubTask extends Task {
    private Integer parentID;

    public SubTask(String name, String description) {
        super(name, description);
    }

    public void setParent(EpicTask parent) {
        if (parent != null){
            this.parentID = parent.getId();
        }
    }

    public int getParentID() {
        return parentID;
    }

    @Override
    public String toString() {
        return super.toString() + ", Parent=" + this.parentID + '}' + "\r\n";
    }
}
