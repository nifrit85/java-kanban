import constants.Status;
import constants.TypeOfManager;
import managers.Managers;
import managers.interfaces.TaskManager;
import servers.HttpTaskServer;
import task.EpicTask;
import task.SimpleTask;
import task.SubTask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException {
        //Наиболее простой менеджер, нам только эндпоинты потестить
        TaskManager manager = Managers.getManager(TypeOfManager.HTTP, "http://localhost:8078");
        //Наполняем менеджер, что бы было что считывать
        //Один Симпл
        SimpleTask simpleTaskToAdd = new SimpleTask("NameSimple", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 12, 20, 14, 40, 0), Duration.ofHours(10));
        manager.addTask(simpleTaskToAdd, null);
        //Один Эпик
        EpicTask epicTaskToAdd = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        manager.addTask(epicTaskToAdd, null);
        //Три сабтаска для эпика
        SubTask subTaskToAdd = new SubTask("NameSub1", "DescriptionSub1", Status.NEW, LocalDateTime.of(2022, 12, 11, 14, 40, 0), Duration.ofHours(10));
        manager.addTask(subTaskToAdd, epicTaskToAdd);
        subTaskToAdd = new SubTask("NameSub2", "DescriptionSub2", Status.NEW, LocalDateTime.of(2022, 12, 13, 14, 40, 0), Duration.ofHours(20));
        manager.addTask(subTaskToAdd, epicTaskToAdd);
        subTaskToAdd = new SubTask("NameSub3", "DescriptionSub3", Status.NEW, LocalDateTime.of(2022, 12, 15, 14, 40, 0), Duration.ofHours(30));
        manager.addTask(subTaskToAdd, epicTaskToAdd);
        //Один Эпик без подзадач
        epicTaskToAdd = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        manager.addTask(epicTaskToAdd, null);

        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();
    }
}
