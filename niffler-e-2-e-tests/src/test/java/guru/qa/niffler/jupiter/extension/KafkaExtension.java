package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.service.impl.KafkaConsumerService;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaExtension implements SuiteExtension {

    private static final KafkaConsumerService kafkaService = new KafkaConsumerService();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void beforeSuite(ExtensionContext context) {
        executor.submit(kafkaService);
        executor.shutdown();
    }

    @Override
    public void afterSuite() {
        kafkaService.shutdown();
    }
}
