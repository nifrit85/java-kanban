package managers;

import constant.Status;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.EpicTask;
import task.SimpleTask;
import task.SubTask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    public abstract T getManager();

    private static final int ID_NOT_EXIST = 99;

    protected EpicTask epicTask;
    protected SubTask subTaskOne;
    protected SubTask subTaskTwo;
    protected SubTask subTaskThree;
    protected SimpleTask simpleTask;
    protected EpicTask epicTaskNoSub;

    @BeforeEach
    public void beforeEach() {
        //Получаем нужный менеджер
        manager = getManager();
        //Наполняем менеджер идентичным состоянием перед каждым тестом
        //Один Симпл
        SimpleTask simpleTaskToAdd = new SimpleTask("NameSimple", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 12, 20, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(simpleTaskToAdd, null);
        //Один Эпик
        EpicTask epicTaskToAdd = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        manager.addTask(epicTaskToAdd, null);
        //Три сабтаска для эпика
        SubTask subTaskToAdd = new SubTask("NameSub1", "DescriptionSub1", Status.NEW, LocalDateTime.of(2022, 12, 11, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(subTaskToAdd, epicTaskToAdd);
        subTaskToAdd = new SubTask("NameSub2", "DescriptionSub2", Status.NEW, LocalDateTime.of(2022, 12, 13, 14, 40, 00), Duration.ofHours(20));
        manager.addTask(subTaskToAdd, epicTaskToAdd);
        subTaskToAdd = new SubTask("NameSub3", "DescriptionSub3", Status.NEW, LocalDateTime.of(2022, 12, 15, 14, 40, 00), Duration.ofHours(30));
        manager.addTask(subTaskToAdd, epicTaskToAdd);
        //Один Эпик без подзадач
        epicTaskToAdd = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        manager.addTask(epicTaskToAdd, null);

        epicTask = (EpicTask) manager.getTaskById(2);
        subTaskOne = (SubTask) manager.getTaskById(3);
        subTaskTwo = (SubTask) manager.getTaskById(4);
        subTaskThree = (SubTask) manager.getTaskById(5);
        simpleTask = (SimpleTask) manager.getTaskById(1);
        epicTaskNoSub = (EpicTask) manager.getTaskById(6);

        manager.clearHistory();

        //Наполняем историю 1,4
        Task task = manager.getTaskById(1);
        task = manager.getTaskById(4);
    }

    @AfterEach
    public void AfterEach() {
        //Очищаем историю и список задач после каждого теста
        manager.clearTasks();
    }

    @Test
    public void shouldBeSixTask() {
        //Тестируем получение списка всех тасков
        assertEquals(6, manager.getTasks().size());

        //Тестируем получение списка всех тасков при добавлении неверного таска
        manager.addTask(null, null);
        assertEquals(6, manager.getTasks().size());

        //Тестируем удаление таска (добавили новый, удалили другой)
        SimpleTask NewSimpleTask = new SimpleTask("NameSimple", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 01, 20, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(NewSimpleTask, null);
        manager.deleteTaskByID(simpleTask.getId());
        assertEquals(6, manager.getTasks().size());

        //Тестируем удаление несуществующего таска
        manager.deleteTaskByID(99);
        assertEquals(6, manager.getTasks().size());

        //Тестируем добавление таска с пересечением по времени
        NewSimpleTask = new SimpleTask("NameSimple", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 01, 20, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(NewSimpleTask, null);
        assertEquals(6, manager.getPrioritizedTasks().size());

    }

    @Test
    public void shouldBeZeroTask() {
        //Тестируем очищение всех тасков
        manager.clearTasks();
        assertEquals(0, manager.getTasks().size());

        //Тестируем добавление несуществующих сабов к Эпику
        EpicTask epicTask = new EpicTask("NameEpic", "DescriptionEpic", Status.NEW);
        manager.addTask(epicTask, null);
        manager.addTask(null, epicTask);
        manager.addTask(null, epicTask);
        manager.addTask(null, epicTask);
        assertEquals(0, manager.getSubTaskFromEpic(epicTask).size());

        //Тестируем очистку истории
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    public void shouldBeId2() {
        //Тестируем удалени саба из эпика
        manager.deleteTaskByID(subTaskOne.getId());
        assertEquals(2, manager.getSubTaskFromEpic(epicTask).size());

        //Тестируем получение Эпика по Сабу
        assertEquals(epicTask.getId(), subTaskTwo.getParentID());
    }

    @Test
    public void shouldBeNull() {
        //Тестируем получение несуществующего таска
        assertNull(manager.getTaskById(ID_NOT_EXIST));
    }

    @Test
    public void shouldBeSimple2Simple1() {
        //Тест сортировки по дате начала
        manager.clearTasks();
        //Добавляем 2 таска
        SimpleTask simpleTask1 = new SimpleTask("NameSimple1", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2023, 12, 22, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(simpleTask1, null);

        SimpleTask simpleTask2 = new SimpleTask("NameSimple2", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 12, 21, 15, 40, 00), Duration.ofHours(10));
        manager.addTask(simpleTask2, null);
        //Третий с пересечением по времени
        SimpleTask simpleTaskBad = new SimpleTask("NameSimpleBad", "DescriptionSimple", Status.IN_PROGRESS, LocalDateTime.of(2022, 12, 21, 15, 40, 00), Duration.ofHours(10));
        manager.addTask(simpleTaskBad, null);

        //Проверяем что третий не попал и верный порядок сортировки
        Task[] prioritizedTasksTest = new Task[]{simpleTask2, simpleTask1};
        assertArrayEquals(prioritizedTasksTest, manager.getPrioritizedTasks().toArray());

    }

    @Test
    public void shouldBeThreeTask() {
        //Добавляем  1 саб с пересечением по времени
        SubTask subTaskNew = new SubTask("NameSub3", "DescriptionSub3", Status.NEW, LocalDateTime.of(2022, 12, 15, 14, 40, 00), Duration.ofHours(30));
        manager.addTask(subTaskNew, epicTask);

        //Проверяем что добавилось только он не добавился
        assertEquals(3, manager.getSubTaskFromEpic(epicTask).size());
    }

    @Test
    public void shouldBe4_3_5_1() {
        //Было 1,4
        //Тестируем историю порядок просмотра 1,4,1,3,5,5,1
        Task task = manager.getTaskById(1);
        task = manager.getTaskById(3);
        task = manager.getTaskById(5);
        task = manager.getTaskById(5);
        task = manager.getTaskById(1);

        //Проверяем что очередь выстроилась корректно 4,3,5,1
        List<Task> history = manager.getHistory();
        assertEquals(4, history.get(0).getId());
        assertEquals(3, history.get(1).getId());
        assertEquals(5, history.get(2).getId());
        assertEquals(1, history.get(3).getId());
        //Проверяем что в истории всего 4 значения и нет лишних
        assertEquals(4, history.size());
    }

    @Test
    public void shouldBeNewStatus() {
        //Тестируем, что у Эпика со всеми подзадачами в статусе NEW, так же статус EW
        assertEquals(Status.NEW, epicTask.getStatus());
    }

    @Test
    public void shouldBeInProgressStatus() {
        //Тестируем, что при смене одного статуса саба на DONE, у Эпика будет IN_PROGRESS
        subTaskOne.setStatus(Status.DONE);
        manager.updateTask(subTaskOne);
        assertEquals(Status.IN_PROGRESS, epicTask.getStatus());

        //Тестируем что при установлке всех сабов в статус IN_PROGRESS, Эпик так же будет IN_PROGRESS
        subTaskOne.setStatus(Status.IN_PROGRESS);
        manager.updateTask(subTaskOne);
        subTaskTwo.setStatus(Status.IN_PROGRESS);
        manager.updateTask(subTaskTwo);
        subTaskThree.setStatus(Status.IN_PROGRESS);
        manager.updateTask(subTaskThree);
        assertEquals(Status.IN_PROGRESS, epicTask.getStatus());

    }

    @Test
    public void shouldBeInDoneStatus() {
        //Тестируем что при установке всех сабов в статус IN_PROGRESS, Эпик так же будет IN_PROGRESS
        subTaskOne.setStatus(Status.DONE);
        manager.updateTask(subTaskOne);
        subTaskTwo.setStatus(Status.DONE);
        manager.updateTask(subTaskTwo);
        subTaskThree.setStatus(Status.DONE);
        manager.updateTask(subTaskThree);
        assertEquals(Status.DONE, manager.getTaskById(2).getStatus());
    }

    @Test
    public void shouldBe1_4() {
        //Тестируем удаление последнего элемента из истории
        //Проверяем, что история 1,4
        List<Task> history = manager.getHistory();
        assertEquals(1, history.get(0).getId());
        assertEquals(4, history.get(1).getId());
        assertEquals(2, history.size());

        //1_4_5
        Task task = manager.getTaskById(5);

        manager.deleteTaskByID(5);
        history = manager.getHistory();
        assertEquals(1, history.get(0).getId());
        assertEquals(4, history.get(1).getId());
        assertEquals(2, history.size());
    }

    @Test
    public void shouldBeEmpty() {
        //Проверяем очистку истории при очистке тасков
        manager.clearTasks();
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void shouldBe1_5() {
        //Тестируем удаление из середины истории
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
        //Тестируем удаление первого элемента истории
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

    @Test
    public void ShoudBeTwoTask() {
        //Тестируем удаление сабов при удалении Эпика
        manager.deleteTaskByID(epicTask.getId());
        assertEquals(2, manager.getTasks().size());
    }
}
