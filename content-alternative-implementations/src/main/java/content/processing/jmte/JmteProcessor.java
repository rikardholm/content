package content.processing.jmte;

import com.floreysoft.jmte.Engine;
import content.processing.internal.TemplateProvider;
import content.processing.text.Processor;
import content.processing.text.Session;
import content.processing.text.internal.Template;

import java.util.Map;

public class JmteProcessor implements Processor {
    private final TemplateProvider<Template> templateProvider;

    public JmteProcessor(TemplateProvider<Template> templateProvider) {
        this.templateProvider = templateProvider;
    }

    @Override
    public Session template(String templatePath) {
        Template template = templateProvider.get(templatePath);
        return new JmteSession(template);
    }

    public static class JmteSession implements Session {

        private Template template;

        public JmteSession(Template template) {
            this.template = template;
        }

        @Override
        public String process(Map<String, Object> model) {
            Engine engine = new Engine();

            return engine.transform(template.content, model);
        }
    }
}
