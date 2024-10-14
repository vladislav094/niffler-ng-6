package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserdataUserJson;
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

        UserdataUserJson userJdbcTransaction = userdataDbClient.createUserJdbcTransaction(
                new UserdataUserJson(
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
        List<UserdataUserJson> usUserJdbc = userdataDbClient.getAllUserJdbcNotTransaction();
        for (UserdataUserJson userdataUserJson : usUserJdbc) {
            System.out.println(userdataUserJson);
        }

        System.out.println("====JDBC transaction====");
        List<UserdataUserJson> udUserJdbcTransaction = userdataDbClient.getAllUserJdbcTransaction();
        for (UserdataUserJson userdataUserJson : udUserJdbcTransaction) {
            System.out.println(userdataUserJson);
        }

        System.out.println("====Spring Jdbc not transaction====");
        List<UserdataUserJson> userdataUserSpringJdbc = userdataDbClient.getAllUserSpringJdbcNotTransaction();
        for (UserdataUserJson userdataUserJson : userdataUserSpringJdbc) {
            System.out.println(userdataUserJson);
        }

        System.out.println("====Spring Jdbc transaction====");
        List<UserdataUserJson> udUserSpringJdbcTransaction = userdataDbClient.getAllUserSpringJdbcTransaction();
        for (UserdataUserJson userdataUserJson : udUserSpringJdbcTransaction) {
            System.out.println(userdataUserJson);
        }
    }
}
