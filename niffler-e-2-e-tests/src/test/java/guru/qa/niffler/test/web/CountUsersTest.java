package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.jupiter.annotations.meta.WebTest;
import guru.qa.niffler.model.UdUserJson;
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
    void otherUsersNotExistsTest(UdUserJson user) {
        UsersApiClient usersApiClient = new UsersApiClient();
        List<UdUserJson> responseList = usersApiClient.getAllUsers(user.username(), null);
        assertTrue(responseList.isEmpty());
    }

    @User
    @Test
    @Order(2)
    void haveAnotherUsersTest(UdUserJson user) {
        UsersApiClient usersApiClient = new UsersApiClient();
        List<UdUserJson> responseList = usersApiClient.getAllUsers(user.username(), null);
        assertFalse(responseList.isEmpty());
    }
}