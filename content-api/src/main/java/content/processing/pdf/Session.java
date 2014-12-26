package content.processing.pdf;

import java.util.Map;

public interface Session {
    byte[] process(Map<String, Object> model);
}
