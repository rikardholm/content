package content.processing.text;

import content.processing.TemplateProvisioningException;
import content.processing.internal.HttpTemplateProvider;
import content.processing.internal.ResponseTransform;
import content.processing.internal.Template;
import content.processing.text.internal.JmteProcessor;
import content.test.HttpServerRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class TextProcessingIntegrationTest {

    private static content.processing.Processor<String> processor;

    @ClassRule
    public static HttpServerRule httpServerRule = new HttpServerRule();

    private final Map<String, Object> model = new HashMap<>();

    @BeforeClass
    public static void createProcessor() {
        processor = new JmteProcessor(new HttpTemplateProvider<>(httpServerRule.getServerConnection(), "templates", ResponseTransform.toAString().andThen(Template::new)));
    }

    @Test
    public void should_fetch_and_process_standard_template() throws Exception {
        model.put("customer", new Customer("Pelle", 1357));
        model.put("employee", new Employee("Nisse", "08-356 77 00"));

        String result = processor.template("test/path/standard-template.jmte").process(model);

        assertTrue(result.contains("Ditt konto 1357 har uppdaterats."));
    }

    @Test(expected = TemplateProvisioningException.class)
    public void should_throw_TemplateProvisioningException_if_template_cannot_be_fetched() throws Exception {
        String result = processor.template("test/path/does-not-exist").process(model);

        System.out.println("result = " + result);
    }

    public static class Customer {
        private String firstName;
        private Integer accountNumber;

        public Customer(String firstName, Integer accountNumber) {
            this.firstName = firstName;
            this.accountNumber = accountNumber;
        }

        public String getFirstName() {
            return firstName;
        }

        public Integer getAccountNumber() {
            return accountNumber;
        }
    }

    public static class Employee {
        private String firstName;
        private String phoneNumber;

        public Employee(String firstName, String phoneNumber) {
            this.firstName = firstName;
            this.phoneNumber = phoneNumber;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }
    }
}
