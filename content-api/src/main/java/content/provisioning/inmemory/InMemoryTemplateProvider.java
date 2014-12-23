package content.provisioning.inmemory;

import content.processing.Template;
import content.provisioning.TemplateProvider;
import content.provisioning.TemplateProvisioningException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryTemplateProvider implements TemplateProvider {

    private final Map<String, Template> map = new HashMap<>();

    @Override
    public Template get(String path) {
        if (!map.containsKey(path)) {
            throw new TemplateProvisioningException("Unknown template: " + path);
        }

        return map.get(path);
    }

    public void put(String path, Template template) {
        map.put(path, template);
    }
}
