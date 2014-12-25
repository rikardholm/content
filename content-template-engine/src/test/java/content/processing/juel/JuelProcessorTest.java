package content.processing.juel;

import content.processing.TextProcessor;
import org.junit.Test;

public class JuelProcessorTest {
    @Test
    public void test_run() throws Exception {
        TextProcessor textProcessor = new JuelProcessor(null);

        textProcessor.process(null, null);

    }
}