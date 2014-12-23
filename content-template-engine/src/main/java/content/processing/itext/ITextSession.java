package content.processing.itext;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import content.processing.ProcessingResult;
import content.processing.Session;
import content.processing.result.ByteArrayProcessingResult;
import content.processing.Template;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class ITextSession implements Session {
    private final Template template;

    public ITextSession(Template template) {
        this.template = template;
    }

    @Override
    public ProcessingResult process(Map<String, Object> model) {
        PdfReader pdfReader;
        try {
            pdfReader = new PdfReader(template.content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PdfStamper pdfStamper;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            pdfStamper = new PdfStamper(pdfReader, byteArrayOutputStream);
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }

        try {
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                pdfStamper.getAcroFields().setField(entry.getKey(), entry.getValue().toString());
            }
        } catch (IOException | DocumentException e) {
            throw new RuntimeException(e);
        }

        try {
            pdfStamper.close();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
        pdfReader.close();

        return ByteArrayProcessingResult.from(byteArrayOutputStream.toByteArray());
    }

    @Override
    public void close() {

    }
}
