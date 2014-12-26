package content.processing.text;

import java.util.Map;

public interface Session {
    /**
     * @param model variables used in processing
     * @throws content.processing.TemplateProcessingException
     * @return Result of processed template
     */
    String process(Map<String, Object> model);
}
