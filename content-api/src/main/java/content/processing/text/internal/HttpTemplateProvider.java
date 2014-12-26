package content.processing.text.internal;

import content.processing.TemplateProvisioningException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class HttpTemplateProvider implements TemplateProvider {
    private final String serverConnection;
    private final String rootPath;

    public HttpTemplateProvider(String serverConnection, String rootPath) {
        this.serverConnection = serverConnection;
        this.rootPath = rootPath;
    }

    @Override
    public Template get(String path) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(serverConnection).path(rootPath).path(path);
        Response response = webTarget.request().get();

        if (response.getStatus() != 200) {
            throw new TemplateProvisioningException("Could not fetch template from " + webTarget.getUri() + " Status: " + response.getStatus() + " " + response.getStatusInfo());
        }

        String content = response.readEntity(String.class);

        return new Template(content);
    }
}
