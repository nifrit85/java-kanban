package task;

import constants.TypeOfTask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleTaskTest extends TaskTest {
    @Test
    void shouldBeSimple() {
        //Тестируем получение типа таска
        assertEquals(TypeOfTask.SIMPLE, simpleTask.getTaskType());
    }
}
