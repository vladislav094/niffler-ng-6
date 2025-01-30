package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.model.rest.CurrencyValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StatQueriesGraphQlTest extends BaseGraphQlTest {

    @User
    @Test
    @ApiLogin
    void statTest(@Token String bearerToken) {
        final ApolloCall<StatQuery.Data> currenciesCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader(authorization, bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;
        assertEquals(
                0.0,
                result.total
        );
    }

    @User(
            categories = {
                    @Category(name = "Обучение 1"),
                    @Category(name = "Обучение 2", archived = true)
            },
            spendings = {
                    @Spending(
                            category = "Обучение 1",
                            description = "Обучение Advanced 1.0",
                            currency = CurrencyValues.USD,
                            amount = 500
                    ),

                    @Spending(
                            category = "Обучение 2",
                            description = "Обучение Advanced 2.0",
                            currency = CurrencyValues.USD,
                            amount = 750
                    ),
            }
    )
    @ApiLogin
    @Test
    void statShouldHaveCategoriesAndSpendings(@Token String token) {
        final ApolloCall<StatQuery.Data> currenciesCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(guru.qa.type.CurrencyValues.USD)
                        .statCurrency(guru.qa.type.CurrencyValues.USD)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader(authorization, token);
        System.out.println(token);
        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;

        assertNotNull(result);
        assertEquals(1250, result.total);
        assertEquals("Обучение 1", result.statByCategories.getFirst().categoryName);
        assertEquals(500, result.statByCategories.getFirst().sum);
        assertEquals("Archived", result.statByCategories.getLast().categoryName);
        assertEquals(750, result.statByCategories.getLast().sum);
    }
}
