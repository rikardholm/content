package content.processing.internal;

public interface TemplateProvider<CONTENT> {
    Template<CONTENT> get(String path);
}
