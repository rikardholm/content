package content.processing;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

public interface Session extends Closeable {
    ProcessingResult process(Map<String, Object> model);

    @Override
    void close();
}
