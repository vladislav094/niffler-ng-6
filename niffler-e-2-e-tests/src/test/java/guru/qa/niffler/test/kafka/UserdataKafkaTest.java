package guru.qa.niffler.test.kafka;

import guru.qa.niffler.jupiter.annotation.meta.KafkaTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.KafkaProducerService;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.AwaitHelper.waitUserFromUserdataDb;

@KafkaTest
public class UserdataKafkaTest {

    private final KafkaProducerService kafkaService = new KafkaProducerService();

    @Test
    void userdataShouldBeAddedFromKafkaTopicIntoDatabase() {

        final String username = RandomDataUtils.randomUsername();
        final UserJson userJson = new UserJson(username);
        kafkaService.sendMessage("users", userJson);

        Assertions.assertEquals(username, waitUserFromUserdataDb(userJson));
    }
}
