package content.processing.text.internal;

import javax.ws.rs.core.Response;
import java.util.function.Function;

public class TemplateTransform implements Function<Response, Template> {
    @Override
    public Template apply(Response response) {
        String content = response.readEntity(String.class);
        return new Template(content);
    }
}
