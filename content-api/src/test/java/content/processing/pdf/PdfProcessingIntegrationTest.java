package content.processing.pdf;

import content.processing.pdf.internal.HttpTemplateProvider;
import content.processing.pdf.internal.ITextProcessor;
import content.test.HttpServerRule;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class PdfProcessingIntegrationTest {

    private static Processor processor;

    @ClassRule
    public static HttpServerRule httpServerRule = new HttpServerRule();

    private final Map<String, Object> model = new HashMap<>();

    @BeforeClass
    public static void createProcessor() {
        processor = new ITextProcessor(new HttpTemplateProvider(httpServerRule.getServerConnection(), "templates"));
    }

    @Test
    public void should_fetch_and_process_a_standard_template() throws Exception {
        byte[] result = processor.template("test/path/standard.pdf").process(model);

        Assert.assertTrue(result.length > 0);
    }
}
