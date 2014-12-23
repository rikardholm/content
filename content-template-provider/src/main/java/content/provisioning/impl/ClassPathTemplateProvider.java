package content.provisioning.impl;

import content.processing.Template;
import content.provisioning.TemplateProvider;
import content.provisioning.TemplateProvisioningException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class ClassPathTemplateProvider implements TemplateProvider {

    private final String rootPath;

    public ClassPathTemplateProvider(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public Template get(String path) {
        String absolutePath = rootPath + path;
        InputStream inputStream = ClassPathTemplateProvider.class.getResourceAsStream(absolutePath);

        if (inputStream == null) {
            throw new TemplateProvisioningException("Template was not found: " + absolutePath);
        }

        byte[] bytes = getBytes(inputStream);

        return new Template(bytes);
    }

    private byte[] getBytes(InputStream inputStream) {
        byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }
}
