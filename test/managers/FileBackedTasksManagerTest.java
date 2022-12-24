package managers;

import constants.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.EpicTask;
import task.SimpleTask;
import task.SubTask;
import test_constants.ConstantsForTests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @Override
    public FileBackedTasksManager getManager() {
        File file = new File(ConstantsForTests.COMMON_FILE);
        File original = new File(ConstantsForTests.COMMON_EXAMPLE);
        file.delete();
        try {
            Files.copy(original.toPath(), file.toPath());
        } catch (IOException e) {
            e.fillInStackTrace();
        }


        return new FileBackedTasksManager(ConstantsForTests.COMMON_FILE);
    }

    @Override
    @BeforeEach
    public void beforeEach() {
        //Получаем новый экземпляр менеджера
        manager = getManager();

        epicTask = (EpicTask) manager.getTaskByIdInternalUse(2);
        subTaskOne = (SubTask) manager.getTaskByIdInternalUse(3);
        subTaskTwo = (SubTask) manager.getTaskByIdInternalUse(4);
        subTaskThree = (SubTask) manager.getTaskByIdInternalUse(5);
        simpleTask = (SimpleTask) manager.getTaskByIdInternalUse(1);
        epicTaskNoSub = (EpicTask) manager.getTaskByIdInternalUse(6);
    }

    @Test
    void shouldBeEmptyTasks() {
        //Загружаем файл, в котором нет тасков, но есть история
        File file = new File(ConstantsForTests.EMPTY_TASK_FILE);
        File original = new File(ConstantsForTests.EMPTY_TASK_EXAMPLE);
        file.delete();
        try {
            Files.copy(original.toPath(), file.toPath());
        } catch (IOException e) {
            e.fillInStackTrace();
        }
        FileBackedTasksManager managerToTest = new FileBackedTasksManager(ConstantsForTests.EMPTY_TASK_FILE);
        //Проверим, что таски не загрузились
        assertTrue(managerToTest.getTasks().isEmpty());
        //Проверим что история не загрузилась, так как нет тасков
        assertTrue(managerToTest.getHistory().isEmpty());

    }

    @Test
    void shouldBeEmptyEpic() {
        //Загружаем файл, в котором у эпика нет подзадач
        File file = new File(ConstantsForTests.NO_SUBTASK_FILE);
        File original = new File(ConstantsForTests.NO_SUBTASK_EXAMPLE);
        file.delete();
        try {
            Files.copy(original.toPath(), file.toPath());
        } catch (IOException e) {
            e.fillInStackTrace();
        }
        FileBackedTasksManager managerToTest = new FileBackedTasksManager(ConstantsForTests.NO_SUBTASK_FILE);

        epicTask = (EpicTask) managerToTest.getTaskById(2);

        Map<Integer, SubTask> subTaskMap = managerToTest.getSubTaskFromEpic(epicTask);
        //Проверим что нет сабов
        assertTrue(subTaskMap.isEmpty());
        //Проверим что есть остальные таски
        assertFalse(managerToTest.getTasks().isEmpty());

    }

    @Test
    void shouldBeEmptyHistory() {
        //Загружаем файл, в котором нет истории
        File file = new File(ConstantsForTests.NO_HISTORY_FILE);
        File original = new File(ConstantsForTests.NO_HISTORY_EXAMPLE);
        file.delete();
        try {
            Files.copy(original.toPath(), file.toPath());
        } catch (IOException e) {
            e.fillInStackTrace();
        }
        FileBackedTasksManager managerToTest = new FileBackedTasksManager(ConstantsForTests.NO_HISTORY_FILE);

        //Проверим, что таски загрузились
        assertFalse(managerToTest.getTasks().isEmpty());
        //Проверим, что история пустая
        assertTrue(managerToTest.getHistory().isEmpty());
    }

    @Test
    void checkReadFromFile() {
        //Тестируем запись в файл
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
        FileBackedTasksManager managerToTest = new FileBackedTasksManager(ConstantsForTests.COMMON_FILE);
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
