package content.processing.juel;

import content.processing.ProcessingResult;
import content.processing.Session;
import content.processing.Template;
import content.processing.TextProcessor;
import content.processing.result.ByteArrayProcessingResult;
import content.provisioning.TemplateProvider;
import de.odysseus.el.util.SimpleContext;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JuelProcessor implements TextProcessor {

    private TemplateProvider templateProvider;

    public JuelProcessor(TemplateProvider templateProvider) {
        this.templateProvider = templateProvider;
    }

    @Override
    public ProcessingResult process(String templatePath, Map<String, Object> model) {
        Template template = templateProvider.get(templatePath);
        String content = new String(template.content, UTF_8);

        ExpressionFactory expressionFactory = ExpressionFactory.newInstance();

        ELContext elContext = new SimpleContext();

        for (Map.Entry<String, Object> entry : model.entrySet()) {
            elContext.getVariableMapper().setVariable(entry.getKey(), expressionFactory.createValueExpression(entry.getValue(), entry.getValue().getClass()));
        }

        ValueExpression valueExpression = expressionFactory.createValueExpression(elContext, content, String.class);

        return ByteArrayProcessingResult.from(valueExpression.getValue(elContext).toString().getBytes(UTF_8));
    }

    @Override
    public Session session(String templatePath) {
        return null;
    }
}
