package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.jupiter.annotations.meta.WebTest;
import guru.qa.niffler.jupiter.extensions.UsersClientExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.SpendDbClient;
import guru.qa.niffler.service.impl.UserdataDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;

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
