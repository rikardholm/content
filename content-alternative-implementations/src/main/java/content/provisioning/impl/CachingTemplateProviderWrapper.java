package content.provisioning.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import content.processing.TemplateProvisioningException;
import content.processing.internal.Template;
import content.processing.internal.TemplateProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class CachingTemplateProviderWrapper<CONTENT> implements TemplateProvider<CONTENT> {

    private final LoadingCache<String, Template<CONTENT>> cache;

    public CachingTemplateProviderWrapper(TemplateProvider<CONTENT> templateProvider, Duration cacheDuration) {
        cache = CacheBuilder.newBuilder()
                .refreshAfterWrite(cacheDuration.toMillis(), TimeUnit.MILLISECONDS)
                .build(CacheLoader.from(templateProvider::get));
    }

    @Override
    public Template<CONTENT> get(String path) {
        try {
            return cache.getUnchecked(path);
        } catch (UncheckedExecutionException e) {
            throw new TemplateProvisioningException(e);
        }
    }
}
