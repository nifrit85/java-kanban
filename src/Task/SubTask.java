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
        return
                "Sub{" +
                "Id=" + Id +
                ", name='" + name + "' " +
                ", description='" + description + "' " +
                ", status=" + status +
                ", Parent=" + parentID + "}" +
                System.lineSeparator();
    }
}
