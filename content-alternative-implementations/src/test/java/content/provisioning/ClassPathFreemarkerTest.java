package content.provisioning;

import content.processing.Person;
import content.processing.Processor;
import content.processing.freemarker.FreemarkerProcessor;
import content.provisioning.impl.ClassPathTemplateProvider;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertTrue;

public class ClassPathFreemarkerTest {
    public static final int BUFFER_SIZE = 4 * 1024;
    public static final int EOF = -1;

    private final HashMap<String, Object> model = new HashMap<>();

    @Test
    public void can_parse_a_normal_template() throws Exception {
        Processor<String> processor = new FreemarkerProcessor(new ClassPathTemplateProvider<>("/httpserver/templates/", this::transform));

        model.put("customer", new Person("Arvid"));
        model.put("ceo", new Person("Cesar"));
        String result = processor.template("my/path/welcome-email.ftl").process(model);

        assertTrue(result.contains("Welcome!"));
    }

    private String transform(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, UTF_8);
        StringWriter stringWriter = new StringWriter();

        char[] buffer = new char[BUFFER_SIZE];
        int read;
        try {
            while (EOF != (read = inputStreamReader.read(buffer))) {
                stringWriter.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stringWriter.toString();
    }
}
