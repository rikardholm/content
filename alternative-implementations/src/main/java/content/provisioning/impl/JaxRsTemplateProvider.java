package content.provisioning.impl;

import content.processing.TemplateProvisioningException;
import content.processing.internal.Template;
import content.processing.internal.TemplateProvider;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class JaxRsTemplateProvider<CONTENT> implements TemplateProvider<CONTENT> {
    private final WebTarget webTarget;
    private final Class<CONTENT> type;

    public JaxRsTemplateProvider(String serverConnection, String rootPath, Class<CONTENT> type) {
        this.type = type;

        Client client = ClientBuilder.newClient();
        webTarget = client.target(serverConnection).path(rootPath);
    }

    @Override
    public Template<CONTENT> get(String path) {
        CONTENT content;
        try {
            content = webTarget.path(path).request().get(type);
        } catch (WebApplicationException e) {
            throw new TemplateProvisioningException(e);
        }

        return new Template<>(content);
    }
}
