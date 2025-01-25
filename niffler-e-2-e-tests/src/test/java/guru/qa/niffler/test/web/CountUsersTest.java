package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebTest
@Isolated
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CountUsersTest {


    @User
    @Test
    @Order(1)
    void otherUsersNotExistsTest(UserJson user) {
        UsersApiClient usersApiClient = new UsersApiClient();
        List<UserJson> responseList = usersApiClient.getAllUsers(user.username(), null);
        assertTrue(responseList.isEmpty());
    }

    @User
    @Test
    @Order(2)
    void haveAnotherUsersTest(UserJson user) {
        UsersApiClient usersApiClient = new UsersApiClient();
        List<UserJson> responseList = usersApiClient.getAllUsers(user.username(), null);
        assertFalse(responseList.isEmpty());
    }
}
