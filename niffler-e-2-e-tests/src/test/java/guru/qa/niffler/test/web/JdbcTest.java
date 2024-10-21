package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.service.AuthDbClient;
import guru.qa.niffler.service.UserdataDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    void  userdataSendFriendshipRequest() {

        UserdataDbClient userdataDbClient = new UserdataDbClient();
        AuthDbClient authDbClient = new AuthDbClient();
//        UdUserJson from = userdataDbClient.getUserByNameJdbc("vladislav");
//        UdUserJson to = userdataDbClient.getUserByNameJdbc("pork");
//        userdataDbClient.createFriendshipRequestJdbc(from, to);

//        UdUserJson from = userdataDbClient.getUserByNameSpringJdbc("vladislav");
//        UdUserJson to = userdataDbClient.getUserByNameSpringJdbc("pork");
//        System.out.println(from);
//        System.out.println(to);

//        userdataDbClient.createFriendshipRequestSpringJdbc(from, to);
    }

    public static UserdataDbClient userdataDbClient = new UserdataDbClient();

    @ValueSource(strings = {
            "valentin-12"
    })
    @ParameterizedTest
    void hibernateTest(String uname) {
        AuthDbClient authDbClient = new AuthDbClient();

        UdUserJson user = userdataDbClient.createUser(uname, "1234");
        System.out.println(user);
//        userdataDbClient.removeUser(user);
        userdataDbClient.addIncomeInvitation(user,1);
        userdataDbClient.addOutcomeInvitation(user,1);
    }
}
