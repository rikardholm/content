package content.provisioning;

import content.processing.Person;
import content.processing.Processor;
import content.processing.freemarker.FreemarkerProcessor;
import content.provisioning.impl.ClassPathTemplateProvider;
import content.provisioning.impl.Transform;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class ClassPathFreemarkerTest {
    private final HashMap<String, Object> model = new HashMap<>();

    @Test
    public void can_parse_a_normal_template() throws Exception {

        Processor<String> processor = new FreemarkerProcessor(new ClassPathTemplateProvider<>("/httpserver/templates/", Transform::toString));

        model.put("customer", new Person("Arvid"));
        model.put("ceo", new Person("Cesar"));
        String result = processor.template("my/path/welcome-email.ftl").process(model);

        assertTrue(result.contains("Welcome!"));
    }
}
