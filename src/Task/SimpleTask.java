package Task;
public class SimpleTask {
    int Id;
    String name;
    String description;
    Status status = Status.NEW;

    public void setID(int Id) {
        this.Id = Id;
    }

    public int getId() {
        return Id;
    }

    public SimpleTask(String name, String description) {
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

    @Override
    public String toString() {
        return "Task{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", type=" + this.getClass().getName() + System.lineSeparator();
    }
}
