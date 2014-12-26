package content.processing;

import content.processing.freemarker.FreemarkerProcessor;
import content.processing.internal.Template;
import content.processing.internal.TemplateProvider;
import content.processing.internal.provisioning.HttpTemplateProvider;
import content.processing.juel.JuelProcessor;
import content.provisioning.impl.CachingTemplateProviderWrapper;

import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.function.Function;

public class Factory {

    public static Processor<String> juelProcessor(String serverConnection) {
        TemplateProvider<String> templateProvider = httpTemplateProvider(serverConnection);
        return new JuelProcessor(templateProvider);
    }

    public static Processor<String> freemarkerProcessor(String serverConnection) {
        TemplateProvider<String> templateProvider = new CachingTemplateProviderWrapper<>(httpTemplateProvider(serverConnection), Duration.ofDays(500));
        return new FreemarkerProcessor(templateProvider);
    }

    private static Function<Response, String> toAString() {
        return response -> response.readEntity(String.class);
    }

    private static HttpTemplateProvider<String> httpTemplateProvider(String serverConnection) {
        return new HttpTemplateProvider<>(serverConnection, "templates", toAString().andThen(Template::new));
    }
}
