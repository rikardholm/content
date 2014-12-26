package content.processing;

import content.processing.internal.Template;
import content.processing.internal.TemplateProvider;
import content.processing.internal.pdf.ITextProcessor;
import content.processing.internal.provisioning.HttpTemplateProvider;
import content.processing.internal.provisioning.ResponseTransform;
import content.processing.internal.text.JmteProcessor;

import javax.ws.rs.core.Response;
import java.util.function.Function;

public class ProcessorFactory {

    public static Processor<byte[]> createPdfProcessor(String serverConnection) {
        TemplateProvider<byte[]> templateProvider = httpTemplateProvider(serverConnection, ResponseTransform::toByteArray);
        return new ITextProcessor(templateProvider);
    }

    public static Processor<String> createTextProcessor(String serverConnection) {
        TemplateProvider<String> templateProvider = httpTemplateProvider(serverConnection, ResponseTransform::toString);
        return new JmteProcessor(templateProvider);
    }

    private static <T> TemplateProvider<T> httpTemplateProvider(String serverConnection, Function<Response, T> transform) {
        return new HttpTemplateProvider<>(serverConnection, "templates", transform.andThen(Template::new));
    }
}
