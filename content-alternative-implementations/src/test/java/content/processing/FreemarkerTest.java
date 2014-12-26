package content.processing;

import content.test.HttpServerRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FreemarkerTest {

    @ClassRule
    public static HttpServerRule httpServerRule = new HttpServerRule();

    private Processor<String> textProcessor;
    private final Map<String, Object> model = new HashMap<>();


    @Before
    public void setUp() throws Exception {
        textProcessor = Factory.freemarkerProcessor(httpServerRule.getServerConnection());
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

        assertEquals(Integer.valueOf(1), httpServerRule.countingHttpProbe.counters.get("/templates/my/path/no-model.ftl"));
    }
}
