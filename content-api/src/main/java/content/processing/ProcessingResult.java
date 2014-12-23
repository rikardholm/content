package content.processing;

import java.io.OutputStream;

public interface ProcessingResult {

    String asString();

    void to(OutputStream outputStream);
}
