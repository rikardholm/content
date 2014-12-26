package content.processing.freemarker;

import content.processing.TemplateProvisioningException;
import content.processing.internal.NewTemplate;
import content.processing.internal.TemplateProvider;
import freemarker.cache.TemplateLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class TemplateProviderTemplateLoader implements TemplateLoader {
    private TemplateProvider<String> templateProvider;

    public TemplateProviderTemplateLoader(TemplateProvider<String> templateProvider) {
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
        NewTemplate<String> template = templateProvider.get((String) templateSource);
        return new StringReader(template.content);
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {

    }
}
