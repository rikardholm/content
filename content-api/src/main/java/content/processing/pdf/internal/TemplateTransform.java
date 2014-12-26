package content.processing.pdf.internal;

import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

public class TemplateTransform implements Function<Response, Template> {
    private static final int EOF = -1;
    private static final int BUFFER_SIZE = 4 * 1024;

    @Override
    public Template apply(Response response) {
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
