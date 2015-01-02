package content;

import com.floreysoft.jmte.Engine;
import content.test.FileStoreTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class InLineTest {

    @Rule
    public FileStoreTestRule fileStoreTestRule = new FileStoreTestRule();

    private Engine engine = new Engine();
    private RestTemplate restTemplate = new RestTemplate();
    private String templateUrl;

    @Before
    public void add_file_to_store() throws Exception {
        String template = "Hej ${firstname}, \n\n" +
                "Du har ett nytt meddelande när du loggar in.\n\n" +
                "Med vänlig hälsning,\n" +
                "${contact}";

        fileStoreTestRule.addFile("/templates/email/nytt-meddelande", template);

        templateUrl = fileStoreTestRule.getServerConnection() + "/templates/email/nytt-meddelande";
    }

    @Test
    public void fetch_and_process_template() throws Exception {
        String template = restTemplate.getForObject(templateUrl, String.class);

        Map<String, Object> model = new HashMap<>();
        model.put("firstname", "Stellan");
        model.put("contact", "Skorpan");

        String message = engine.transform(template, model);

        System.out.println(message);
    }
}
