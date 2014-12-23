package content.processing.freemarker;

import content.processing.ProcessingResult;
import content.processing.Session;
import content.processing.TemplateProcessingException;
import content.processing.TextProcessor;
import content.provisioning.TemplateProvider;
import content.provisioning.TemplateProvisioningException;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.FileNotFoundException;
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
        } catch (FileNotFoundException e) {
            throw new TemplateProvisioningException(e);
        } catch (IOException d) {
            throw new TemplateProcessingException(d);
        }
    }
}
