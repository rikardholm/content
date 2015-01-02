package content.processing;

import content.test.HttpServerRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class JuelTest {

    private Processor<String> textProcessor;
    private final Map<String, Object> model = new HashMap<>();

    @ClassRule
    public static HttpServerRule httpServerRule = new HttpServerRule();

    @Before
    public void setUp() throws Exception {
        textProcessor = Factory.juelProcessor(httpServerRule.getServerConnection());
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
