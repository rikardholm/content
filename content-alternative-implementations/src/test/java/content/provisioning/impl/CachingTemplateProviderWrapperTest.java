package content.provisioning.impl;

import content.processing.text.internal.Template;
import content.processing.text.internal.TemplateProvider;
import content.provisioning.TemplateProvisioningException;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Duration;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;

public class CachingTemplateProviderWrapperTest {

    private TemplateProvider backingProvider = Mockito.mock(TemplateProvider.class);
    private Template template = new Template("The first template");

    private TemplateProvider target = new CachingTemplateProviderWrapper(backingProvider, Duration.ofMillis(50));

    @Test
    public void should_return_Template_from_backing_service() throws Exception {
        Mockito.when(backingProvider.get("path/to/template")).thenReturn(template);

        Template result = target.get("path/to/template");

        assertEquals(template.content, result.content);
    }

    @Test
    public void should_return_cached_value_if_backing_service_fails() throws Exception {
        Mockito.when(backingProvider.get("path/to/template"))
                .thenReturn(template)
                .thenThrow(TemplateProvisioningException.class);

        target.get("path/to/template");
        Template result = target.get("path/to/template");

        assertEquals(template.content, result.content);
    }

    @Test
    public void should_respond_with_cache_for_some_time() {
        Mockito.when(backingProvider.get("path/to/template")).thenReturn(template);

        target.get("path/to/template");
        target.get("path/to/template");
        target.get("path/to/template");
        target.get("path/to/template");
        target.get("path/to/template");
        target.get("path/to/template");
        target.get("path/to/template");

        Mockito.verify(backingProvider, Mockito.atMost(1)).get("path/to/template");
    }

    @Test(expected = TemplateProvisioningException.class)
    public void should_throw_Exception_if_unable_to_get_Template_from_backing_provider() {
        Mockito.when(backingProvider.get("path/to/template"))
                .thenThrow(TemplateProvisioningException.class);

        target.get("path/to/template");
    }

    @Test
    public void should_flush_cache_after_some_duration() throws Exception {
        Mockito.when(backingProvider.get("path/to/template")).thenReturn(template);
        String newTemplateContent = "The newer template";
        Template newTemplate = new Template(newTemplateContent);

        target.get("path/to/template");
        Mockito.when(backingProvider.get("path/to/template")).thenReturn(newTemplate);

        sleep(100);

        Template result = target.get("path/to/template");

        assertEquals(newTemplateContent, result.content);
    }
}