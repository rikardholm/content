package content.provisioning.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import content.processing.TemplateProvisioningException;
import content.processing.internal.TemplateProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class CachingTemplateProviderWrapper<TEMPLATE> implements TemplateProvider<TEMPLATE> {

    private final LoadingCache<String, TEMPLATE> cache;

    public CachingTemplateProviderWrapper(TemplateProvider<TEMPLATE> templateProvider, Duration cacheDuration) {
        cache = CacheBuilder.newBuilder()
                .refreshAfterWrite(cacheDuration.toMillis(), TimeUnit.MILLISECONDS)
                .build(CacheLoader.from(templateProvider::get));
    }

    @Override
    public TEMPLATE get(String path) {
        try {
            return cache.getUnchecked(path);
        } catch (UncheckedExecutionException e) {
            throw new TemplateProvisioningException(e);
        }
    }
}
