package guru.qa.niffler.test.debug;

import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.jupiter.annotations.meta.WebTest;
import guru.qa.niffler.jupiter.extensions.UsersClientExtension;
import guru.qa.niffler.model.UdUserJson;
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
    void hibernateTest(UdUserJson userJson) {
        UdUserJson user = usersClient.createUser(userJson.username(), "1234");
        System.out.println(user);
    }
}
