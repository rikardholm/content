package content.processing.jmte;

import com.floreysoft.jmte.Engine;
import content.processing.internal.NewTemplate;
import content.processing.internal.TemplateProvider;
import content.processing.text.Processor;
import content.processing.text.Session;

import java.util.Map;

public class JmteProcessor implements Processor {
    private final TemplateProvider<String> templateProvider;

    public JmteProcessor(TemplateProvider<String> templateProvider) {
        this.templateProvider = templateProvider;
    }

    @Override
    public Session template(String templatePath) {
        NewTemplate<String> template = templateProvider.get(templatePath);
        return new JmteSession(template);
    }

    public static class JmteSession implements Session {

        private NewTemplate<String> template;

        public JmteSession(NewTemplate<String> template) {
            this.template = template;
        }

        @Override
        public String process(Map<String, Object> model) {
            Engine engine = new Engine();

            return engine.transform(template.content, model);
        }
    }
}
