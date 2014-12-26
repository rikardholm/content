package content.processing.internal.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import content.processing.Processor;
import content.processing.Session;
import content.processing.TemplateProcessingException;
import content.processing.internal.Template;
import content.processing.internal.TemplateProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class ITextProcessor implements Processor<byte[]> {
    private final TemplateProvider<byte[]> templateProvider;

    public ITextProcessor(TemplateProvider<byte[]> templateProvider) {
        this.templateProvider = Objects.requireNonNull(templateProvider);
    }

    @Override
    public Session<byte[]> template(String path) {
        Objects.requireNonNull(path);
        Template<byte[]> template = templateProvider.get(path);
        return new ITextSession(template);
    }

    private class ITextSession implements Session<byte[]> {
        private final Template<byte[]> template;

        public ITextSession(Template<byte[]> template) {
            this.template = template;
        }

        @Override
        public byte[] process(Map<String, Object> model) {
            Objects.requireNonNull(model);
            PdfReader pdfReader;
            try {
                pdfReader = new PdfReader(template.content);
            } catch (IOException e) {
                throw new TemplateProcessingException(e);
            }

            PdfStamper pdfStamper;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                pdfStamper = new PdfStamper(pdfReader, byteArrayOutputStream);
            } catch (DocumentException | IOException e) {
                throw new TemplateProcessingException(e);
            }

            try {
                for (Map.Entry<String, Object> entry : model.entrySet()) {
                    pdfStamper.getAcroFields().setField(entry.getKey(), entry.getValue().toString());
                }
            } catch (IOException | DocumentException e) {
                throw new TemplateProcessingException(e);
            }
            try {
                pdfStamper.close();
            } catch (DocumentException | IOException e) {
                throw new TemplateProcessingException(e);
            }
            pdfReader.close();

            return byteArrayOutputStream.toByteArray();
        }
    }
}
