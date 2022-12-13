package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.EpicTask;
import task.SimpleTask;
import task.SubTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private static final String COMMON_FILE = "./test/resources/test";
    private static final String EMPTY_TASK_FILE = "./test/resources/empty_task";
    private static final String NO_SUBTASK_FILE = "./test/resources/no_sub";
    private static final String NO_HISTORY_FILE = "./test/resources/no_history";

    private static final String COMMON_EXAMPLE = "./test/resources/example/test";
    private static final String EMPTY_TASK_EXAMPLE = "./test/resources/example/empty_task";
    private static final String NO_SUBTASK_EXAMPLE = "./test/resources/example/no_sub";
    private static final String NO_HISTORY_EXAMPLE = "./test/resources/example/no_history";


    @Override
    public FileBackedTasksManager getManager() {
        File file = new File(COMMON_FILE);
        File original = new File(COMMON_EXAMPLE);
        file.delete();
        try {
            Files.copy(original.toPath(), file.toPath());
        } catch (IOException e) {
            e.fillInStackTrace();
        }


        return new FileBackedTasksManager(COMMON_FILE);
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
        File file = new File(EMPTY_TASK_FILE);
        File original = new File(EMPTY_TASK_EXAMPLE);
        file.delete();
        try {
            Files.copy(original.toPath(), file.toPath());
        } catch (IOException e) {
            e.fillInStackTrace();
        }
        FileBackedTasksManager managerToTest = new FileBackedTasksManager(EMPTY_TASK_FILE);
        //Проверим, что таски не загрузились
        assertTrue(managerToTest.getTasks().isEmpty());
        //Проверим что история не загрузилась, так как нет тасков
        assertTrue(managerToTest.getHistory().isEmpty());

    }

    @Test
    void shouldBeEmptyEpic() {
        //Загружаем файл, в котором у эпика нет подзадач
        File file = new File(NO_SUBTASK_FILE);
        File original = new File(NO_SUBTASK_EXAMPLE);
        file.delete();
        try {
            Files.copy(original.toPath(), file.toPath());
        } catch (IOException e) {
            e.fillInStackTrace();
        }
        FileBackedTasksManager managerToTest = new FileBackedTasksManager(NO_SUBTASK_FILE);

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
        File file = new File(NO_HISTORY_FILE);
        File original = new File(NO_HISTORY_EXAMPLE);
        file.delete();
        try {
            Files.copy(original.toPath(), file.toPath());
        } catch (IOException e) {
            e.fillInStackTrace();
        }
        FileBackedTasksManager managerToTest = new FileBackedTasksManager(NO_HISTORY_FILE);

        //Проверим, что таски загрузились
        assertFalse(managerToTest.getTasks().isEmpty());
        //Проверим, что история пустая
        assertTrue(managerToTest.getHistory().isEmpty());
    }
}
