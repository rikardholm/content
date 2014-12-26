package content.processing.juel;

import content.processing.internal.NewTemplate;
import content.processing.internal.TemplateProvider;
import content.processing.text.Processor;
import de.odysseus.el.util.SimpleContext;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.Map;

public class JuelProcessor implements Processor {

    private TemplateProvider<String> templateProvider;

    public JuelProcessor(TemplateProvider<String> templateProvider) {
        this.templateProvider = templateProvider;
    }

    @Override
    public content.processing.text.Session template(String templatePath) {
        NewTemplate<String> template = templateProvider.get(templatePath);

        return new JuelSession(template);
    }

    private class JuelSession implements content.processing.text.Session {
        private final NewTemplate<String> template;

        public JuelSession(NewTemplate<String> template) {
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
