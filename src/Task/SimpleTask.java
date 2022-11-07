package Task;
public class SimpleTask extends Task {
    public SimpleTask(String name, String description) {
        super(name, description);
    }
    @Override
    public String toString() {
        return "Simple{" +
                "Id=" + Id +
                ", name='" + name + "' " +
                ", description='" + description + "' " +
                ", status=" + status + "}" +
                System.lineSeparator() ;
    }
}
