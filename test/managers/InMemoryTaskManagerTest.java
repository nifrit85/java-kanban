package managers;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public InMemoryTaskManager getManager() {
        return new InMemoryTaskManager();
    }
}
