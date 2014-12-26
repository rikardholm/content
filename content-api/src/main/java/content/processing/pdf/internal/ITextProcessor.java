package content.processing.pdf.internal;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import content.processing.internal.NewTemplate;
import content.processing.internal.TemplateProvider;
import content.processing.pdf.Processor;
import content.processing.pdf.Session;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class ITextProcessor implements Processor {
    private final TemplateProvider<NewTemplate<byte[]>> templateProvider;

    public ITextProcessor(TemplateProvider<NewTemplate<byte[]>> templateProvider) {
        this.templateProvider = templateProvider;
    }

    @Override
    public Session template(String path) {
        NewTemplate<byte[]> template = templateProvider.get(path);
        return new ITextSession(template);
    }

    private class ITextSession implements Session {
        private final NewTemplate<byte[]> template;

        public ITextSession(NewTemplate<byte[]> template) {
            this.template = template;
        }

        @Override
        public byte[] process(Map<String, Object> model) {
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

            return byteArrayOutputStream.toByteArray();
        }
    }
}
