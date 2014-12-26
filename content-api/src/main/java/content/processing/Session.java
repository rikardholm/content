package content.processing;

import java.util.Map;

public interface Session<OUTPUT> {
    OUTPUT process(Map<String, Object> model);
}
