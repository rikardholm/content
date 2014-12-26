package content.processing.internal.provisioning;

import content.processing.TemplateProvisioningException;
import content.processing.internal.Template;
import content.processing.internal.TemplateProvider;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.function.Function;

public class HttpTemplateProvider<CONTENT> implements TemplateProvider<CONTENT> {
    private final String serverConnection;
    private final String rootPath;
    private final Function<Response, Template<CONTENT>> transform;

    public HttpTemplateProvider(String serverConnection, String rootPath, Function<Response, Template<CONTENT>> transform) {
        this.serverConnection = serverConnection;
        this.rootPath = rootPath;
        this.transform = transform;
    }

    @Override
    public Template<CONTENT> get(String path) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(serverConnection).path(rootPath).path(path);
        Response response = webTarget.request().get();

        if (response.getStatus() != 200) {
            throw new TemplateProvisioningException("Could not fetch template from " + webTarget.getUri() + " Status: " + response.getStatus() + " " + response.getStatusInfo());
        }

        return transform.apply(response);
    }
}
