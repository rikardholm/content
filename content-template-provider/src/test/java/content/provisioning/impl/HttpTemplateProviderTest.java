package content.provisioning.impl;

import content.processing.Template;
import content.provisioning.TemplateProvider;
import content.provisioning.TemplateProvisioningException;
import org.glassfish.grizzly.PortRange;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

public class HttpTemplateProviderTest {

    private HttpServer httpServer;
    private TemplateProvider templateProvider;

    @Before
    public void startServer() throws Exception {
        httpServer = HttpServer.createSimpleServer(null, new PortRange(6000, 6999));
        httpServer.getServerConfiguration()
                .addHttpHandler(new CLStaticHttpHandler(getClass().getClassLoader(), "/httpserver/"));
        httpServer.start();

        Collection<NetworkListener> listeners = httpServer.getListeners();
        NetworkListener networkListener = listeners.stream().findFirst().get();

        String server = "http://"+networkListener.getHost() + ":" + networkListener.getPort();
        System.out.println(server);
        templateProvider = new HttpTemplateProvider(server, "templates/");
    }

    @After
    public void tearDown() throws Exception {
        httpServer.shutdownNow();
    }

    @Test
    public void able_to_fetch_template() throws Exception {
        Template template = templateProvider.get("fm/fm-template.ftl");

        String content = new String(template.content, UTF_8);

        assertEquals("<h2>${title}</h2>", content);
    }

    @Test(expected = TemplateProvisioningException.class)
    public void throws_exception_if_template_was_not_found() throws Exception {
        templateProvider.get("unknown/template.ftl");
    }
}