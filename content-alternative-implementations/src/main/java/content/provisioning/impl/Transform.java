package content.provisioning.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Transform {
    private static final int EOF = -1;
    private static final int BUFFER_SIZE = 4 * 1024;

    public static byte[] toByteArray(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        try (InputStream inner = inputStream) {
            while (EOF != (read = inner.read(buffer))) {
                byteArrayOutputStream.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }

}
