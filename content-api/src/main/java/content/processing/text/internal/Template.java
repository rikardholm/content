package content.processing.text.internal;

public class Template {
    public final String content;

    public Template(String content) {
        this.content = content;
    }

    public static interface TemplateProvider {
        Template get(String path);
    }
}
