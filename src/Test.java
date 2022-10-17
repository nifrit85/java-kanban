import Manager.TaskManager;
import Task.EpicTask;
import Task.SimpleTask;
import Task.Status;
import Task.SubTask;

public class Test {
    private final TaskManager manager;

    public Test(TaskManager manager) {
        this.manager = manager;
    }

    public void run() {
        addSimple();
        addEpicWithSub();
        show();
        showTask();
        manager.clearTasks();
        System.out.println("****************");

        addSimple();
        show();
        deleteSimpleTask(1);
        show();
        manager.clearTasks();
        System.out.println("****************");

        addEpicWithSub();
        show();
        deleteEpicTask(1);
        show();
        manager.clearTasks();
        System.out.println("****************");

        addEpicWithSub();
        show();
        deleteSubTask(2);
        show();
        deleteSubTask(3);
        show();
        deleteSubTask(4);
        show();
        manager.clearTasks();
        System.out.println("****************");

        addEpicWithSub();
        show();
        setSubStatus(2, Status.IN_PROGRESS);
        show();
        setSubStatus(3, Status.IN_PROGRESS);
        show();
        setSubStatus(4, Status.IN_PROGRESS);
        show();
        setSubStatus(2, Status.DONE);
        show();
        setSubStatus(3, Status.DONE);
        show();
        setSubStatus(4, Status.DONE);
        show();
        setSubStatus(3, Status.NEW);
        show();
        setSubStatus(4, Status.IN_PROGRESS);
        show();
        showSubOfEpic(1);
        manager.clearTasks();
        System.out.println("****************");

        for (int i = 1; i <= 10; i++) {
            addSimple();
            addEpicWithSub();
        }

        for (int i = 1; i <= 15; i++) {
            manager.getTaskById(i);
            System.out.println(manager.getHistory());
            System.out.println("***");
        }
        System.out.println("****************");
    }

    private void addSimple() {
        String name = "Name";
        String descr = "Descr";
        SimpleTask task = new SimpleTask(name, descr);
        manager.addTask(task, null);
    }

    private void addEpicWithSub() {

        String name = "Name";
        String descr = "Descr";
        EpicTask taskEpic = new EpicTask(name, descr);
        manager.addTask(taskEpic, null);
        for (int j = 0; j < 3; j++) {
            name = "Name" + j;
            descr = "Descr" + j;
            SubTask taskSub = new SubTask(name, descr);
            manager.addTask(taskSub, taskEpic);
        }
    }

    private void show() {
        System.out.println(manager.getTasks());
    }

    private void showTask() {
        System.out.println("Чтение");
        for (int i = 1; i < 4; i++) {
            System.out.println(i);
            System.out.println(manager.getTaskById(i));
        }
    }

    private void deleteSimpleTask(int id) {
        System.out.println("Удаление симпла " + id);
        manager.deleteTaskByID(id);
    }

    private void deleteEpicTask(int id) {
        System.out.println("Удаление эпика " + id);
        manager.deleteTaskByID(id);
    }

    private void deleteSubTask(int id) {
        System.out.println("Удаление саба " + id);
        manager.deleteTaskByID(id);
    }

    private void setSubStatus(int id, Status status) {
        System.out.println(id + " -> " + status);
        SubTask sub = (SubTask) manager.getTaskById(id);
        sub.setStatus(status);
        manager.updateTask(sub);
    }

    private void showSubOfEpic(int id) {
        System.out.println("Сабы эпика " + id);
        EpicTask epic = (EpicTask) manager.getTaskById(id);
        System.out.println(manager.getSubTaskFromEpic(epic));
    }
}

