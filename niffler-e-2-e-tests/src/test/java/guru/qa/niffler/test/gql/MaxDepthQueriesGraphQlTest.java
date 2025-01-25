package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.apollo.api.Error;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.AllPeopleQuery;
import guru.qa.UserQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MaxDepthQueriesGraphQlTest extends BaseGraphQlTest {

    private static final String expectedErrorText = "Can`t fetch over 2 friends sub-queries";

    @User
    @ApiLogin
    @Test
    void allPeopleQueryOverMaxDepthShouldReturnErrorMessageTest(@Token String token) {

        final ApolloCall<AllPeopleQuery.Data> allPeopleCall = apolloClient.query(AllPeopleQuery.builder()
                        .page(0)
                        .size(10)
                        .build())
                .addHttpHeader(authorization, token);

        final ApolloResponse<AllPeopleQuery.Data> response = Rx2Apollo.single(allPeopleCall).blockingGet();
        final List<Error> errors = Optional.ofNullable(response.errors).orElseGet(Collections::emptyList);

        Assertions.assertFalse(errors.isEmpty());
        Assertions.assertEquals(expectedErrorText, errors.getFirst().getMessage());
    }

    @User
    @ApiLogin
    @Test
    void userQueryOverMaxDepthShouldReturnErrorMessageText(@Token String token) {
        System.out.println(token);
        final ApolloCall<UserQuery.Data> allPeopleCall = apolloClient.query(UserQuery.builder()
                        .page(0)
                        .size(10)
                        .build())
                .addHttpHeader(authorization, token);

        final ApolloResponse<UserQuery.Data> response = Rx2Apollo.single(allPeopleCall).blockingGet();
        final List<Error> errors = Optional.ofNullable(response.errors).orElseGet(Collections::emptyList);

        Assertions.assertFalse(errors.isEmpty());
        Assertions.assertEquals(expectedErrorText, errors.getFirst().getMessage());
    }
}