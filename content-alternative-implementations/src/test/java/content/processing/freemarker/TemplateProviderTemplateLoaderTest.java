package content.processing.freemarker;

import content.processing.Template;
import content.provisioning.inmemory.InMemoryTemplateProvider;
import freemarker.template.Configuration;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TemplateProviderTemplateLoaderTest {

    private final InMemoryTemplateProvider inMemoryTemplateProvider = new InMemoryTemplateProvider();
    private final TemplateProviderTemplateLoader templateProviderTemplateLoader = new TemplateProviderTemplateLoader(inMemoryTemplateProvider);
    private Configuration configuration;


    @Before
    public void setUp() throws Exception {
        configuration = new Configuration(Configuration.VERSION_2_3_21);
        configuration.setTemplateLoader(templateProviderTemplateLoader);
    }

    @Test
    public void should_provide_existing_template() throws Exception {
        inMemoryTemplateProvider.put("some/template", new Template("Hello ${name}".getBytes(UTF_8)));

        freemarker.template.Template template = configuration.getTemplate("some/template");

        Map<String, String> model = new HashMap<>();
        model.put("name", "Rikard");

        StringWriter stringWriter = new StringWriter();
        template.process(model, stringWriter);

        assertEquals("Hello Rikard", stringWriter.toString());
    }

    @Test(expected = FileNotFoundException.class)
    public void handles_missing_template() throws Exception {
        configuration.getTemplate("missing/template");
    }
}