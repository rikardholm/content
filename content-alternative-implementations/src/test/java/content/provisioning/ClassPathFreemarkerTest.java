package content.provisioning;

import content.processing.Person;
import content.processing.Processor;
import content.processing.freemarker.FreemarkerProcessor;
import content.processing.internal.provisioning.Transform;
import content.provisioning.impl.ClassPathTemplateProvider;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertTrue;

public class ClassPathFreemarkerTest {
    private final HashMap<String, Object> model = new HashMap<>();

    @Test
    public void can_parse_a_normal_template() throws Exception {

        Processor<String> processor = new FreemarkerProcessor(new ClassPathTemplateProvider<>("/httpserver/templates/", toByteArray().andThen(bytes -> new String(bytes, UTF_8))));

        model.put("customer", new Person("Arvid"));
        model.put("ceo", new Person("Cesar"));
        String result = processor.template("my/path/welcome-email.ftl").process(model);

        assertTrue(result.contains("Welcome!"));
    }

    private Function<InputStream,byte[]> toByteArray() {
        return Transform::toByteArray;
    }
}
