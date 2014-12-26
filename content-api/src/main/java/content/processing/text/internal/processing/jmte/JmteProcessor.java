package content.processing.text.internal.processing.jmte;

import com.floreysoft.jmte.Engine;
import content.processing.text.Processor;
import content.processing.text.Session;
import content.processing.text.internal.Template;
import content.processing.text.internal.TemplateProvider;

import java.util.Map;

public class JmteProcessor implements Processor {

    private TemplateProvider templateProvider;

    public JmteProcessor(TemplateProvider templateProvider) {
        this.templateProvider = templateProvider;
    }

    @Override
    public Session template(String templatePath) {
        Template template = templateProvider.get(templatePath);

        return new JmteSession(template);
    }

    public static class JmteSession implements Session {
        private final Template template;
        private Engine engine = Engine.createCachingEngine();

        public JmteSession(Template template) {
            this.template = template;
        }

        @Override
        public String process(Map<String, Object> model) {
            return engine.transform(template.content, model);
        }
    }
}
