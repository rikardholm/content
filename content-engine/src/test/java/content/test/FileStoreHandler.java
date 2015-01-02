package content.test;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileStoreHandler extends AbstractHandler {
    private Map<String, Resource> files = new HashMap<>();

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String path = request.getRequestURI();

        Resource resource = files.get(path);
        if (resource == null) {
            response.sendError(404, "File not found");
            baseRequest.setHandled(true);
            return;
        }

        if (resource instanceof Error) {
            response.sendError(500, "Fake internal server error");
            baseRequest.setHandled(true);
            return;
        }

        if (resource instanceof Redirect) {
            Redirect redirect = (Redirect) resource;
            response.sendRedirect(redirect.newPath);
            baseRequest.setHandled(true);
            return;
        }

        File file = (File) resource;

        long ifModifiedSince = request.getDateHeader("If-Modified-Since");
        if (ifModifiedSince > -1) {
            if (!Instant.ofEpochMilli(ifModifiedSince).isAfter(file.lastModified)) {
                response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
        }

        response.addDateHeader("Last-Modified", file.lastModified.toEpochMilli());
        response.setContentType(file.contentType);
        writeContent(response, file.content);
        baseRequest.setHandled(true);
    }

    public void add(String path, byte[] content, Instant lastModified) {
        MediaType mediaType = detectMediaType(content);

        files.put(path, new File(lastModified, content, mediaType.toString()));
    }

    public void add(String path, String content, Instant lastModified) {
        files.put(path, new File(lastModified, content.getBytes(UTF_8), "text/plain;charset=utf-8"));
    }

    public void delete(String path) {
        files.remove(path);
    }

    public void addError(String path) {
        files.put(path, new Error());
    }

    public void addRedirect(String path, String newPath) {
        files.put(path, new Redirect(newPath));
    }

    private void writeContent(HttpServletResponse response, byte[] content) throws IOException {
        try (OutputStream outputStream = response.getOutputStream()) {
            outputStream.write(content);
            outputStream.flush();
        }
    }

    private MediaType detectMediaType(byte[] content) {
        MediaType mediaType;
        try {
            TikaConfig tikaConfig;
            tikaConfig = new TikaConfig();
            mediaType = tikaConfig.getDetector().detect(TikaInputStream.get(content), new Metadata());
        } catch (TikaException | IOException e) {
            throw new RuntimeException(e);
        }
        return mediaType;
    }

    private class File extends Resource {
        public final Instant lastModified;
        public final byte[] content;
        public final String contentType;

        public File(Instant lastModified, byte[] content, String contentType) {
            this.lastModified = lastModified;
            this.content = content;
            this.contentType = contentType;
        }
    }

    private class Resource {
    }

    private class Error extends Resource {
    }

    private class Redirect extends Resource {
        public final String newPath;

        public Redirect(String newPath) {
            this.newPath = newPath;
        }
    }
}
