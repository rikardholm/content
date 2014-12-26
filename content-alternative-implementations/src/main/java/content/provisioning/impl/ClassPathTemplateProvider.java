package content.provisioning.impl;

import content.processing.Template;
import content.provisioning.TemplateProvider;
import content.provisioning.TemplateProvisioningException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ClassPathTemplateProvider implements TemplateProvider {

    public static final int BUFFER_SIZE = 4 * 1024;
    public static final int EOF = -1;
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
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        try {
            while (EOF != (read = inputStream.read(buffer))) {
                byteArrayOutputStream.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }
}
