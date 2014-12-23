package content.processing.freemarker;

import content.processing.ProcessingResult;
import content.processing.Session;
import content.processing.result.ByteArrayProcessingResult;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

public class FreemarkerSession implements Session {
    private final Template template;

    public FreemarkerSession(Template template) {
        this.template = template;
    }

    @Override
    public ProcessingResult process(Map<String, Object> model) {
        byte[] processes = process(model, template);

        return ByteArrayProcessingResult.from(processes);
    }

    @Override
    public void close() {

    }

    private byte[] process(Map<String, Object> model, Template fTemplate) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            fTemplate.process(model, new OutputStreamWriter(byteArrayOutputStream));
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }
}
