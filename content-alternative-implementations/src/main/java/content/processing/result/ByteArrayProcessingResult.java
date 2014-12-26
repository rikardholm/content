package content.processing.result;

import content.processing.ProcessingResult;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static java.util.Objects.requireNonNull;

public class ByteArrayProcessingResult implements ProcessingResult {
    public static final Charset EXPECTED_CHARSET = Charset.forName("UTF-8");
    private byte[] bytes;

    ByteArrayProcessingResult(byte[] bytes) {
        this.bytes = requireNonNull(bytes);
    }

    @Override
    public String asString() {
        return new String(bytes, EXPECTED_CHARSET);
    }

    @Override
    public void to(OutputStream outputStream) {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ProcessingResult from(byte[] bytes) {
        return new ByteArrayProcessingResult(bytes);
    }
}
