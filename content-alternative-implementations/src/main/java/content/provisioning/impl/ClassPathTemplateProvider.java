package content.provisioning.impl;

import content.processing.TemplateProvisioningException;
import content.processing.internal.NewTemplate;
import content.processing.internal.TemplateProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ClassPathTemplateProvider<CONTENT> implements TemplateProvider<CONTENT> {

    public static final int BUFFER_SIZE = 4 * 1024;
    public static final int EOF = -1;
    private final String rootPath;

    public ClassPathTemplateProvider(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public NewTemplate<CONTENT> get(String path) {
        String absolutePath = rootPath + path;
        InputStream inputStream = ClassPathTemplateProvider.class.getResourceAsStream(absolutePath);

        if (inputStream == null) {
            throw new TemplateProvisioningException("Template was not found: " + absolutePath);
        }

        String content = readContent(inputStream);

        return new NewTemplate(content);
    }

    private String readContent(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, UTF_8);
        StringWriter stringWriter = new StringWriter();

        char[] buffer = new char[BUFFER_SIZE];
        int read;
        try {
            while (EOF != (read = inputStreamReader.read(buffer))) {
                stringWriter.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stringWriter.toString();
    }
}
