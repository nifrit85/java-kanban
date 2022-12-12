package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.EpicTask;
import task.SubTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private static final String PATHTOFILE = "./test/resources/test";
    private static final String COPYFROM = "./test/resources/example/test";


    @Override
    public FileBackedTasksManager getManager() {
        File file = new File(PATHTOFILE);
        File original = new File(COPYFROM);
        file.delete();
        try {
            Files.copy(original.toPath(), file.toPath());
        } catch (IOException e) {
            e.fillInStackTrace();
        }


        return new FileBackedTasksManager(PATHTOFILE);
    }

    @Override
    @BeforeEach
    public void beforeEach() {
        manager = getManager();
    }

    @Test
    void shouldBeEmptyTasks() {
        manager.clearTasks();
        File file = new File("./test/resources/empty_task");
        File original = new File("./test/resources/example/empty_task");
        file.delete();
        try {
            Files.copy(original.toPath(), file.toPath());
        } catch (IOException e) {
            e.fillInStackTrace();
        }
        FileBackedTasksManager managerToTest = new FileBackedTasksManager("./test/resources/empty_task");
        assertTrue(managerToTest.getTasks().isEmpty());
        assertTrue(managerToTest.getHistory().isEmpty());

    }

    @Test
    void shouldBeEmptyEpic() {
        manager.clearTasks();
        File file = new File("./test/resources/no_sub");
        File original = new File("./test/resources/example/no_sub");
        file.delete();
        try {
            Files.copy(original.toPath(), file.toPath());
        } catch (IOException e) {
            e.fillInStackTrace();
        }
        FileBackedTasksManager managerToTest = new FileBackedTasksManager("./test/resources/no_sub");

        Map<Integer, SubTask> subTaskMap = managerToTest.getSubTaskFromEpic((EpicTask) managerToTest.getTaskById(2));
        assertTrue(subTaskMap.isEmpty());
        assertFalse(managerToTest.getTasks().isEmpty());

    }

    @Test
    void shouldBeEmptyHistory() {
        manager.clearTasks();
        File file = new File("./test/resources/no_history");
        File original = new File("./test/resources/example/no_history");
        file.delete();
        try {
            Files.copy(original.toPath(), file.toPath());
        } catch (IOException e) {
            e.fillInStackTrace();
        }
        FileBackedTasksManager managerToTest = new FileBackedTasksManager("./test/resources/no_history");

        assertFalse(managerToTest.getTasks().isEmpty());
        assertTrue(managerToTest.getHistory().isEmpty());

    }


}
