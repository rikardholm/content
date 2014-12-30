package content.provisioning.impl;

import content.processing.internal.TemplateProvider;
import content.processing.provisioning.AbstractHttpProvisioningTest;

public class JaxRsTemplateProviderTest extends AbstractHttpProvisioningTest {

    @Override
    protected TemplateProvider<String> createStringProvider(String serverConnection, String rootPath) {
        return new JaxRsTemplateProvider<>(serverConnection, rootPath, String.class);
    }

    @Override
    protected TemplateProvider<byte[]> createByteArrayProvider(String serverConnection, String rootPath) {
        return new JaxRsTemplateProvider<>(serverConnection, rootPath, byte[].class);
    }
}