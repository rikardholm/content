package content.provisioning.impl;

import content.processing.TemplateProvisioningException;
import content.processing.internal.Template;
import content.processing.internal.TemplateProvider;

import java.io.InputStream;
import java.util.function.Function;

public class ClassPathTemplateProvider<CONTENT> implements TemplateProvider<CONTENT> {
    private final String rootPath;
    private Function<InputStream, CONTENT> transformer;


    public ClassPathTemplateProvider(String rootPath, Function<InputStream, CONTENT> transformer) {
        this.rootPath = rootPath;
        this.transformer = transformer;
    }

    @Override
    public Template<CONTENT> get(String path) {
        String absolutePath = rootPath + path;
        InputStream inputStream = ClassPathTemplateProvider.class.getResourceAsStream(absolutePath);

        if (inputStream == null) {
            throw new TemplateProvisioningException("Template was not found: " + absolutePath);
        }

        return new Template<>(transformer.apply(inputStream));
    }
}


