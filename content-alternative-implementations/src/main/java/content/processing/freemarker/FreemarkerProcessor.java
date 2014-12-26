package content.processing.freemarker;

import content.processing.TemplateProcessingException;
import content.processing.TemplateProvisioningException;
import content.processing.internal.TemplateProvider;
import content.processing.text.Processor;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class FreemarkerProcessor implements Processor {
    private final Configuration configuration;

    public FreemarkerProcessor(TemplateProvider<content.processing.text.internal.Template> templateProvider) {
        configuration = new Configuration(Configuration.VERSION_2_3_21);
        configuration.setTemplateLoader(new TemplateProviderTemplateLoader(templateProvider));
        configuration.setLocalizedLookup(false);
        configuration.setTemplateUpdateDelay(0);
    }

    @Override
    public content.processing.text.Session template(String templatePath) {
        Template fTemplate = getFreemarkerTemplate(templatePath);
        return new IFreemarkerSession(fTemplate);
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

    private class IFreemarkerSession implements content.processing.text.Session {
        private final Template fTemplate;

        public IFreemarkerSession(Template fTemplate) {
            this.fTemplate = fTemplate;
        }

        @Override
        public String process(Map<String, Object> model) {
            StringWriter stringWriter = new StringWriter();

            try {
                fTemplate.process(model, stringWriter);
            } catch (TemplateException | IOException e) {
                throw new RuntimeException(e);
            }

            return stringWriter.toString();
        }
    }
}
