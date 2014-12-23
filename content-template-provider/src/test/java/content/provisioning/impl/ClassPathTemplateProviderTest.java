package content.provisioning.impl;

import content.processing.Template;
import content.provisioning.TemplateProvider;
import content.provisioning.TemplateProvisioningException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.*;

public class ClassPathTemplateProviderTest {

    private final TemplateProvider templateProvider = new ClassPathTemplateProvider("/content/templates/");

    @Test
    public void provides_the_content_of_a_template_correctly() throws Exception {
        Template template = templateProvider.get("my/path/pdf-template.pdf");

        byte[] fromFile = IOUtils.toByteArray(ClassPathTemplateProviderTest.class.getResourceAsStream("/content/templates/my/path/pdf-template.pdf"));

        byte[] fromTemplate = template.content;

        assertArrayEquals(fromFile, fromTemplate);
    }

    @Test(expected = TemplateProvisioningException.class)
    public void throws_exception_template_does_not_exist() throws Exception {
        templateProvider.get("my/path/non-existing");
    }
}