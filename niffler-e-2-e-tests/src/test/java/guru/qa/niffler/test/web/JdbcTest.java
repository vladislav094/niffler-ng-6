package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.service.impl.SpendDbClient;
import guru.qa.niffler.service.impl.UserdataDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;

public class JdbcTest {

    public static UserdataDbClient userdataDbClient = new UserdataDbClient();

    @ValueSource(strings = {
            "valentin-12"
    })
    @ParameterizedTest
    void hibernateTest(String uname) {
//        UdUserJson user = userdataDbClient.createUser(uname, "1234");
//        System.out.println(user);d
//        userdataDbClient.addIncomeInvitation(user,1);
//        userdataDbClient.addOutcomeInvitation(user,1);
    }

    @Test
    void spendTest() {

        SpendDbClient spendDbClient = new SpendDbClient();
        SpendJson spendJson = new SpendJson(
                null,
                new Date(),
                new CategoryJson(
                        null,
                        "spendCategoryName",
                        "spendUsername10",
                        false
                ),
                CurrencyValues.RUB,
                700.00,
                "spendDescription",
                "spendUsername10"
        );

        spendJson = spendDbClient.createSpend(spendJson);

        System.out.println(spendJson);

        spendJson = spendDbClient.updateSpend(spendJson);
//
        System.out.println(spendJson);
    }

    @Test
    void debugTest() {
//        UdUserJson u = userdataDbClient.getDebug("valentin-12");
//        UdUserJson u = userdataDbClient.getUserById("645ac424-898d-11ef-8e45-0242ac110002");
        UdUserJson leon = userdataDbClient.getUserByName("springJdbc41");
//        System.out.println(u);
//        System.out.println(leon);
//        leon = userdataDbClient.updateUser(leon);
//        System.out.println(leon);
        userdataDbClient.deleteUdUser(leon);
    }

    @Test
    void debug2Test() {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson spendById = spendDbClient.getSpendById("fb469e54-916b-11ef-be40-0242ac110002");
        System.out.println(spendById);
        spendDbClient.removeSpend(spendById);
//
//        CategoryJson categoryJson = spendDbClient.getCategoryById("7edcd378-baf6-48e1-b6d6-72ee354a84de");
//        System.out.println(categoryJson);
//
//        spendDbClient.deleteCategory(categoryJson);
    }

    @Test
    void testFriendshipInvitation() {

        UdUserJson user = userdataDbClient.getUserByName("vladislav");
//        userdataDbClient.addOutcomeInvitation(user, 1);
//        userdataDbClient.addIncomeInvitation(user, 1);
        userdataDbClient.createFriend(user, 1);
    }
}
