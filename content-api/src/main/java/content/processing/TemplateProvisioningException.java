package content.processing;

public class TemplateProvisioningException extends RuntimeException {
    public TemplateProvisioningException(String message) {
        super(message);
    }

    public TemplateProvisioningException(Throwable cause) {
        super(cause);
    }

    public TemplateProvisioningException(String message, Throwable cause) {
        super(message, cause);
    }
}
