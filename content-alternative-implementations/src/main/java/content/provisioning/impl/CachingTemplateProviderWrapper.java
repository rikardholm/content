package content.provisioning.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import content.processing.TemplateProvisioningException;
import content.processing.text.internal.Template;
import content.processing.text.internal.TemplateProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class CachingTemplateProviderWrapper implements TemplateProvider {

    private final LoadingCache<String, Template> cache;

    public CachingTemplateProviderWrapper(TemplateProvider templateProvider, Duration cacheDuration) {
        cache = CacheBuilder.newBuilder()
                .refreshAfterWrite(cacheDuration.toMillis(), TimeUnit.MILLISECONDS)
                .build(CacheLoader.from(templateProvider::get));
    }

    @Override
    public Template get(String path) {
        try {
            return cache.getUnchecked(path);
        } catch (UncheckedExecutionException e) {
            throw new TemplateProvisioningException(e);
        }
    }
}
