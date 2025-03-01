package guru.qa.niffler.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void categoriesListShouldBeReturnedForCurrentUser() throws Exception {
        final String fixtureUser = "bee";

        mockMvc.perform(get("/api/currencies/all")
                        .with(jwt().jwt(c -> c.claim("sub", fixtureUser))))
                .andExpect(status().isOk())
                .andDo(print());
    }
}