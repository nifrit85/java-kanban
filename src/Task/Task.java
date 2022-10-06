package Task;

abstract class Task {
   int Id;
   String name;
   String description;
   EnumStatus.Status status = EnumStatus.Status.NEW;

    public void setID(int Id) {
        this.Id = Id;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void setStatus(EnumStatus.Status status) {
        this.status = status;
    }

    public int getId() {
        return Id;
    }

    public EnumStatus.Status getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Task{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", type=" + this.getClass().getName();
    }
}


