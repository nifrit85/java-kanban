package task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SubTaskTest extends TaskTest {

    @Test
    void shouldBeParetIdOne() {
        assertEquals(1, subTask.getParentID());
    }

    @Test
    void shouldBeParetIdNull() {
        subTask.clearParent();
        assertNull(subTask.getParentID());
    }

    @Test
    void shouldBeSub() {
        assertEquals(TypeOfTask.SUB, subTask.getTaskType());
    }

}
