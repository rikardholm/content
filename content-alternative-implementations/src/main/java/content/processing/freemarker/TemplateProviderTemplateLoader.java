package content.processing.freemarker;

import content.processing.text.internal.Template;
import content.processing.text.internal.TemplateProvider;
import content.provisioning.TemplateProvisioningException;
import freemarker.cache.TemplateLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class TemplateProviderTemplateLoader implements TemplateLoader {
    private TemplateProvider templateProvider;

    public TemplateProviderTemplateLoader(TemplateProvider templateProvider) {
        this.templateProvider = templateProvider;
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        try {
            templateProvider.get(name);
        } catch (TemplateProvisioningException e) {
            return null;
        }

        return name;
    }

    @Override
    public long getLastModified(Object templateSource) {
        return -1;
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        Template template = templateProvider.get((String) templateSource);
        return new StringReader(template.content);
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {

    }
}
