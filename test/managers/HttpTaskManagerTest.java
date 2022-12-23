package managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import servers.KVServer;

import java.io.IOException;
import java.net.URI;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private KVServer server;

    @Override
    public HttpTaskManager getManager() {

        try {
            return new HttpTaskManager(URI.create("http://localhost:" + KVServer.PORT));
        } catch (InterruptedException | IOException e) {
            e.getMessage();
        }
        return null;
    }

    @Override
    @BeforeEach
    public void beforeEach() {
        try {
            server = new KVServer();
            server.start();
        } catch (IOException e) {
            e.getMessage();
        }
        super.beforeEach();
    }

    @AfterEach
    public void AfterEach() {
        server.stop();
    }
}
