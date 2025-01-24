package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.UsersClientExtension;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.UserdataDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@WebTest
@ExtendWith(UsersClientExtension.class)
public class JdbcTest {

    public static UserdataDbClient userdataDbClient = new UserdataDbClient();
    private UsersClient usersClient;

    @User
    @Test
    void hibernateTest(UserJson userJson) {
        UserJson user = usersClient.createUser(userJson.username(), "1234");
        System.out.println(user);
    }
}
