package content.processing.freemarker;

import content.processing.ProcessingResult;
import content.processing.Session;
import content.processing.TextProcessor;
import content.provisioning.TemplateProvider;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;
import java.util.Map;

public class FreemarkerProcessor implements TextProcessor {
    private final Configuration configuration;

    public FreemarkerProcessor(TemplateProvider templateProvider) {
        configuration = new Configuration(Configuration.VERSION_2_3_21);
        configuration.setTemplateLoader(new TemplateProviderTemplateLoader(templateProvider));
        configuration.setLocalizedLookup(false);
    }

    @Override
    public ProcessingResult process(String templatePath, Map<String, Object> model) {
        try (Session session = session(templatePath)) {
            return session.process(model);
        }
    }

    @Override
    public Session session(String templatePath) {
        Template fTemplate = getFreemarkerTemplate(templatePath);

        return new FreemarkerSession(fTemplate);
    }

    private Template getFreemarkerTemplate(String templatePath) {
        try {
            return configuration.getTemplate(templatePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
