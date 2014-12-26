package content.processing.pdf;

import content.processing.internal.HttpTemplateProvider;
import content.processing.internal.ResponseTransform;
import content.processing.internal.Template;
import content.processing.internal.TemplateProvider;
import content.test.HttpServerRule;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.assertTrue;

public class PdfProcessingIntegrationTest {

    private static content.processing.Processor<byte[]> processor;

    @ClassRule
    public static HttpServerRule httpServerRule = new HttpServerRule();

    private final Map<String, Object> model = new HashMap<>();

    @BeforeClass
    public static void createProcessor() {
        Function<Response, Template<byte[]>> transform = ResponseTransform.toByteArray().andThen(Template::new);
        TemplateProvider<byte[]> templateProvider = new HttpTemplateProvider<>(httpServerRule.getServerConnection(), "templates", transform);
        processor = new ITextProcessor(templateProvider);
    }

    @Test
    public void should_fetch_and_process_a_standard_template() throws Exception {
        model.put("name", "Rikard");
        byte[] result = processor.template("test/path/standard.pdf").process(model);

        PDDocument document = PDDocument.load(new ByteArrayInputStream(result));

        String text = new PDFTextStripper().getText(document);

        document.close();

        assertTrue(text.contains("Magic Test Forms"));
    }
}
