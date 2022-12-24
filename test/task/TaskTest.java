package task;

import constants.Status;
import constants.TypeOfManager;
import managers.Managers;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    protected EpicTask epicTask;
    protected SubTask subTask;
    protected SimpleTask simpleTask;
    protected TaskManager manager = Managers.getManager(TypeOfManager.MEMORY, null);
    @BeforeEach
    public void BeforeEach() {
        //Наполняем менеджер идентичным состоянием перед каждым тестом
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

        //Один Симпл
        SimpleTask simpleTaskToAdd = new SimpleTask("NameSimple", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 12, 20, 14, 40, 0), Duration.ofHours(30));
        manager.addTask(simpleTaskToAdd, null);

        epicTask = (EpicTask) manager.getTaskById(1);
        subTask = (SubTask) manager.getTaskById(2);
        simpleTask = (SimpleTask) manager.getTaskById(5);
    }

    @AfterEach
    public void afterEach() {
        //Очищаем историю и список задач после каждого теста
        manager.clearTasks();
    }


    @Test
    void shouldBeSameDescriptionEpic() {
        //Тестируем коректность получения наименования
        assertEquals("DescriptionEpic", epicTask.getDescription());
    }

    @Test
    void shouldBeId1() {
        //Тестируем получение Id у таска
        assertEquals(1, epicTask.getId());
    }

    @Test
    void shouldBeNewStatus() {
        //Тестируем получение статуса Эпика
        assertEquals(Status.NEW, epicTask.getStatus());
    }

    @Test
    void shouldBeInProgressStatus() {
        //Тестируем получение статуса симпла
        assertEquals(Status.IN_PROGRESS, simpleTask.getStatus());
    }

    @Test
    void shouldBe10HoursDuration() {
        //Тестируем получение длительности таска
        assertEquals(Duration.ofHours(10), subTask.getDuration());
    }

    @Test
    void shouldBeEndTime2022_12_12_00_40_00() {
        //Тестируем расчёт времени окончания таска
        LocalDateTime endTime = LocalDateTime.of(2022, 12, 11, 14, 40, 0).plusHours(10);
        assertEquals(endTime, subTask.getEndTime());
    }

    @Test
    void shouldBeStartTime2022_12_11_14_40_00() {
        //Тестируем получение времени начала таска
        LocalDateTime startTime = LocalDateTime.of(2022, 12, 11, 14, 40, 0);
        assertEquals(startTime, subTask.getStartTime());
    }
}
