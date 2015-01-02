package content.provisioning.impl;

import content.processing.internal.TemplateProvider;
import content.processing.provisioning.AbstractHttpProvisioningTest;
import org.junit.Ignore;

@Ignore
public class JaxRsTemplateProviderTest extends AbstractHttpProvisioningTest {

    @Override
    protected TemplateProvider<String> createStringProvider(String serverConnection, String rootPath) {
        return new JaxRsTemplateProvider<>(serverConnection, rootPath, String.class);
    }

}