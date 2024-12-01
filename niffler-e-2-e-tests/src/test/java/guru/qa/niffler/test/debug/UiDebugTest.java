package guru.qa.niffler.test.debug;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotations.ApiLogin;
import guru.qa.niffler.jupiter.annotations.Spending;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.jupiter.annotations.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.service.impl.SpendApiClient;
import guru.qa.niffler.service.impl.UsersApiClient;
import guru.qa.niffler.test.web.BaseWebTest;
import org.junit.jupiter.api.Test;

import java.util.List;

@WebTest
public class UiDebugTest extends BaseWebTest {

    @ApiLogin
    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990
                    ),
                    @Spending(
                            category = "Рыбалка",
                            description = "Рыбалка на Волге",
                            amount = 400
                    )
            },
            friends = 1
    )
    @Test
    public void testApiLoginWithoutUsernameAndPasswordInAnnotation(UdUserJson user) {
        System.out.println(user);
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkUserNotHaveFriend();
    }


    @ApiLogin(username = "leon", password = "1234")
    @Test
    public void testApiLoginWithUsernameAndPasswordInAnnotation(UdUserJson user) {
        System.out.println(user);
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkUserNotHaveFriend();
    }

    @Test
    public void testDebugApi() {
        final SpendApiClient authApiClient = new SpendApiClient();
        List<SpendJson> spendJson = authApiClient.getAllSpend("leon", null, null, null);
        System.out.println(spendJson);
        List<CategoryJson> categoryJson = authApiClient.getAllCategories("leon", false);
        System.out.println(categoryJson);
    }

    @Test
    public void testDebug() {
        final UsersApiClient usersApiClient = new UsersApiClient();
        List<UdUserJson> outcoming = usersApiClient.getAllOutcomingInvitations("leon", null);
        List<UdUserJson> incoming = usersApiClient.getAllIncomingInvitations("leon", null);
        List<UdUserJson> friends = usersApiClient.getFriends("leon", null);
        System.out.println(outcoming);
        System.out.println(incoming);
        System.out.println(friends);
    }
}
