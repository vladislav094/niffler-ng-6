package guru.qa.niffler.test.web;

import guru.qa.niffler.model.*;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserdataDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

public class JdbcTest {

    @Test
    void txTest() {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson spend = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "cat-name",
                                "duck1",
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "spend-name-tx",
                        "duck1"
                )
        );
        System.out.println(spend);
    }

    @Test
    void createAuthUserTest() {
        UserdataDbClient userdataDbClient = new UserdataDbClient();

        UserJson authUser = userdataDbClient.createUser(
                new UserJson(
                        null,
                        "pork2121",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                ));
        System.out.println(authUser);
    }

    @Test
    void springJdbcTest() {
        UserdataDbClient userdataDbClient = new UserdataDbClient();

        UserJson auth = userdataDbClient.createUserSpringJdbc(
                new UserJson(
                        null,
                        "springJdbc21",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
        System.out.println(auth);
    }

    @Test
    void springJdbcGetCategoriesByUsername() {
        SpendDbClient spendDbClient = new SpendDbClient();
        UserdataDbClient userdataDbClient = new UserdataDbClient();
        List<AuthAuthorityJson> list = userdataDbClient.getAllAuthority();
        for (AuthAuthorityJson authAuthorityJson : list) {
            System.out.println(authAuthorityJson);
        }
//        List<CategoryJson> categoryJsonList = spendDbClient.getCategoriesByUsernameSpringJdbc("vladislav");
//        for (CategoryJson categoryJson : categoryJsonList) {
//            System.out.println(categoryJson);
//        }
    }
}
