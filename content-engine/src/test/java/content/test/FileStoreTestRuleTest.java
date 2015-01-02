package content.test;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.Instant;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.*;

public class FileStoreTestRuleTest {

    public static final int EOF = -1;
    @Rule
    public FileStoreTestRule fileStoreTestRule = new FileStoreTestRule();
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final RestTemplate restTemplate = new RestTemplate();
    private byte[] content = "Some content in the file".getBytes(UTF_8);
    private String path;

    @Test
    public void should_make_server_connection_available() throws Exception {
        assertTrue(fileStoreTestRule.getServerConnection().contains("http://localhost:"));
    }

    @Test
    public void should_make_added_file_available() throws Exception {
        path = "/templates/path/a-file";
        fileStoreTestRule.addFile(path, content);

        byte[] result = get(path).getBody();

        assertArrayEquals(content, result);
    }

    @Test
    public void should_return_404_for_file_that_was_not_added() throws Exception {
        expectedException.expect(httpError(withStatusCode(HttpStatus.NOT_FOUND)));
        get("/no/file");
    }

    @Test
    public void should_return_404_for_removed_file() throws Exception {
        path = "/here/temporarily";
        fileStoreTestRule.addFile(path, content);

        assertArrayEquals(content, getObject(path));

        fileStoreTestRule.deleteFile(path);

        expectedException.expect(httpError(withStatusCode(HttpStatus.NOT_FOUND)));
        get(path);
    }

    @Test
    public void should_set_last_modified() throws Exception {
        path = "/templates/updating";
        Instant lastModified = Instant.parse("2013-04-05T13:58:30Z");
        fileStoreTestRule.addFile(path, content, lastModified);

        ResponseEntity<byte[]> responseEntity = get(path);

        assertEquals(lastModified.toEpochMilli(), responseEntity.getHeaders().getLastModified());
    }

    @Test
    public void should_be_able_to_replace_file() throws Exception {
        path = "/to/be/replaced";

        fileStoreTestRule.addFile(path, "First content".getBytes(), Instant.parse("2014-04-01T12:31:00Z"));

        Instant lastModified = Instant.parse("2015-04-03T11:43:00Z");
        fileStoreTestRule.addFile(path, content, lastModified);

        ResponseEntity<byte[]> responseEntity = get(path);

        assertArrayEquals(content, responseEntity.getBody());
        assertEquals(lastModified.toEpochMilli(), responseEntity.getHeaders().getLastModified());
    }

    @Test
    public void should_be_able_to_fake_a_server_error() throws Exception {
        path = "/file/that/stops/working";
        fileStoreTestRule.addFile(path, content);

        assertArrayEquals(content, get(path).getBody());

        fileStoreTestRule.addServerError(path);

        expectedException.expect(httpError(withStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)));
        get(path);
    }

    @Test
    public void should_be_able_to_send_a_redirect() throws Exception {
        path = "/old/path";
        String newPath = "/new/path";

        fileStoreTestRule.addRedirect(path, newPath);
        fileStoreTestRule.addFile(newPath, content);

        ResponseEntity<byte[]> responseEntity = get(path);

        assertArrayEquals(content, responseEntity.getBody());
    }

    @Test
    public void should_respect_ifNotModifiedSince_header() throws Exception {
        path = "/was/not/modified";

        fileStoreTestRule.addFile(path, content, Instant.parse("2014-11-30T14:00:00.0Z"));

        ResponseEntity<byte[]> firstResponse = get("/was/not/modified");
        String lastModified = firstResponse.getHeaders().getFirst(HttpHeaders.LAST_MODIFIED);


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.IF_MODIFIED_SINCE, lastModified);
        HttpEntity<?> request = new HttpEntity<>(httpHeaders);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(fileStoreTestRule.getServerConnection() + path, HttpMethod.GET, request, byte[].class);

        assertEquals(HttpStatus.NOT_MODIFIED, responseEntity.getStatusCode());
    }

    @Test
    public void should_provide_a_statistics_handler() throws Exception {
        assertEquals(0, fileStoreTestRule.statisticsHandler.getRequests());

        fileStoreTestRule.addFile("/some/file", content);
        get("/some/file");

        assertEquals(1, fileStoreTestRule.statisticsHandler.getRequests());
    }

    @Test
    public void should_have_working_statistics() throws Exception {
        assertEquals(0, fileStoreTestRule.statisticsHandler.getResponses2xx());
        fileStoreTestRule.addFile("/some/file", content);
        get("/some/file");
        assertEquals(1, fileStoreTestRule.statisticsHandler.getResponses2xx());
    }

    @Test
    public void should_return_content_as_utf8() throws Exception {
        String text = "Text med åäö";
        fileStoreTestRule.addFile("/some/file", text);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(fileStoreTestRule.getServerConnection() + "/some/file", String.class);
        assertEquals(text, responseEntity.getBody());
    }

    @Test
    public void should_set_correct_content_type() throws Exception {
        fileStoreTestRule.addFile("/some/file", "Text med åäö");

        ResponseEntity<byte[]> responseEntity = get("/some/file");
        MediaType contentType = responseEntity.getHeaders().getContentType();
        assertEquals(MediaType.parseMediaType("text/plain;charset=utf-8"), contentType);
        assertEquals("text", contentType.getType());
        assertEquals("plain", contentType.getSubtype());
        assertEquals(UTF_8, contentType.getCharSet());

        InputStream inputStream = this.getClass().getResourceAsStream("/httpserver/templates/test/path/standard.pdf");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int available;
        byte[] buffer = new byte[256];
        while (EOF != (available = inputStream.read(buffer))) {
            byteArrayOutputStream.write(buffer, 0, available);
        }

        fileStoreTestRule.addFile("/some/pdf/file", byteArrayOutputStream.toByteArray());

        ResponseEntity<byte[]> pdfResponse = get("/some/pdf/file");
        MediaType pdfContentType = pdfResponse.getHeaders().getContentType();
        assertEquals(MediaType.parseMediaType("application/pdf"), pdfContentType);
    }

    private byte[] getObject(String path) {
        return restTemplate.getForObject(fileStoreTestRule.getServerConnection() + path, byte[].class);
    }

    private ResponseEntity<byte[]> get(String path) {
        return restTemplate.getForEntity(fileStoreTestRule.getServerConnection() + path, byte[].class);
    }

    private Matcher<Exception> httpError(Matcher<HttpStatusCodeException> exceptionMatcher) {
        return new TypeSafeMatcher<Exception>() {
            @Override
            protected boolean matchesSafely(Exception item) {
                if (item instanceof HttpStatusCodeException) {
                    return exceptionMatcher.matches(item);
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("an http error ").appendDescriptionOf(exceptionMatcher);
            }
        };
    }

    private Matcher<HttpStatusCodeException> withStatusCode(HttpStatus expected) {
        return new TypeSafeMatcher<HttpStatusCodeException>() {
            @Override
            protected boolean matchesSafely(HttpStatusCodeException item) {
                return expected.equals(item.getStatusCode());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with error code ").appendValue(expected);
            }
        };
    }

}
