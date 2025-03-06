package guru.qa.niffler.utils;

import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.UserdataDbClient;
import org.awaitility.Awaitility;

import java.time.Duration;
import java.util.Objects;

public class AwaitHelper {

    private static final UserdataDbClient userdataDbClient = new UserdataDbClient();

    public static String waitUserFromUserdataDb(UserJson userJson) {
        return Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofSeconds(2))
                .until(() -> userdataDbClient.getUserByName(userJson.username()), Objects::nonNull)
                .username();
    }
}
