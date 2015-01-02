package content.provisioning.impl;

import java.io.*;

public class Transform {
    private static final int EOF = -1;
    private static final int BUFFER_SIZE = 4 * 1024;

    public static byte[] toByteArray(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int available;
        try (InputStream inner = inputStream) {
            while (EOF != (available = inner.read(buffer))) {
                byteArrayOutputStream.write(buffer, 0, available);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }

    public static String toString(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        char[] buffer = new char[BUFFER_SIZE];
        int available;
        StringWriter stringWriter = new StringWriter();
        try {
            while (EOF != (available = inputStreamReader.read(buffer))) {
                stringWriter.write(buffer, 0, available);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stringWriter.toString();
    }

}
