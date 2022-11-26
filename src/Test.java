import Manager.TaskManager;
import Task.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Test {
    private final TaskManager manager;

    public Test(TaskManager manager) {
        this.manager = manager;
    }

    public void run() {
        addSimple();
        addEpicWith3Sub();
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

        addEpicWith3Sub();
        show();
        deleteEpicTask(1);
        show();
        manager.clearTasks();
        System.out.println("****************");

        addEpicWith3Sub();
        show();
        deleteSubTask(2);
        show();
        deleteSubTask(3);
        show();
        deleteSubTask(4);
        show();
        manager.clearTasks();
        System.out.println("****************");

        addEpicWith3Sub();
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
            addEpicWith3Sub();
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

    private void addEpicWith3Sub() {

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

    public void runSecondTest() {
        addSimple();
        addSimple();
        addEpicWith3Sub();
        addEpic();
        for (int i = 1; i < 8; i++) {
            System.out.println("get " + i);
            Task task = manager.getTaskById(i);
            System.out.println(manager.getHistory());
        }
        for (int i = 1; i < 9; i +=2) {
            System.out.println("get " + i);
            Task task = manager.getTaskById(i);
            System.out.println(manager.getHistory());
        }
        int id;
        id = 1;
        System.out.println("remove simple " + id);
        manager.deleteTaskByID(id);
        System.out.println(manager.getHistory());
        id = 3;
        System.out.println("remove Epic " + id +" and sub 4,5,6");
        manager.deleteTaskByID(id);
        System.out.println(manager.getHistory());
        id = 7;
        System.out.println("remove Epic " + id);
        manager.deleteTaskByID(id);
        System.out.println(manager.getHistory());
        id = 7;
        System.out.println("remove Epic " + id);
        manager.deleteTaskByID(id);
        System.out.println(manager.getHistory());

        System.out.println("clear all");
        manager.clearTasks();
        addSimple();
        System.out.println("get 1" );
        Task task = manager.getTaskById(1);
        System.out.println(manager.getHistory());
    }

    private void addEpic() {
        String name = "Name";
        String descr = "Descr";
        EpicTask taskEpic = new EpicTask(name, descr);
        manager.addTask(taskEpic, null);
    }

    public void runThirdTest(String pathToFile) {
        Task task;

        showFileContent(pathToFile);
        System.out.println("Добавляем Симпл");
        addSimple();
        showFileContent(pathToFile);
        System.out.println("Добавляем Эпик и 3 Саба к нему");
        addEpicWith3Sub();
        showFileContent(pathToFile);
        System.out.println("Добавляем Симпл");
        addSimple();
        showFileContent(pathToFile);
        System.out.println("Добавляем Эпик и 3 Саба к нему");
        addEpicWith3Sub();
        showFileContent(pathToFile);

        for (int i = 0; i < 4; i++) {
            Random random = new Random();
            int rndInt = random.nextInt(9);
            System.out.println("Читаем:" + rndInt + " таск");

            task = manager.getTaskById(rndInt);
            showFileContent(pathToFile);
        }

        System.out.println("Читаем Эпик 2");
        task = manager.getTaskById(2);
        showFileContent(pathToFile);
        System.out.println("Читаем Саб 3");
        task = manager.getTaskById(3);
        showFileContent(pathToFile);
        System.out.println("Удаляем Эпик 2");
        manager.deleteTaskByID(2);
        showFileContent(pathToFile);
    }

    private void showFileContent(String pathToFile) {
        System.out.println("Содержимое файла:");

        try (BufferedReader fileReader = new BufferedReader(new FileReader(pathToFile))) {
            List<String> content = new ArrayList<>();
            while (fileReader.ready()) {
                System.out.println(fileReader.readLine());
            }
            System.out.println("****Конец Файла****");
        } catch (IOException e) {
            System.err.println("Упс");
        }


    }
}

