package content.integrationtest;

import content.processing.TemplateProcessingException;
import content.processing.TextProcessor;
import content.processing.freemarker.FreemarkerProcessor;
import content.provisioning.TemplateProvider;
import content.provisioning.TemplateProvisioningException;
import content.provisioning.impl.HttpTemplateProvider;
import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.ConnectionProbe;
import org.glassfish.grizzly.PortRange;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.Filter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.http.server.*;
import org.junit.*;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class EndToEndTest {

    private static HttpServer httpServer;

    private TextProcessor textProcessor;
    private final Map<String, Object> model = new HashMap<>();

    @BeforeClass
    public static void startServer() throws IOException {
        httpServer = HttpServer.createSimpleServer(null, new PortRange(6000, 6999));
        httpServer.getServerConfiguration()
                .addHttpHandler(new CLStaticHttpHandler(EndToEndTest.class.getClassLoader(), "/httpserver/"));
        httpServer.getServerConfiguration()
                .getMonitoringConfig()
                .getWebServerConfig()
                .addProbes(new CountingHttpProbe());
        httpServer.start();


        Collection<NetworkListener> listeners = httpServer.getListeners();
        NetworkListener networkListener = listeners.stream().findFirst().get();

        String server = "http://" + networkListener.getHost() + ":" + networkListener.getPort();
    }

    @Before
    public void setUp() throws Exception {
        Collection<NetworkListener> listeners = httpServer.getListeners();
        NetworkListener networkListener = listeners.stream().findFirst().get();

        String server = "http://" + networkListener.getHost() + ":" + networkListener.getPort();

        TemplateProvider templateProvider = new HttpTemplateProvider(server, "templates/");

        textProcessor = new FreemarkerProcessor(templateProvider);
    }

    @After
    public void tearDown() throws Exception {


    }

    @AfterClass
    public static void shutdownServer() {
        httpServer.shutdownNow();
    }

    @Test
    public void can_process_a_freemarker_template() throws Exception {
        model.put("customer", new Person("Rikard"));
        model.put("ceo", new Person("Svante"));

        String content = textProcessor.process("my/path/welcome-email.ftl", model).asString();

        System.out.println(content);

        assertTrue(content.contains("Welcome!"));
    }

    @Test(expected = TemplateProvisioningException.class)
    public void should_throw_a_provision_exception_if_template_is_unknown() throws Exception {
        textProcessor.process("my/path/non-existing", model);
    }

    @Test(expected = TemplateProcessingException.class)
    public void should_throw_a_processing_exception_if_template_is_corrupted() throws Exception {
        textProcessor.process("my/path/corrupted-template.ftl", model);
    }

    public static class CountingHttpProbe extends HttpServerProbe.Adapter {
        @Override
        public void onRequestReceiveEvent(HttpServerFilter filter, Connection connection, Request request) {
            System.out.println("uri = " + request.getRequestURI());
            System.out.println("request.getRequestURL() = " + request.getRequestURL());
            super.onRequestReceiveEvent(filter, connection, request);
        }
    }


}
