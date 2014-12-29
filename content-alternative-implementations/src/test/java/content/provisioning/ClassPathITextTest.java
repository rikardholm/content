package content.provisioning;

import content.processing.Processor;
import content.processing.internal.pdf.ITextProcessor;
import content.provisioning.impl.Transform;
import content.provisioning.impl.ClassPathTemplateProvider;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ClassPathITextTest {

    private final Map<String, Object> model = new HashMap<>();

    @Test
    public void can_parse_a_normal_template() throws Exception {
        Processor<byte[]> processor = new ITextProcessor(new ClassPathTemplateProvider<>("/content/templates/", Transform::toByteArray));

        byte[] result = processor.template("my/path/pdf-template.pdf").process(model);

        PDDocument document = PDDocument.load(new ByteArrayInputStream(result));

        String text = new PDFTextStripper().getText(document);

        document.close();

        assertTrue(text.contains("Magic Test Forms"));
    }
}
