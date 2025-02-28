package guru.qa.niffler.test.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.Currency;
import guru.qa.niffler.grpc.CurrencyResponse;
import guru.qa.niffler.grpc.CurrencyValues;
import guru.qa.niffler.grpc.NifflerCurrencyServiceGrpc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.grpc.Jetty12GrpcExtensionFactory;
import org.wiremock.grpc.dsl.WireMockGrpc;
import org.wiremock.grpc.dsl.WireMockGrpcService;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CurrencyControllerTest {

    private final WireMockServer wiremock = new WireMockServer(
            WireMockConfiguration.wireMockConfig()
                    .port(8092)
                    .withRootDirectory("src/test/resources/wiremock")
                    .extensions(new Jetty12GrpcExtensionFactory())
    );

    private WireMockGrpcService mockGrpcService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        wiremock.start();
        mockGrpcService = new WireMockGrpcService(
                new WireMock(wiremock.port()),
                NifflerCurrencyServiceGrpc.SERVICE_NAME
        );
    }

    @AfterEach
    void afterEach() {
        wiremock.shutdown();
    }

    @Test
    void getAllCurrenciesTest() throws Exception {
        CurrencyResponse currencyResponse = CurrencyResponse.newBuilder()
                .addAllAllCurrencies(
                        List.of(
                                Currency.newBuilder().setCurrency(CurrencyValues.RUB).setCurrencyRate(1.0).build(),
                                Currency.newBuilder().setCurrency(CurrencyValues.USD).setCurrencyRate(75.0).build(),
                                Currency.newBuilder().setCurrency(CurrencyValues.EUR).setCurrencyRate(85.0).build(),
                                Currency.newBuilder().setCurrency(CurrencyValues.KZT).setCurrencyRate(0.2).build()
                        )
                )
                .build();

        mockGrpcService.stubFor(
                WireMockGrpc.method("GetAllCurrencies")
                        .withRequestMessage(WireMockGrpc.equalToMessage(Empty.getDefaultInstance()))
                        .willReturn(WireMockGrpc.message(currencyResponse))
        );

        mockMvc.perform(
                        get("/api/currencies/all")
                                .with(jwt().jwt(c -> c.claim("sub", "duck"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currency").value("RUB"))
                .andExpect(jsonPath("$[0].currencyRate").value(1.0))
                .andExpect(jsonPath("$[1].currency").value("USD"))
                .andExpect(jsonPath("$[1].currencyRate").value(75.0))
                .andExpect(jsonPath("$[2].currency").value("EUR"))
                .andExpect(jsonPath("$[2].currencyRate").value(85.0))
                .andExpect(jsonPath("$[3].currency").value("KZT"))
                .andExpect(jsonPath("$[3].currencyRate").value(0.2));
    }
}
