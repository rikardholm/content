package content.processing.internal.provisioning;

import content.processing.TemplateProvisioningException;
import content.processing.internal.Template;
import content.processing.internal.TemplateProvider;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class SpringRestTemplateProvider<CONTENT> implements TemplateProvider<CONTENT> {

    private final String rootUrl;
    private final Class<CONTENT> type;
    private final RestTemplate restTemplate = new RestTemplate();

    public SpringRestTemplateProvider(String serverConnection, String rootPath, Class<CONTENT> type) {
        this.rootUrl = serverConnection + rootPath;
        this.type = type;
    }

    @Override
    public Template<CONTENT> get(String path) {
        CONTENT content;
        try {
            content = restTemplate.getForObject(rootUrl + path, type);
        } catch (RestClientException e) {
            throw new TemplateProvisioningException(e);
        }
        return new Template<>(content);
    }
}
