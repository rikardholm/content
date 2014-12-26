package content.processing.internal.provisioning;

import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResponseTransform {
    private static final int EOF = -1;
    private static final int BUFFER_SIZE = 4 * 1024;

    public static String toString(Response response) {
        return response.readEntity(String.class);
    }

    public static byte[] toByteArray(Response response) {
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
