package task;

import managers.Managers;
import managers.TaskManager;
import managers.TypeOfManager;
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
        EpicTask epicTaskToAdd = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        manager.addTask(epicTaskToAdd, null);
        SubTask subTaskToAdd = new SubTask("NameSub1", "DescriptionSub1", Status.NEW, LocalDateTime.of(2022, 12, 11, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(subTaskToAdd, epicTaskToAdd);
        subTaskToAdd = new SubTask("NameSub2", "DescriptionSub2", Status.NEW, LocalDateTime.of(2022, 12, 13, 14, 40, 00), Duration.ofHours(20));
        manager.addTask(subTaskToAdd, epicTaskToAdd);
        subTaskToAdd = new SubTask("NameSub3", "DescriptionSub3", Status.NEW, LocalDateTime.of(2022, 12, 15, 14, 40, 00), Duration.ofHours(30));
        manager.addTask(subTaskToAdd, epicTaskToAdd);
        SimpleTask simpleTaskToAdd = new SimpleTask("NameSimple", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 12, 20, 14, 40, 00), Duration.ofHours(30));
        manager.addTask(simpleTaskToAdd, null);

        epicTask = (EpicTask) manager.getTaskById(1);
        subTask = (SubTask) manager.getTaskById(2);
        simpleTask = (SimpleTask) manager.getTaskById(5);
    }

    @AfterEach
    public void afterEach() {
        manager.clearTasks();
    }


    @Test
    void shouldBeSameDescriptionEpic() {
        assertEquals("DescriptionEpic", epicTask.getDescription());
    }

    @Test
    void shouldBeId1() {
        assertEquals(1, epicTask.getId());
    }

    @Test
    void shouldBeNewStatus() {
        assertEquals(Status.NEW, epicTask.getStatus());
    }

    @Test
    void shouldBeInProgressStatus() {
        assertEquals(Status.IN_PROGRESS, simpleTask.getStatus());
    }

    @Test
    void shouldBe10HoursDuration() {
        assertEquals(Duration.ofHours(10), subTask.getDuration());
    }

    @Test
    void shouldBeEndTime2022_12_12_00_40_00() {
        LocalDateTime endTime = LocalDateTime.of(2022, 12, 11, 14, 40, 00).plusHours(10);
        assertEquals(endTime, subTask.getEndTime());
    }

    @Test
    void shouldBeStartTime2022_12_11_14_40_00() {
        LocalDateTime startTime = LocalDateTime.of(2022, 12, 11, 14, 40, 00);
        assertEquals(startTime, subTask.getStartTime());
    }

}
