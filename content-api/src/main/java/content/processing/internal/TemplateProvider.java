package content.processing.internal;

public interface TemplateProvider<TEMPLATE> {
    TEMPLATE get(String path);
}
