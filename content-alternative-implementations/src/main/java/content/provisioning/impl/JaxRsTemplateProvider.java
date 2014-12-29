package content.provisioning.impl;

import content.processing.internal.Template;
import content.processing.internal.TemplateProvider;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class JaxRsTemplateProvider<CONTENT> implements TemplateProvider<CONTENT> {
    private final String serverConnection;
    private final String rootPath;
    private final Class<CONTENT> type;

    public JaxRsTemplateProvider(String serverConnection, String rootPath, Class<CONTENT> type) {
        this.serverConnection = serverConnection;
        this.rootPath = rootPath;
        this.type = type;
    }

    @Override
    public Template<CONTENT> get(String path) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(serverConnection).path(rootPath).path(path);
        CONTENT content = webTarget.request().get(type);

        //throw new TemplateProvisioningException("Could not fetch template from " + webTarget.getUri() + " Status: " + response.getStatus() + " " + response.getStatusInfo());

        return new Template<>(content);
    }
}
