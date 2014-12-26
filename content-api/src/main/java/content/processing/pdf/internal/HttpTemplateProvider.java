package content.processing.pdf.internal;

import content.provisioning.TemplateProvisioningException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpTemplateProvider implements TemplateProvider {
    public static final int EOF = -1;
    public static final int BUFFER_SIZE = 4 * 1024
            ;
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

        byte[] content = readByteArray(response);

        return new Template(content);
    }

    private byte[] readByteArray(Response response) {
        ByteArrayOutputStream byteArrayOutputStream;
        try (InputStream inputStream = response.readEntity(InputStream.class)) {
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while (EOF != (read = inputStream.read(buffer))) {
                byteArrayOutputStream.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }
}
