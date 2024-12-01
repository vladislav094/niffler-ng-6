package guru.qa.niffler.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.CodeInterceptor;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extensions.ApiLoginExtension;
import guru.qa.niffler.utils.OauthUtils;
import lombok.SneakyThrows;
import retrofit2.Response;

import java.util.Objects;

public class AuthApiClient extends RestClient {

    private static final Config CFG = Config.getInstance();
    private final AuthApi authApi;
    private final String redirectUri = CFG.frontUrl() + "authorized";

    public AuthApiClient() {
        super(CFG.authUrl(), true, new CodeInterceptor());
        this.authApi = create(AuthApi.class);
    }

    @SneakyThrows
    public String login(String username, String password) {
        final String clientId = "client";
        final String codeVerifier = OauthUtils.generateCodeVerifier();
        final String codeChallenge = OauthUtils.generateCodeChallenge(codeVerifier);

        authApi.authorize(
                        "code",
                        clientId,
                        "openid",
                        redirectUri,
                        codeChallenge,
                        "S256"
                )
                .execute();

        authApi.login(
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN"),
                username,
                password
        ).execute();

        Response<JsonNode> tokenResponse = authApi.token(
                        ApiLoginExtension.getCode(),
                        redirectUri,
                        codeVerifier,
                        "authorization_code",
                        clientId
                )
                .execute();

        return Objects.requireNonNull(tokenResponse.body())
                .get("id_token")
                .asText();
    }
}
