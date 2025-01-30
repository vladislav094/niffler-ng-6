package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.apollo.api.Error;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.AllPeopleQuery;
import guru.qa.AnotherUserCategoriesQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AnotherUserCategoriesGraphQlTest extends BaseGraphQlTest {

    @User(friends = 1)
    @ApiLogin
    @Test
    void anotherUserCategoriesQueryShouldReturnError(@Token String token) {

        final ApolloCall<AnotherUserCategoriesQuery.Data> allPeopleCall = apolloClient.query(AnotherUserCategoriesQuery.builder()
                        .page(0)
                        .size(10)
                        .build())
                .addHttpHeader(authorization, token);

        final ApolloResponse<AnotherUserCategoriesQuery.Data> response = Rx2Apollo.single(allPeopleCall).blockingGet();
        final List<Error> errors = Optional.ofNullable(response.errors).orElseGet(Collections::emptyList);
        System.out.println(token);
        Assertions.assertFalse(errors.isEmpty());
        Assertions.assertEquals("Can`t query categories for another user", errors.getFirst().getMessage());
    }
}
