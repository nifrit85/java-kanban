package task;

import constant.Status;
import constant.TypeOfTask;
import exceptions.IntersectionsException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTaskTest extends TaskTest {

    @Test
    void shouldBeFourSubTask() {
        //Тестируем добавление Саба к Эпику
        SubTask subTaskNew = new SubTask("Name4", "Descr4", Status.NEW, LocalDateTime.of(2022, 11, 18, 14, 40, 00), Duration.ofHours(10));
        try {
            manager.addTask(subTaskNew, epicTask);
        }catch (IntersectionsException e){

        }

        assertEquals(4, epicTask.subTaskIDs.size());
    }


    @Test
    void shouldBeTwoSubTask() {
        //Тестируем удаление Саба у Эпика
        epicTask.delSubTask(subTask);
        assertEquals(2, epicTask.subTaskIDs.size());
    }

    @Test
    void shouldBeZeroSubTask() {
        //Тестируем удаление всех Сабов у Эпика
        epicTask.clearSubTasks();
        assertEquals(0, epicTask.subTaskIDs.size());
    }

    @Test
    void shouldBeArrayOf2_3_4() {
        //Тестируем получение списка Сабов у Эпика
        Integer[] testIdArray = new Integer[]{2, 3, 4};
        assertArrayEquals(testIdArray, epicTask.subTaskIDs.toArray());
    }

    @Test
    void shouldBeEpic() {
        //Тестируем получение типа таска
        assertEquals(TypeOfTask.EPIC, epicTask.getTaskType());
    }

    @Test
    void shouldBeEpicStartTimeSameAsSubStartTime() {
        //Тестируем расчёт даты старта Эпика по наименьшей дате старка Саба
        assertEquals(subTask.getStartTime(), epicTask.getStartTime());
    }
}
