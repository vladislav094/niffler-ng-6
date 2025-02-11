package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static guru.qa.niffler.grpc.CurrencyValues.*;

public class CurrencyGrpcTest extends BaseGrpcTest {

    @Test
    void allCurrenciesShouldReturned() {
        final CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
        final List<Currency> allCurrenciesList = response.getAllCurrenciesList();
        Assertions.assertEquals(4, allCurrenciesList.size());
    }

    @ParameterizedTest
    @MethodSource("providerCurrencies")
    void calculateRateShouldReturnCorrectAmount(
            CurrencyValues spendCurrency,
            CurrencyValues desiredCurrency,
            double spendAmount,
            double expectedAmount,
            String testDescription
    ) {
        CalculateRequest calculateRequest = CalculateRequest.newBuilder()
                .setSpendCurrency(spendCurrency)
                .setDesiredCurrency(desiredCurrency)
                .setAmount(spendAmount)
                .build();

        final CalculateResponse response = blockingStub.calculateRate(calculateRequest);
        System.out.println(response.getCalculatedAmount());
        Assertions.assertEquals(expectedAmount, response.getCalculatedAmount());
    }

    private static Stream<Arguments> providerCurrencies() {
        return Stream.of(
                Arguments.of(USD, EUR, 100, 92.59, "USD to EUR"),
                Arguments.of(USD, KZT, 100, 47619.05, "USD to KZT"),
                Arguments.of(USD, RUB, 100, 6666.67, "USD to RUB"),
                Arguments.of(EUR, USD, 100, 108, "EUR to USD"),
                Arguments.of(EUR, KZT, 100, 51428.57, "EUR to KZT"),
                Arguments.of(EUR, RUB, 100, 7200, "EUR to RUB"),
                Arguments.of(KZT, EUR, 100, 0.19, "KZT to EUR"),
                Arguments.of(KZT, USD, 100, 0.21, "KZT to USD"),
                Arguments.of(KZT, RUB, 100, 14, "KZT to RUB"),
                Arguments.of(RUB, EUR, 100, 1.39, "RUB to EUR"),
                Arguments.of(RUB, USD, 100, 1.5, "RUB to USD"),
                Arguments.of(RUB, KZT, 100, 714.29, "RUB to KZT")
        );
    }
}
