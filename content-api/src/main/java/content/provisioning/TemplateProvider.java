package content.provisioning;

import content.processing.Template;

public interface TemplateProvider {
    Template get(String path);
}
