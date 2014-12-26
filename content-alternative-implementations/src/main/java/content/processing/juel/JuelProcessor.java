package content.processing.juel;

import content.processing.text.Processor;
import content.processing.text.internal.Template;
import content.processing.text.internal.TemplateProvider;
import de.odysseus.el.util.SimpleContext;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.Map;

public class JuelProcessor implements Processor {

    private TemplateProvider templateProvider;

    public JuelProcessor(TemplateProvider templateProvider) {
        this.templateProvider = templateProvider;
    }

    @Override
    public content.processing.text.Session template(String templatePath) {
        Template template = templateProvider.get(templatePath);

        return new JuelSession(template);
    }

    private class JuelSession implements content.processing.text.Session {
        private final Template template;

        public JuelSession(Template template) {
            this.template = template;
        }

        @Override
        public String process(Map<String, Object> model) {
            ExpressionFactory expressionFactory = ExpressionFactory.newInstance();

            ELContext elContext = new SimpleContext();

            for (Map.Entry<String, Object> entry : model.entrySet()) {
                elContext.getVariableMapper().setVariable(entry.getKey(), expressionFactory.createValueExpression(entry.getValue(), entry.getValue().getClass()));
            }

            ValueExpression valueExpression = expressionFactory.createValueExpression(elContext, template.content, String.class);

            return (String) valueExpression.getValue(elContext);
        }
    }
}
