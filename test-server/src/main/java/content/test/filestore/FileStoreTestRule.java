package content.test.filestore;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.junit.rules.ExternalResource;

import java.time.Instant;

public class FileStoreTestRule extends ExternalResource {

    private final FileStoreHandler fileStoreHandler;
    private final Server server;
    public final StatisticsHandler statisticsHandler;

    public FileStoreTestRule() {
        fileStoreHandler = new FileStoreHandler();

        statisticsHandler = new StatisticsHandler();
        statisticsHandler.setHandler(fileStoreHandler);

        server = new Server(0);
        server.setHandler(statisticsHandler);
    }

    @Override
    protected void before() throws Throwable {
        server.start();

    }

    @Override
    protected void after() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFile(String path, String content) {
        fileStoreHandler.add(path, content, Instant.now());
    }

    public void addFile(String path, byte[] content) {
        fileStoreHandler.add(path, content, Instant.now());
    }

    public void addFile(String path, byte[] content, Instant lastModified) {
        fileStoreHandler.add(path, content, lastModified);
    }

    public void deleteFile(String path) {
        fileStoreHandler.delete(path);
    }

    public void addServerError(String path) {
        fileStoreHandler.addError(path);
    }

    public void addRedirect(String path, String newPath) {
        fileStoreHandler.addRedirect(path, newPath);
    }

    public String getServerConnection() {
        ServerConnector serverConnector = (ServerConnector) server.getConnectors()[0];
        return "http://localhost:" + serverConnector.getLocalPort();
    }
}
