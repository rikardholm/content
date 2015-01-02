package content.processing;

import content.processing.freemarker.FreemarkerProcessor;
import content.processing.internal.TemplateProvider;
import content.provisioning.impl.JaxRsTemplateProvider;
import content.processing.juel.JuelProcessor;
import content.provisioning.impl.CachingTemplateProviderWrapper;

import java.time.Duration;

public class Factory {

    public static Processor<String> juelProcessor(String serverConnection) {
        TemplateProvider<String> templateProvider = httpTemplateProvider(serverConnection);
        return new JuelProcessor(templateProvider);
    }

    public static Processor<String> freemarkerProcessor(String serverConnection) {
        TemplateProvider<String> templateProvider = new CachingTemplateProviderWrapper<>(httpTemplateProvider(serverConnection), Duration.ofDays(500));
        return new FreemarkerProcessor(templateProvider);
    }

    private static JaxRsTemplateProvider<String> httpTemplateProvider(String serverConnection) {
        return new JaxRsTemplateProvider<>(serverConnection, "templates", String.class);
    }
}
