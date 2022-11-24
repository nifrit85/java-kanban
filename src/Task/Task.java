package Task;

public abstract class Task {
    int Id;
    String name;
    String description;
    Status status = Status.NEW;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setID(int Id) {
        this.Id = Id;
    }

    public int getId() {
        return Id;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeOfTask getTaskType(){
        return null;
    }
    public String stringForFile(){
        return Id                           + "," +
                getTaskType().toString()     + "," +
                getName()                    + "," +
                getStatus().toString()       + "," +
                getDescription();

    }
}
