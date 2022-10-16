package Task;

public class SubTask extends Task {
    private Integer parentID;

    public SubTask(String name, String description) {
        super(name, description);
    }

    public void setParent(EpicTask parent) {
        if (parent != null) {
            this.parentID = parent.getId();
        }
    }

    public Integer getParentID() {
        return parentID;
    }

    public void clearParent(){
        this.parentID = null;
    }

    @Override
    public String toString() {
        return super.toString() + ", Parent=" + this.parentID + '}' + System.lineSeparator();
    }
}
