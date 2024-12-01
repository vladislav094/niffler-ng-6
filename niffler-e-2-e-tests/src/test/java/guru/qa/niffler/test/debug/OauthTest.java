package guru.qa.niffler.test.debug;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotations.ApiLogin;
import guru.qa.niffler.jupiter.annotations.Token;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.service.impl.AuthApiClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OauthTest {

    private static final Config CFG = Config.getInstance();
    private final AuthApiClient authApiClient = new AuthApiClient();

    @Test
    @ApiLogin(username = "leon", password = "1234")
    void oauthTest(@Token String token, UdUserJson userJson) {
        System.out.println(userJson);
        assertNotNull(token);
    }
}
