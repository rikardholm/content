package content.processing.simplest;

import java.util.Map;

public interface SimplestProcessor {
    String process(String templatePath, Map<String, Object> model);
}
