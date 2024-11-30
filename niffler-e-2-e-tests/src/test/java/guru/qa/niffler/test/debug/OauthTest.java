package guru.qa.niffler.test.debug;

import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.service.impl.AuthApiClient;
import guru.qa.niffler.utils.OauthUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OauthTest {

    @User
    @Test
    void oauthTest(UdUserJson user) {
        AuthApiClient authApiClient = new AuthApiClient();
        Response response = authApiClient.login(user.username(), user.testData().password());
        String token = authApiClient.token(response);
        System.out.println(token);
        assertNotNull(token);

    }
}
