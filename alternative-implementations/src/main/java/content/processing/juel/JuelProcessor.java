package content.processing.juel;

import content.processing.Processor;
import content.processing.Session;
import content.processing.internal.Template;
import content.processing.internal.TemplateProvider;
import de.odysseus.el.util.SimpleContext;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.Map;

public class JuelProcessor implements Processor<String> {

    private TemplateProvider<String> templateProvider;

    public JuelProcessor(TemplateProvider<String> templateProvider) {
        this.templateProvider = templateProvider;
    }

    @Override
    public Session<String> template(String templatePath) {
        Template<String> template = templateProvider.get(templatePath);

        return new JuelSession(template);
    }

    private class JuelSession implements Session<String> {
        private final Template<String> template;

        public JuelSession(Template<String> template) {
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
