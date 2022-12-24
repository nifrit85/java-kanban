package managers;

import constants.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.KVServer;
import task.EpicTask;
import task.SimpleTask;
import task.SubTask;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private KVServer server;

    @Override
    public HttpTaskManager getManager() {

        try {
            return new HttpTaskManager(URI.create("http://localhost:" + KVServer.PORT));
        } catch (InterruptedException | IOException e) {
            e.getMessage();
        }
        return null;
    }

    @Override
    @BeforeEach
    public void beforeEach() {
        try {
            server = new KVServer();
            server.start();
        } catch (IOException e) {
            e.getMessage();
        }
        super.beforeEach();
    }

    @AfterEach
    public void AfterEach() {
        server.stop();
    }

    @Test
    void checkLoadData() throws IOException, InterruptedException {
        //Тестируем запись на сервер
        //Очистим всё
        manager.clearTasks();
        //Убедимся что всё чисто
        assertTrue(manager.getTasks().isEmpty());
        assertTrue(manager.getHistory().isEmpty());
        assertTrue(manager.getPrioritizedTasks().isEmpty());
        //Добавим по одной задаче
        //Один Симпл
        SimpleTask simpleTaskToAdd = new SimpleTask("NameSimple", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 12, 20, 14, 40, 0), Duration.ofHours(10));
        manager.addTask(simpleTaskToAdd, null);

        //Один Эпик
        EpicTask epicTaskToAdd = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        manager.addTask(epicTaskToAdd, null);

        //Один саб для эпика
        SubTask subTaskToAdd = new SubTask("NameSub1", "DescriptionSub1", Status.NEW, LocalDateTime.of(2022, 12, 11, 14, 40, 0), Duration.ofHours(10));
        manager.addTask(subTaskToAdd, epicTaskToAdd);

        //Эпик без саба
        epicTaskToAdd = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        manager.addTask(epicTaskToAdd, null);

        //Наполним историей
        manager.getTaskById(3);
        manager.getTaskById(1);
        manager.getTaskById(2);

        //Создадим новый менеджер из этого файла
        HttpTaskManager managerToTest = new HttpTaskManager(URI.create("http://localhost:" + KVServer.PORT));
        //Сверим содержимое
        //Все задачи
        assertEquals(manager.getTasks().toString(), managerToTest.getTasks().toString());
        //Симплы
        assertEquals(manager.getSimpleTasks().toString(), managerToTest.getSimpleTasks().toString());
        //Епики
        assertEquals(manager.getEpicTasks().toString(), managerToTest.getEpicTasks().toString());
        //Сабы
        assertEquals(manager.getSubTasks().toString(), managerToTest.getSubTasks().toString());
        //История
        assertEquals(manager.getHistory().toString(), managerToTest.getHistory().toString());
        //Приоритеты
        assertEquals(manager.getPrioritizedTasks().toString(), managerToTest.getPrioritizedTasks().toString());
    }
}
