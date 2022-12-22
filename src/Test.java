import constant.Status;
import exceptions.IntersectionsException;
import managers.interfaces.TaskManager;
import task.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Test {
    private final TaskManager manager;

    public Test(TaskManager manager) {
        this.manager = manager;
    }

    private void addSimple(){
        SimpleTask task = new SimpleTask("Name", "Descr", Status.NEW, LocalDateTime.now(), Duration.ofDays(1));
        try {
            manager.addTask(task, null);
        }catch (IntersectionsException e){
            System.err.println(e.getMessage());
        }

    }

    private void addEpicWith3Sub(){
        EpicTask epicTask = new EpicTask("Name", "Descr", Status.NEW);
        try {
            manager.addTask(epicTask, null);
        }catch (IntersectionsException e){
            System.err.println(e.getMessage());
        }

        for (int i = 1; i <= 3; i++) {
            SubTask subTask = new SubTask("Name" + i, "Descr" + i, Status.NEW, LocalDateTime.now().plusDays(i), Duration.ofHours(i * 10));
            try {
                manager.addTask(subTask, epicTask);
            }catch (IntersectionsException e){
                System.err.println(e.getMessage());
            }
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
        try {
            manager.updateTask(sub);
        }catch (IntersectionsException e){
            System.err.println(e.getMessage());
        }

    }

    private void showSubOfEpic(int id) {
        System.out.println("Сабы эпика " + id);
        EpicTask epic = (EpicTask) manager.getTaskById(id);
        System.out.println(manager.getSubTaskFromEpic(epic));
    }

    public void run(String pathToFile) {
        Task task;
        showFileContent(pathToFile);
        showPrioritizedTasks();
        System.out.println("Добавляем Симпл");
        addSimple();
        showPrioritizedTasks();
        showFileContent(pathToFile);
        System.out.println("Добавляем Эпик и 3 Саба к нему");
        addEpicWith3Sub();
        showPrioritizedTasks();
        showFileContent(pathToFile);
        EpicTask epicTaskToAdd = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        try {
            manager.addTask(epicTaskToAdd, null);
        }catch (IntersectionsException e){
            System.err.println(e.getMessage());
        }

//        System.out.println("Добавляем Симпл");
//        addSimple();
//        showPrioritizedTasks();
//        showFileContent(pathToFile);
//        System.out.println("Добавляем Эпик и 3 Саба к нему");
//        addEpicWith3Sub();
//        showPrioritizedTasks();
//        showFileContent(pathToFile);
//        System.out.println("У задачи 1 сменил время начала");
//        task = manager.getTaskById(1);
//        task.setStartTime(LocalDateTime.now().plusDays(10));
//        manager.updateTask(task);
//        showPrioritizedTasks();
//        System.out.println("Удаляем саб таск 3 у эпика 2");
//        manager.deleteTaskByID(3);
//        showPrioritizedTasks();
//        showFileContent(pathToFile);
//        System.out.println("Удаляем Эпик 2");
//        manager.deleteTaskByID(2);
//        showPrioritizedTasks();
//        showFileContent(pathToFile);
//        System.out.println("Добаявляем 2 эпика без времени");
//        EpicTask epicTask = new EpicTask("Name", "Descr", Status.NEW);
//        manager.addTask(epicTask, null);
//        epicTask = new EpicTask("Name", "Descr", Status.NEW);
//        manager.addTask(epicTask, null);
//        showPrioritizedTasks();
    }

    private void showFileContent(String pathToFile) {
        System.out.println("Содержимое файла:");

        try (BufferedReader fileReader = new BufferedReader(new FileReader(pathToFile))) {
            while (fileReader.ready()) {
                System.out.println(fileReader.readLine());
            }
            System.out.println("****Конец Файла****");
        } catch (IOException e) {
            System.err.println("Упс");
        }
    }

    private void showPrioritizedTasks() {
        System.out.println("Список отсортированый по приоритету");
        System.out.println(manager.getPrioritizedTasks());
    }
}

