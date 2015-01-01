package content.processing.internal.provisioning;

import content.processing.internal.TemplateProvider;
import content.processing.provisioning.AbstractHttpProvisioningTest;

public class SpringRestTemplateProviderTest extends AbstractHttpProvisioningTest {
    @Override
    protected TemplateProvider<String> createStringProvider(String serverConnection, String rootPath) {
        return new SpringRestTemplateProvider<>(serverConnection, rootPath + "/", String.class, 10);
    }
}