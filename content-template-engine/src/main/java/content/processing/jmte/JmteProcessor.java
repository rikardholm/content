package content.processing.jmte;

import com.floreysoft.jmte.Engine;
import content.processing.ProcessingResult;
import content.processing.Session;
import content.processing.Template;
import content.processing.TextProcessor;
import content.processing.result.ByteArrayProcessingResult;
import content.provisioning.TemplateProvider;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JmteProcessor implements TextProcessor {
    private final TemplateProvider templateProvider;

    public JmteProcessor(TemplateProvider templateProvider) {
        this.templateProvider = templateProvider;
    }

    @Override
    public ProcessingResult process(String templatePath, Map<String, Object> model) {
        Template template = templateProvider.get(templatePath);
        String content = new String(template.content, UTF_8);

        Engine engine = new Engine();
        String result = engine.transform(content, model);

        return ByteArrayProcessingResult.from(result.getBytes(UTF_8));
    }

    @Override
    public Session session(String templatePath) {
        return null;
    }
}
