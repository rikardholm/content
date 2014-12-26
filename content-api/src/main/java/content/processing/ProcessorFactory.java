package content.processing;

import content.processing.internal.TemplateProvider;
import content.processing.internal.pdf.ITextProcessor;
import content.processing.internal.provisioning.HttpTemplateProvider;
import content.processing.internal.provisioning.Transform;
import content.processing.internal.text.JmteProcessor;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;

public class ProcessorFactory {

    public static Processor<byte[]> createPdfProcessor(String serverConnection) {
        Objects.requireNonNull(serverConnection);
        TemplateProvider<byte[]> templateProvider = httpTemplateProvider(serverConnection, readEntity(InputStream.class).andThen(Transform::toByteArray));
        return new ITextProcessor(templateProvider);
    }

    public static Processor<String> createTextProcessor(String serverConnection) {
        Objects.requireNonNull(serverConnection);
        TemplateProvider<String> templateProvider = httpTemplateProvider(serverConnection, readEntity(String.class));
        return new JmteProcessor(templateProvider);
    }

    private static <T> TemplateProvider<T> httpTemplateProvider(String serverConnection, Function<Response, T> transformer) {
        return new HttpTemplateProvider<>(serverConnection, "templates", transformer);
    }

    private static <T> Function<Response, T> readEntity(Class<T> type) {
        return response -> response.readEntity(type);
    }
}
