package content.processing.simplest;

import content.test.filestore.FileStoreTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SimplestProcessorTest {
    @Rule
    public FileStoreTestRule fileStoreTestRule = new FileStoreTestRule(8080);
    private final SimplestProcessor simplestProcessorImpl = new SimplestProcessorImpl();

    @Before
    public void setUp() throws Exception {
        String template = "Hej ${firstname}, \n\n" +
                "Du har ett nytt meddelande när du loggar in.\n\n" +
                "Med vänlig hälsning,\n" +
                "${contact}";

        fileStoreTestRule.addFile("/templates/email/nytt-meddelande", template);
    }

    @Test
    public void renders_template() throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("firstname", "Stellan");
        model.put("contact", "Skorpan");

        String message = simplestProcessorImpl.process("email/nytt-meddelande", model);

        System.out.println(message);
    }
}