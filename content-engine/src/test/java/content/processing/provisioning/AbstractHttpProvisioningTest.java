package content.processing.provisioning;

import content.processing.TemplateProvisioningException;
import content.processing.internal.Template;
import content.processing.internal.TemplateProvider;
import content.test.filestore.FileStoreTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

public abstract class AbstractHttpProvisioningTest {

    @Rule
    public FileStoreTestRule fileStore = new FileStoreTestRule();

    private TemplateProvider<String> provider;
    private String content = "Content of template";

    protected abstract TemplateProvider<String> createStringProvider(String serverConnection, String rootPath);

    @Before
    public void setUp() throws Exception {
        provider = createStringProvider(fileStore.getServerConnection(), "templates");
    }

    @Test
    public void should_return_an_existing_template() throws Exception {
        fileStore.addFile("/templates/test/path/template", content.getBytes(UTF_8));
        Template<String> template = provider.get("test/path/template");

        assertEquals(content, template.content);
    }

    @Test
    public void should_use_correct_encoding() throws Exception {
        String content = "Text med å och ä och ö.";
        fileStore.addFile("/templates/correct/encoding/utf-8", content);

        Template<String> template = provider.get("/correct/encoding/utf-8");

        assertEquals(content, template.content);
    }

    @Test(expected = TemplateProvisioningException.class)
    public void should_throw_a_provision_exception_if_template_is_not_found() throws Exception {
        provider.get("test/path/non-existing");
    }

    @Test(expected = TemplateProvisioningException.class)
    public void should_throw_provisioning_exception_on_server_error() throws Exception {
        fileStore.addServerError("/templates/test/path/error");
        provider.get("test/path/error");
    }

    @Test
    public void should_return_cached_template_on_server_error() throws Exception {
        fileStore.addFile("/templates/stops/working", content.getBytes(UTF_8));
        provider.get("stops/working");
        fileStore.addServerError("/templates/stops/working");
        Thread.sleep(20);
        Template<String> template = provider.get("stops/working");

        assertEquals(content, template.content);
    }

    @Test
    public void should_use_ifNotModified_to_update_template() throws Exception {
        fileStore.addFile("/templates/not/modified", content.getBytes(UTF_8));
        provider.get("not/modified");
        long responsesBytesTotal = fileStore.statisticsHandler.getResponsesBytesTotal();
        Thread.sleep(20);
        provider.get("not/modified");
        assertEquals(responsesBytesTotal, fileStore.statisticsHandler.getResponsesBytesTotal());
    }
}
