package content.processing.simplest;

import com.floreysoft.jmte.Engine;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class SimplestProcessorImpl implements SimplestProcessor {

    private RestTemplate restTemplate = new RestTemplate();
    private Engine engine = new Engine();

    @Override
    public String process(String templatePath, Map<String, Object> model) {
        String template = restTemplate.getForObject("http://localhost:8080/templates/" + templatePath, String.class);

        return engine.transform(template, model);
    }
}
