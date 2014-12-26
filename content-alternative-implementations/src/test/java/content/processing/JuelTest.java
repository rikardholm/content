package content.processing;

import content.processing.internal.Template;
import content.processing.internal.provisioning.HttpTemplateProvider;
import content.processing.internal.provisioning.ResponseTransform;
import content.processing.juel.JuelProcessor;
import org.glassfish.grizzly.PortRange;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.assertTrue;

public class JuelTest {

    public static EndToEndTest.CountingHttpProbe countingHttpProbe = new EndToEndTest.CountingHttpProbe();
    private static HttpServer httpServer;

    private Processor<String> textProcessor;
    private final Map<String, Object> model = new HashMap<>();

    @BeforeClass
    public static void startServer() throws IOException {
        httpServer = HttpServer.createSimpleServer(null, new PortRange(6000, 6999));
        CLStaticHttpHandler clStaticHttpHandler = new CLStaticHttpHandler(EndToEndTest.class.getClassLoader(), "/httpserver/");
        clStaticHttpHandler.setFileCacheEnabled(false);
        httpServer.getServerConfiguration()
                .addHttpHandler(clStaticHttpHandler);
        httpServer.getServerConfiguration()
                .getMonitoringConfig()
                .getWebServerConfig()
                .addProbes(countingHttpProbe);
        httpServer.start();
    }

    @Before
    public void setUp() throws Exception {
        Collection<NetworkListener> listeners = httpServer.getListeners();
        NetworkListener networkListener = listeners.stream().findFirst().get();

        String server = "http://" + networkListener.getHost() + ":" + networkListener.getPort();

        textProcessor = new JuelProcessor(new HttpTemplateProvider<>(server, "templates", toAString().andThen(Template::new)));

        countingHttpProbe.clearCounters();
    }

    private Function<Response, String> toAString() {
        return response -> response.readEntity(String.class);
    }


    @AfterClass
    public static void shutdownServer() {
        httpServer.shutdownNow();
    }


    @Test
    public void can_process_a_template() throws Exception {
        model.put("customer", new Person("Rikard"));
        model.put("ceo", new Person("Svante"));

        String content = textProcessor.template("my/path/juel-template").process(model);

        System.out.println(content);

        assertTrue(content.contains("Welcome!"));
    }
}
