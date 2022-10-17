import Manager.Managers;
import Manager.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        Test test = new Test(manager);
        test.run();
    }

}
