package content.processing.itext;

import content.processing.PdfProcessor;
import content.processing.Template;
import content.provisioning.inmemory.InMemoryTemplateProvider;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class ITextProcessorTest {
    @Test
    public void processes_template() throws Exception {
        InMemoryTemplateProvider inMemoryTemplateProvider = new InMemoryTemplateProvider();
        PdfProcessor processor = new ITextProcessor(inMemoryTemplateProvider);

        InputStream inputStream = ITextProcessorTest.class.getResourceAsStream("/oo-test.pdf");
        byte[] bytes = IOUtils.toByteArray(inputStream);
        Template template = new Template(bytes);

        inMemoryTemplateProvider.put("my/template", template);

        HashMap<String, Object> model = new HashMap<>();
        model.put("name","Rikard");

        OutputStream outputStream = new FileOutputStream("oo-test-filled.pdf");

        processor.process("my/template", model).to(outputStream);
    }
}