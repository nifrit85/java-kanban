package managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    public abstract T getManager();

    private static final int ID_NOT_EXIST = 99;

    @BeforeEach
    public void beforeEach() {
        manager = getManager();
        SimpleTask simpleTaskToAdd = new SimpleTask("NameSimple", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 12, 20, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(simpleTaskToAdd, null);
        EpicTask epicTaskToAdd = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        manager.addTask(epicTaskToAdd, null);
        SubTask subTaskToAdd = new SubTask("NameSub1", "DescriptionSub1", Status.NEW, LocalDateTime.of(2022, 12, 11, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(subTaskToAdd, epicTaskToAdd);
        subTaskToAdd = new SubTask("NameSub2", "DescriptionSub2", Status.NEW, LocalDateTime.of(2022, 12, 13, 14, 40, 00), Duration.ofHours(20));
        manager.addTask(subTaskToAdd, epicTaskToAdd);
        subTaskToAdd = new SubTask("NameSub3", "DescriptionSub3", Status.NEW, LocalDateTime.of(2022, 12, 15, 14, 40, 00), Duration.ofHours(30));
        manager.addTask(subTaskToAdd, epicTaskToAdd);
        epicTaskToAdd = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        manager.addTask(epicTaskToAdd, null);

        Task task = manager.getTaskById(1);
        task = manager.getTaskById(4);

    }

    @AfterEach
    public void AfterEach() {
        manager.clearTasks();
    }

    @Test
    public void shouldBeSixTask() {

        assertEquals(6, manager.getTasks().size());

        manager.addTask(null, null);
        assertEquals(6, manager.getTasks().size());

        SimpleTask simpleTask = new SimpleTask("NameSimple", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 01, 20, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(simpleTask, null);
        manager.deleteTaskByID(1);
        assertEquals(6, manager.getTasks().size());

        manager.deleteTaskByID(99);
        assertEquals(6, manager.getTasks().size());

        simpleTask = new SimpleTask("NameSimple", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 01, 20, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(simpleTask, null);
        assertEquals(6, manager.getPrioritizedTasks().size());

        EpicTask epicTask = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        manager.addTask(epicTask, null);
        SubTask subTask = new SubTask("NameSub1", "DescriptionSub1", Status.NEW, LocalDateTime.of(2022, 12, 11, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(subTask, epicTask);
        subTask = new SubTask("NameSub2", "DescriptionSub2", Status.NEW, LocalDateTime.of(2022, 12, 13, 14, 40, 00), Duration.ofHours(20));
        manager.addTask(subTask, epicTask);
        subTask = new SubTask("NameSub3", "DescriptionSub3", Status.NEW, LocalDateTime.of(2022, 12, 15, 14, 40, 00), Duration.ofHours(30));
        manager.addTask(subTask, epicTask);
        manager.deleteTaskByID(4);
        assertEquals(6, manager.getTasks().size());
    }

    @Test
    public void shouldBeZeroTask() {
        manager.clearTasks();
        assertEquals(0, manager.getTasks().size());

        EpicTask epicTask = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        manager.addTask(epicTask, null);

        manager.addTask(null, epicTask);
        manager.addTask(null, epicTask);
        manager.addTask(null, epicTask);
        assertEquals(0, manager.getSubTaskFromEpic(epicTask).size());

        assertEquals(0, manager.getHistory().size());
    }

    @Test
    public void shouldBeId2() {
        SimpleTask simpleTask = new SimpleTask("NameSimple", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 01, 20, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(simpleTask, null);
        assertEquals(2, manager.getTaskById(2).getId());

        EpicTask epicTask = (EpicTask) manager.getTaskById(2);
        manager.deleteTaskByID(4);
        assertEquals(2, manager.getSubTaskFromEpic(epicTask).size());

        SubTask subTask = (SubTask) manager.getTaskById(5);
        assertEquals(2, subTask.getParentID());
    }

    @Test
    public void shouldBeNull() {
        assertNull(manager.getTaskById(ID_NOT_EXIST));
    }

    @Test
    public void shouldBeSimple2Simple1() {
        manager.clearTasks();

        SimpleTask simpleTask1 = new SimpleTask("NameSimple1", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2023, 12, 22, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(simpleTask1, null);

        SimpleTask simpleTask2 = new SimpleTask("NameSimple2", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 12, 21, 15, 40, 00), Duration.ofHours(10));
        manager.addTask(simpleTask2, null);

        SimpleTask simpleTaskBad = new SimpleTask("NameSimpleBad", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 12, 21, 15, 40, 00), Duration.ofHours(10));
        manager.addTask(simpleTaskBad, null);

        Task[] prioritizedTasksTest = new Task[]{simpleTask2, simpleTask1};

        assertArrayEquals(prioritizedTasksTest, manager.getPrioritizedTasks().toArray());

    }

    @Test
    public void shouldBeThreeTask() {
        manager.clearTasks();
        assertEquals(0, manager.getTasks().size());

        EpicTask epicTask = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        manager.addTask(epicTask, null);
        SubTask subTask = new SubTask("NameSub1", "DescriptionSub1", Status.NEW, LocalDateTime.of(2022, 12, 11, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(subTask, epicTask);
        subTask = new SubTask("NameSub2", "DescriptionSub2", Status.NEW, LocalDateTime.of(2022, 12, 13, 14, 40, 00), Duration.ofHours(20));
        manager.addTask(subTask, epicTask);
        subTask = new SubTask("NameSub3", "DescriptionSub3", Status.NEW, LocalDateTime.of(2022, 12, 15, 14, 40, 00), Duration.ofHours(30));
        manager.addTask(subTask, epicTask);
        subTask = new SubTask("NameSub3", "DescriptionSub3", Status.NEW, LocalDateTime.of(2022, 12, 15, 14, 40, 00), Duration.ofHours(30));
        manager.addTask(subTask, epicTask);

        assertEquals(3, manager.getSubTaskFromEpic(epicTask).size());
        epicTask.clearSubTasks();

    }


    @Test
    public void shouldBeEpic() {

        EpicTask epicTask = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        manager.addTask(epicTask, null);
        SubTask subTask = new SubTask("NameSub1", "DescriptionSub1", Status.NEW, LocalDateTime.of(2022, 12, 11, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(subTask, epicTask);
        subTask = new SubTask("NameSub2", "DescriptionSub2", Status.NEW, LocalDateTime.of(2022, 12, 13, 14, 40, 00), Duration.ofHours(20));
        manager.addTask(subTask, epicTask);
        subTask = new SubTask("NameSub3", "DescriptionSub3", Status.NEW, LocalDateTime.of(2022, 12, 15, 14, 40, 00), Duration.ofHours(30));
        manager.addTask(subTask, epicTask);


        assertEquals(TypeOfTask.EPIC, manager.getTaskById(2).getTaskType());
    }

    @Test
    public void shouldBe4_3_5_1() {
        SimpleTask simpleTask = new SimpleTask("NameSimple", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 01, 20, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(simpleTask, null);

        EpicTask epicTask = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        manager.addTask(epicTask, null);
        SubTask subTask = new SubTask("NameSub1", "DescriptionSub1", Status.NEW, LocalDateTime.of(2022, 12, 11, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(subTask, epicTask);
        subTask = new SubTask("NameSub2", "DescriptionSub2", Status.NEW, LocalDateTime.of(2022, 12, 13, 14, 40, 00), Duration.ofHours(20));
        manager.addTask(subTask, epicTask);
        subTask = new SubTask("NameSub3", "DescriptionSub3", Status.NEW, LocalDateTime.of(2022, 12, 15, 14, 40, 00), Duration.ofHours(30));
        manager.addTask(subTask, epicTask);

        Task task = manager.getTaskById(1);
        task = manager.getTaskById(3);
        task = manager.getTaskById(5);
        task = manager.getTaskById(5);
        task = manager.getTaskById(1);

        List<Task> history = manager.getHistory();
        assertEquals(4, history.get(0).getId());
        assertEquals(3, history.get(1).getId());
        assertEquals(5, history.get(2).getId());
        assertEquals(1, history.get(3).getId());
        assertEquals(4, history.size());
    }

    @Test
    public void shouldBeNewStatus() {

        EpicTask epicTask = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        manager.addTask(epicTask, null);
        SubTask subTask = new SubTask("NameSub1", "DescriptionSub1", Status.NEW, LocalDateTime.of(2022, 12, 11, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(subTask, epicTask);
        subTask = new SubTask("NameSub2", "DescriptionSub2", Status.NEW, LocalDateTime.of(2022, 12, 13, 14, 40, 00), Duration.ofHours(20));
        manager.addTask(subTask, epicTask);
        subTask = new SubTask("NameSub3", "DescriptionSub3", Status.NEW, LocalDateTime.of(2022, 12, 15, 14, 40, 00), Duration.ofHours(30));
        manager.addTask(subTask, epicTask);


        assertEquals(Status.NEW, manager.getTaskById(2).getStatus());
    }

    @Test
    public void shouldBeInProgressStatus() {

        SubTask subTask = (SubTask) manager.getTaskById(3);
        subTask.setStatus(Status.NEW);
        manager.updateTask(subTask);
        subTask = (SubTask) manager.getTaskById(4);
        subTask.setStatus(Status.DONE);
        manager.updateTask(subTask);
        subTask = (SubTask) manager.getTaskById(5);
        subTask.setStatus(Status.NEW);
        manager.updateTask(subTask);

        assertEquals(Status.IN_PROGRESS, manager.getTaskById(2).getStatus());

        subTask = (SubTask) manager.getTaskById(3);
        subTask.setStatus(Status.IN_PROGRESS);
        manager.updateTask(subTask);
        subTask = (SubTask) manager.getTaskById(4);
        subTask.setStatus(Status.IN_PROGRESS);
        manager.updateTask(subTask);
        subTask = (SubTask) manager.getTaskById(5);
        subTask.setStatus(Status.IN_PROGRESS);
        manager.updateTask(subTask);
        assertEquals(Status.IN_PROGRESS, manager.getTaskById(2).getStatus());

    }

    @Test
    public void shouldBeInDoneStatus() {
        SubTask subTask = (SubTask) manager.getTaskById(3);
        subTask.setStatus(Status.DONE);
        manager.updateTask(subTask);
        subTask = (SubTask) manager.getTaskById(4);
        subTask.setStatus(Status.DONE);
        manager.updateTask(subTask);
        subTask = (SubTask) manager.getTaskById(5);
        subTask.setStatus(Status.DONE);
        manager.updateTask(subTask);
        assertEquals(Status.DONE, manager.getTaskById(2).getStatus());
    }

    @Test
    public void shouldBe1_4() {
        List<Task> history = manager.getHistory();
        assertEquals(1, history.get(0).getId());
        assertEquals(4, history.get(1).getId());
        assertEquals(2, history.size());

        //1_4
        Task task = manager.getTaskById(5);
        //1_4_5
        manager.deleteTaskByID(5);
        //1_4
        history = manager.getHistory();
        assertEquals(1, history.get(0).getId());
        assertEquals(4, history.get(1).getId());
        assertEquals(2, history.size());
    }

    @Test
    public void shouldBeEmpty() {
        manager.clearTasks();
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void shouldBe1_5() {
        //1_4
        Task task = manager.getTaskById(5);
        //1_4_5
        manager.deleteTaskByID(4);
        //1_5
        List<Task> history = manager.getHistory();
        assertEquals(1, history.get(0).getId());
        assertEquals(5, history.get(1).getId());
        assertEquals(2, history.size());

    }

    @Test
    public void shouldBe4_5() {
        //1_4
        Task task = manager.getTaskById(5);
        //1_4_5
        manager.deleteTaskByID(1);
        //4_5
        List<Task> history = manager.getHistory();
        assertEquals(4, history.get(0).getId());
        assertEquals(5, history.get(1).getId());
        assertEquals(2, history.size());

    }
}
