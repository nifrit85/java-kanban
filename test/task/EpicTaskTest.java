package task;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTaskTest extends TaskTest {

    @Test
    void shouldBeFourSubTask() {

        SubTask subTaskNew = new SubTask("Name4", "Descr4", Status.NEW, LocalDateTime.of(2022, 11, 18, 14, 40, 00), Duration.ofHours(10));
        manager.addTask(subTaskNew, epicTask);
        assertEquals(4, epicTask.subTaskIDs.size());
    }


    @Test
    void shouldBeTwoSubTask() {

        epicTask.delSubTask(subTask);
        assertEquals(2, epicTask.subTaskIDs.size());
    }

    @Test
    void shouldBeZeroSubTask() {
        epicTask.clearSubTasks();
        assertEquals(0, epicTask.subTaskIDs.size());
    }

    @Test
    void shouldBeArrayOf2_3_4() {
        Integer[] testIdArray = new Integer[]{2, 3, 4};
        assertArrayEquals(testIdArray, epicTask.subTaskIDs.toArray());
    }

    @Test
    void shouldBeEpic() {
        assertEquals(TypeOfTask.EPIC, epicTask.getTaskType());
    }

    @Test
    public void shouldBeStartTime2022_12_11_14_40_00() {
        LocalDateTime correctLocaDateTime = LocalDateTime.of(2022, 12, 11, 14, 40, 00);
        assertEquals(correctLocaDateTime, epicTask.getStartTime());
    }
}
