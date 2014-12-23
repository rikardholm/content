package content.processing.freemarker;

import content.provisioning.TemplateProvider;
import content.provisioning.TemplateProvisioningException;
import freemarker.cache.TemplateLoader;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

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
        char[] chars = decode(templateProvider.get((String) templateSource).content);
        return new CharArrayReader(chars);
    }

    private char[] decode(byte[] bytes) {
        CharBuffer charBuffer = UTF_8.decode(ByteBuffer.wrap(bytes));

        return charBuffer.array();
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {

    }
}
