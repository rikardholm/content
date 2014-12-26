package content.processing.pdf.internal;

public class Template {
    public final byte[] content;

    public Template(byte[] content) {
        this.content = content;
    }

    public static interface TemplateProvider {
        Template get(String path);
    }
}
