package content.processing.internal.provisioning;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import content.processing.TemplateProvisioningException;
import content.processing.internal.Template;
import content.processing.internal.TemplateProvider;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

public class SpringRestTemplateProvider<CONTENT> implements TemplateProvider<CONTENT> {

    private final String rootUrl;
    private final Class<CONTENT> type;
    private final RestTemplate restTemplate = new RestTemplate();
    private final LoadingCache<String, TemplateWithMetaData<CONTENT>> cache;

    public SpringRestTemplateProvider(String serverConnection, String rootPath, Class<CONTENT> type, int cacheRefreshDuration) {
        this.rootUrl = serverConnection + rootPath;
        this.type = type;
        cache = CacheBuilder.newBuilder()
                .refreshAfterWrite(cacheRefreshDuration, TimeUnit.MILLISECONDS)
                .build(CacheLoader.from(this::loadTemplate));
    }

    @Override
    public Template<CONTENT> get(String path) {
        try {
            TemplateWithMetaData<CONTENT> templateWithMetaData = cache.getUnchecked(path);
            return new Template<>(templateWithMetaData.content);
        } catch (UncheckedExecutionException e) {
            throw new TemplateProvisioningException(e);
        }
    }

    private TemplateWithMetaData<CONTENT> loadTemplate(String path) {
        TemplateWithMetaData<CONTENT> item = cache.getIfPresent(path);
        if (item == null) {
            ResponseEntity<CONTENT> response = request(path);
            return template(response);
        }

        ResponseEntity<CONTENT> response = requestWithIfModifiedSince(path, item.lastModified);

        if (HttpStatus.NOT_MODIFIED == response.getStatusCode()) {
            return item;
        }

        return template(response);
    }

    private ResponseEntity<CONTENT> request(String path) {
        return request(path, HttpEntity.EMPTY);
    }

    private ResponseEntity<CONTENT> requestWithIfModifiedSince(String path, String lastModified) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.IF_MODIFIED_SINCE, lastModified);
        HttpEntity<?> request = new HttpEntity<>(httpHeaders);
        return request(path, request);
    }

    private ResponseEntity<CONTENT> request(String path, HttpEntity<?> request) {
        return restTemplate.exchange(rootUrl + path, HttpMethod.GET, request, type);
    }

    private TemplateWithMetaData<CONTENT> template(ResponseEntity<CONTENT> responseEntity) {
        CONTENT content = responseEntity.getBody();
        String lastModified = responseEntity.getHeaders().getFirst(HttpHeaders.LAST_MODIFIED);
        return new TemplateWithMetaData<>(content, lastModified);
    }

}
