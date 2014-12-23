package content.processing;

import java.util.Map;

public interface Processor {

    ProcessingResult process(String templatePath, Map<String, Object> model);

    Session session(String templatePath);
}
