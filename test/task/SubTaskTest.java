package task;

import constants.TypeOfTask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SubTaskTest extends TaskTest {

    @Test
    void shouldBeParetIdOne() {
        //Тестируем получение Эпика для СабТаска
        assertEquals(1, subTask.getParentID());
    }

    @Test
    void shouldBeParetIdNull() {
        //Тестируем метод очистки связи Саба и Эпика
        subTask.clearParent();
        assertNull(subTask.getParentID());
    }

    @Test
    void shouldBeSub() {
        //Тестируем получение типа саба
        assertEquals(TypeOfTask.SUB, subTask.getTaskType());
    }
}
