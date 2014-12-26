package content.test;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.PortRange;
import org.glassfish.grizzly.http.server.*;
import org.junit.rules.ExternalResource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HttpServerRule extends ExternalResource {

    private HttpServer httpServer;
    private CountingHttpProbe countingHttpProbe = new CountingHttpProbe();

    public String getServerConnection() {
        return serverConnection;
    }

    private String serverConnection;

    @Override
    protected void before() throws Throwable {
        httpServer = HttpServer.createSimpleServer(null, new PortRange(6000, 7999));
        CLStaticHttpHandler clStaticHttpHandler = new CLStaticHttpHandler(HttpServerRule.class.getClassLoader(), "/httpserver/");
        clStaticHttpHandler.setFileCacheEnabled(false);
        httpServer.getServerConfiguration()
                .addHttpHandler(clStaticHttpHandler);
        httpServer.getServerConfiguration()
                .getMonitoringConfig()
                .getWebServerConfig()
                .addProbes(countingHttpProbe);
        httpServer.start();

        Collection<NetworkListener> listeners = httpServer.getListeners();
        NetworkListener networkListener = listeners.stream().findFirst().get();

        serverConnection = "http://" + networkListener.getHost() + ":" + networkListener.getPort();
    }

    @Override
    protected void after() {
        httpServer.shutdownNow();
    }

    public static class CountingHttpProbe extends HttpServerProbe.Adapter {

        public final Map<String, Integer> counters = new HashMap<>();

        @Override
        public void onRequestReceiveEvent(HttpServerFilter filter, Connection connection, Request request) {
            String uri = request.getRequestURI();
            System.out.println("uri = " + uri);
            Integer count = counters.get(uri);
            if (count == null) {
                count = 0;
            }

            counters.put(uri, count + 1);
        }

        public void clearCounters() {
            for (String uri : counters.keySet()) {
                counters.put(uri, 0);
            }
        }
    }
}
