package content.processing.internal;

public interface TemplateProvider<CONTENT> {
    NewTemplate<CONTENT> get(String path);
}
