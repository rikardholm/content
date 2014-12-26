package content.processing.freemarker;

import content.processing.ProcessingResult;
import content.processing.Session;
import content.processing.Template;
import content.processing.TextProcessor;
import content.provisioning.inmemory.InMemoryTemplateProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class FreemarkerProcessorTest {

    public static final Template TEMPLATE = new Template("<h1>${title}</h1>".getBytes(UTF_8));
    private final InMemoryTemplateProvider templateProvider = new InMemoryTemplateProvider();
    private final TextProcessor target = new FreemarkerProcessor(templateProvider);

    @Before
    public void setUp() throws Exception {
        templateProvider.put("my/path", TEMPLATE);
    }

    @Test
    public void processes_template() throws Exception {
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "My Title åäö");

        String result = target.process("my/path", model).asString();

        assertEquals("<h1>My Title åäö</h1>", result);
    }

    @Test
    public void Session_works() throws Exception {
        Map<String, Object> model = new HashMap<>();
        try (Session session = target.session("my/path")) {
            model.put("title", "title A");
            assertEquals("<h1>title A</h1>", session.process(model).asString());
            model.put("title", "title B");
            assertEquals("<h1>title B</h1>", session.process(model).asString());
            model.put("title", "title C");
            assertEquals("<h1>title C</h1>", session.process(model).asString());
        }
    }
}