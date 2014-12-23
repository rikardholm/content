package content.provisioning.impl;

import content.processing.Template;
import content.provisioning.TemplateProvider;
import content.provisioning.TemplateProvisioningException;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

public class HttpTemplateProvider implements TemplateProvider {

    private final String server;
    private final String rootPath;

    public HttpTemplateProvider(String server, String rootPath) {
        this.server = server;
        this.rootPath = rootPath;
    }

    @Override
    public Template get(String path) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(server).path(rootPath).path(path);
        Response response = webTarget.request().get();

        if (response.getStatus() != 200) {
            throw new TemplateProvisioningException("Could not get template. " + webTarget.getUri() + " Status: " + response.getStatus() + " " + response.getStatusInfo().getReasonPhrase());
        }


        InputStream inputStream = response.readEntity(InputStream.class);

        byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new Template(bytes);
    }
}
