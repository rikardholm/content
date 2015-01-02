package content.processing;

import content.processing.ProcessorFactory;
import content.test.HttpServerRule;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class PdfProcessingIntegrationTest {

    private static content.processing.Processor<byte[]> processor;

    @ClassRule
    public static HttpServerRule httpServerRule = new HttpServerRule();

    private final Map<String, Object> model = new HashMap<>();

    @BeforeClass
    public static void createProcessor() {
        processor = ProcessorFactory.createPdfProcessor(httpServerRule.getServerConnection());
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
