package content.processing.internal.text;

import com.floreysoft.jmte.Engine;
import content.processing.Processor;
import content.processing.Session;
import content.processing.internal.Template;
import content.processing.internal.TemplateProvider;

import java.util.Map;

public class JmteProcessor implements Processor<String> {

    private TemplateProvider<String> templateProvider;

    public JmteProcessor(TemplateProvider<String> templateProvider) {
        this.templateProvider = templateProvider;
    }

    @Override
    public Session<String> template(String templatePath) {
        Template<String> template = templateProvider.get(templatePath);

        return new JmteSession(template);
    }

    public static class JmteSession implements Session<String> {
        private final Template<String> template;
        private Engine engine = Engine.createCachingEngine();

        public JmteSession(Template<String> template) {
            this.template = template;
        }

        @Override
        public String process(Map<String, Object> model) {
            return engine.transform(template.content, model);
        }
    }
}
