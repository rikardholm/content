package content.processing;

import content.processing.internal.TemplateProvider;
import content.processing.internal.pdf.ITextProcessor;
import content.processing.internal.provisioning.SpringRestTemplateProvider;
import content.processing.internal.text.JmteProcessor;

import java.util.Objects;

public class ProcessorFactory {

    public static Processor<byte[]> createPdfProcessor(String serverConnection) {
        Objects.requireNonNull(serverConnection);
        TemplateProvider<byte[]> templateProvider = httpTemplateProvider(serverConnection, byte[].class);
        return new ITextProcessor(templateProvider);
    }

    public static Processor<String> createTextProcessor(String serverConnection) {
        Objects.requireNonNull(serverConnection);
        TemplateProvider<String> templateProvider = httpTemplateProvider(serverConnection, String.class);
        return new JmteProcessor(templateProvider);
    }

    private static <T> TemplateProvider<T> httpTemplateProvider(String serverConnection, Class<T> type) {
        return new SpringRestTemplateProvider<>(serverConnection, "templates/", type, 10);
    }
}
