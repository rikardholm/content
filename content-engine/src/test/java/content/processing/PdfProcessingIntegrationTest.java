package content.processing;

import content.test.filestore.FileStoreTestRule;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class PdfProcessingIntegrationTest {

    private static content.processing.Processor<byte[]> processor;

    @ClassRule
    public static FileStoreTestRule fileStoreTestRule = new FileStoreTestRule();

    private final Map<String, Object> model = new HashMap<>();

    @BeforeClass
    public static void createProcessor() {
        processor = ProcessorFactory.createPdfProcessor(fileStoreTestRule.getServerConnection());
    }

    @Test
    public void should_fetch_and_process_a_standard_template() throws Exception {
        addFile("/testfiles/standard.pdf", "/templates/test/path/standard.pdf");

        model.put("name", "Rikard");
        byte[] result = processor.template("test/path/standard.pdf").process(model);

        PDDocument document = PDDocument.load(new ByteArrayInputStream(result));

        String text = new PDFTextStripper().getText(document);

        document.close();

        assertTrue(text.contains("Magic Test Forms"));
    }

    private void addFile(String from, String to) {
        InputStream inputStream = getClass().getResourceAsStream(from);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int available;
        byte[] buffer = new byte[512];
        try {
            while (-1 != (available = inputStream.read(buffer))) {
                byteArrayOutputStream.write(buffer, 0, available);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fileStoreTestRule.addFile(to, byteArrayOutputStream.toByteArray());
    }
}
