package content.integrationtest;

import content.processing.TemplateProcessingException;
import content.processing.TemplateProvisioningException;
import content.processing.freemarker.FreemarkerProcessor;
import content.processing.internal.HttpTemplateProvider;
import content.processing.internal.ResponseTransform;
import content.processing.internal.TemplateProvider;
import content.processing.text.Processor;
import content.processing.text.internal.Template;
import content.provisioning.impl.CachingTemplateProviderWrapper;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.PortRange;
import org.glassfish.grizzly.http.server.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EndToEndTest {

    public static CountingHttpProbe countingHttpProbe = new CountingHttpProbe();
    private static HttpServer httpServer;

    private Processor textProcessor;
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

        TemplateProvider<Template> textTemplateProvider = new CachingTemplateProviderWrapper<>(new HttpTemplateProvider<>(server, "templates", ResponseTransform.toAString().andThen(Template::new)), Duration.ofDays(500));
        textProcessor = new FreemarkerProcessor(textTemplateProvider);

        countingHttpProbe.clearCounters();
    }

    @AfterClass
    public static void shutdownServer() {
        httpServer.shutdownNow();
    }

    @Test
    public void can_process_a_freemarker_template() throws Exception {
        model.put("customer", new Person("Rikard"));
        model.put("ceo", new Person("Svante"));

        String content = textProcessor.template("my/path/welcome-email.ftl").process(model);

        System.out.println(content);

        assertTrue(content.contains("Welcome!"));
    }

    @Test(expected = TemplateProvisioningException.class)
    public void should_throw_a_provision_exception_if_template_is_unknown() throws Exception {
        textProcessor.template("my/path/non-existing").process(model);
    }

    @Test(expected = TemplateProcessingException.class)
    public void should_throw_a_processing_exception_if_template_is_corrupted() throws Exception {
        textProcessor.template("my/path/corrupted-template.ftl").process(model);
    }

    @Test
    public void should_cache_freemarker_tamplates_for_a_while() {
        textProcessor.template("my/path/no-model.ftl").process(model);
        textProcessor.template("my/path/no-model.ftl").process(model);
        textProcessor.template("my/path/no-model.ftl").process(model);

        assertEquals(Integer.valueOf(1), countingHttpProbe.counters.get("/templates/my/path/no-model.ftl"));
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
