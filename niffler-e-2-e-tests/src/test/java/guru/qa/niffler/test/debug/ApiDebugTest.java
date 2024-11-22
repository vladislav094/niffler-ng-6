package guru.qa.niffler.test.debug;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotations.Spending;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.impl.UsersApiClient;
import guru.qa.niffler.test.web.BaseWebTest;
import org.junit.jupiter.api.Test;

public class ApiDebugTest extends BaseWebTest {

    @Test
    void debugTest() {
        UsersApiClient usersApiClient = new UsersApiClient();
        usersApiClient.createUser("qweqwe", "12345");
    }

    @User(friends = 1)
    @Test
    void createFriendTest(UdUserJson user) {
        UsersApiClient usersApiClient = new UsersApiClient();
        System.out.println(user.username());
        System.out.println(user.testData().friends().getFirst().username());

    }

    @User(incomingRequests = 1)
    @Test
    void createIncomingFriendRequestTest(UdUserJson user) {
        System.out.println(user.username());
        System.out.println(user.testData().incomingRequest().getFirst().username());

    }

    @User(outcomingRequests = 1)
    @Test
    void createOutcomingFriendRequestTest(UdUserJson user) {
        System.out.println(user.username());
        System.out.println(user.testData().outcomingRequest().getFirst().username());

    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @Test
    void categoryDescriptionShouldBeChangedFromTable(UdUserJson user) {
        final String newDescription = "Обучение Niffler Next Generation";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .editSpending(user.testData().spendings().getFirst().description())
                .setNewSpendingDescription(newDescription)
                .save();
        new MainPage().checkThatTableContainsSpending(newDescription);
    }
}
