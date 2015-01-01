package content.processing.internal.provisioning;

import content.processing.internal.Template;

class TemplateWithMetaData<CONTENT> extends Template<CONTENT> {
    public final String lastModified;

    public TemplateWithMetaData(CONTENT content, String lastModified) {
        super(content);
        this.lastModified = lastModified;
    }
}
