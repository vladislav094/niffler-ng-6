package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.service.UserdataDbClient;
import org.junit.jupiter.api.Test;

import java.util.List;

public class JdbcTest {


    @Test
    void jdbcAndSpringJdbcCreateUser() {
        UserdataDbClient userdataDbClient = new UserdataDbClient();

//        UserdataUserJson userJdbc = userdataDbClient.creatUserJdbcNotTransaction(
//                new UserdataUserJson(
//                        null,
//                        "springJdbc412",
//                        null,
//                        null,
//                        null,
//                        CurrencyValues.RUB,
//                        null,
//                        null,
//                        null
//                )
//        );
//        System.out.println(userJdbc);

        UdUserJson userJdbcTransaction = userdataDbClient.createUserJdbcTransaction(
                new UdUserJson(
                        null,
                        "springJdbc77711",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
        System.out.println(userJdbcTransaction);

//
//        UserdataUserJson userSpringJdbc = userdataDbClient.creatUserSpringJdbcNotTransaction(
//                new UserdataUserJson(
//                        null,
//                        "springJdbc62",
//                        null,
//                        null,
//                        null,
//                        CurrencyValues.RUB,
//                        null,
//                        null,
//                        null
//                )
//        );
//        System.out.println(userSpringJdbc);
//
//        UserdataUserJson userSpringJdbcTransaction = userdataDbClient.createUserSpringJdbcTransaction(
//                new UserdataUserJson(
//                        null,
//                        "springJdbc72",
//                        null,
//                        null,
//                        null,
//                        CurrencyValues.RUB,
//                        null,
//                        null,
//                        null
//                )
//        );
//        System.out.println(userSpringJdbcTransaction);
    }

    @Test
    void jdbcAndSpringJdbcGetAllUserdataUser() {
        UserdataDbClient userdataDbClient = new UserdataDbClient();

        System.out.println("====JDBC not transaction====");
        List<UdUserJson> usUserJdbc = userdataDbClient.getAllUserJdbcNotTransaction();
        for (UdUserJson udUserJson : usUserJdbc) {
            System.out.println(udUserJson);
        }

        System.out.println("====JDBC transaction====");
        List<UdUserJson> udUserJdbcTransaction = userdataDbClient.getAllUserJdbcTransaction();
        for (UdUserJson udUserJson : udUserJdbcTransaction) {
            System.out.println(udUserJson);
        }

        System.out.println("====Spring Jdbc not transaction====");
        List<UdUserJson> userdataUserSpringJdbc = userdataDbClient.getAllUserSpringJdbcNotTransaction();
        for (UdUserJson udUserJson : userdataUserSpringJdbc) {
            System.out.println(udUserJson);
        }

        System.out.println("====Spring Jdbc transaction====");
        List<UdUserJson> udUserSpringJdbcTransaction = userdataDbClient.getAllUserSpringJdbcTransaction();
        for (UdUserJson udUserJson : udUserSpringJdbcTransaction) {
            System.out.println(udUserJson);
        }
    }

    @Test
    void userdataSendFriendshipRequest() {

        UserdataDbClient userdataDbClient = new UserdataDbClient();
//        UdUserJson from = userdataDbClient.getUserByNameJdbc("vladislav");
//        UdUserJson to = userdataDbClient.getUserByNameJdbc("pork");
//        userdataDbClient.createFriendshipRequestJdbc(from, to);

        UdUserJson from = userdataDbClient.getUserByNameSpringJdbc("vladislav");
        UdUserJson to = userdataDbClient.getUserByNameSpringJdbc("pork");
        System.out.println(from);
        System.out.println(to);
//        userdataDbClient.createFriendshipRequestSpringJdbc(from, to);
        userdataDbClient.deleteFriendShipSpringJdbc(from, to);
    }
}
