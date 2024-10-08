package guru.qa.niffler.test.web;

import guru.qa.niffler.model.*;
import guru.qa.niffler.service.AuthDbClient;
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

        UdUserJson authUser = userdataDbClient.createUser(
                new UdUserJson(
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

        UdUserJson auth = userdataDbClient.createUserSpringJdbc(
                new UdUserJson(
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
        AuthDbClient authDbClient = new AuthDbClient();

        System.out.println("====AuthAUTHORITY====");
        List<AuthAuthorityJson> list = authDbClient.getAllAuthority();
        for (AuthAuthorityJson authAuthorityJson : list) {
            System.out.println(authAuthorityJson);
        }

        System.out.println("====AuthAUTHORITY SPRING JDBC====");
        List<AuthAuthorityJson> authAuthorityListSpringJdbc = authDbClient.getAllAuthAuthoritySpringJdbc();
        for (AuthAuthorityJson authUserJson : authAuthorityListSpringJdbc) {
            System.out.println(authUserJson);
        }

        System.out.println("====AuthUSER====");
        List<AuthUserJson> authUserList = authDbClient.getAllAuthUser();
        for (AuthUserJson authUserJson : authUserList) {
            System.out.println(authUserJson);
        }

        System.out.println("====AuthUSER SPRING JDBC====");
        List<AuthUserJson> authUserListSpringJdbc = authDbClient.getAllAuthUserSpringJdbc();
        for (AuthUserJson authUserJson : authUserListSpringJdbc) {
            System.out.println(authUserJson);
        }

        System.out.println("====CATEGORY====");
        List<CategoryJson> categoryJsonList = spendDbClient.getAllCategory();
        for (CategoryJson categoryJson : categoryJsonList) {
            System.out.println(categoryJson);
        }

        System.out.println("====CATEGORY SPRING JDBC====");
        List<CategoryJson> categoryJsonListSpringJdbc = spendDbClient.getAllCategorySpringJdbc();
        for (CategoryJson categoryJson : categoryJsonListSpringJdbc) {
            System.out.println(categoryJson);
        }

        System.out.println("=====SPEND====");
        List<SpendJson> spendJsonList = spendDbClient.getAllSpends();
        for (SpendJson spendJson : spendJsonList) {
            System.out.println(spendJson);
        }

        System.out.println("=====SPEND SPRING JDBC====");
        List<SpendJson> spendJsonListSpringJdbc = spendDbClient.getAllSpendSpringJdbc();
        for (SpendJson spendJson : spendJsonListSpringJdbc) {
            System.out.println(spendJson);
        }


        System.out.println("====UdUSER====");
        List<UdUserJson> udUserJsonList = userdataDbClient.getAllUser();
        for (UdUserJson udUserJson : udUserJsonList) {
            System.out.println(udUserJson);
        }

        System.out.println("====UdUSER SPRING JDBC====");
        List<UdUserJson> udUserJsonListSpringJdbc = userdataDbClient.getAllUdUserSpringJdbc();
        for (UdUserJson udUserJson : udUserJsonListSpringJdbc) {
            System.out.println(udUserJson);
        }

    }
}
