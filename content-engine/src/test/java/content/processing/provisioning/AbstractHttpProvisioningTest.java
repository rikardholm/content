package content.processing.provisioning;

import content.processing.TemplateProvisioningException;
import content.processing.internal.TemplateProvider;
import content.test.HttpServerRule;
import org.junit.ClassRule;
import org.junit.Test;

public abstract class AbstractHttpProvisioningTest {

    @ClassRule
    public static HttpServerRule httpServerRule = new HttpServerRule();

    private final TemplateProvider<byte[]> byteArrayProvider = createByteArrayProvider(httpServerRule.getServerConnection(), "templates");
    private final TemplateProvider<String> stringProvider = createStringProvider(httpServerRule.getServerConnection(), "templates");

    protected abstract TemplateProvider<String> createStringProvider(String serverConnection, String rootPath);

    protected abstract TemplateProvider<byte[]> createByteArrayProvider(String serverConnection, String rootPath);

    @Test
    public void can_return_a_string() throws Exception {
        stringProvider.get("test/path/standard-template.jmte");
    }

    @Test
    public void can_return_a_pdf() throws Exception {
        byteArrayProvider.get("test/path/standard.pdf");
    }

    @Test(expected = TemplateProvisioningException.class)
    public void should_throw_a_provision_exception_if_template_is_not_found() throws Exception {
        stringProvider.get("test/path/non-existing");
    }
}
