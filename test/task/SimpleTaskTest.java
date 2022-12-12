package task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleTaskTest extends TaskTest {
    @Test
    void shouldBeSimple() {
        assertEquals(TypeOfTask.SIMPLE, simpleTask.getTaskType());
    }
}
