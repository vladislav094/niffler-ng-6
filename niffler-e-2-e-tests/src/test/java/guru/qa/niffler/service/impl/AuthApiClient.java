package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.TokenResponse;
import guru.qa.niffler.utils.OauthUtils;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Response;

import java.io.IOException;
import java.util.Objects;

public class AuthApiClient extends RestClient {

    private static final Config CFG = Config.getInstance();
    private final AuthApi authApi;
    private final String redirectUrl = CFG.frontUrl() + "authorized";
    private final String clientId = "client";
    private final String codeVerifier = OauthUtils.generateCodeVerifier();
    private final String codeChallenge = OauthUtils.generateCodeChallenge(codeVerifier);

    public AuthApiClient() {
        super(CFG.authUrl(), true);
        this.authApi = create(AuthApi.class);
    }

    public Response<Void> login(String username, String password) {
        final Response<Void> response;
        preRequest(codeChallenge);
        try {
            response = authApi.login(
                            ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN"),
                            username,
                            password)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    public String token(Response response) {
        final Response<TokenResponse> token;
        String code = StringUtils.substringAfter(response.raw().request().url().toString(), "code=");
        try {
            token = authApi.token(
                    code,
                    redirectUrl,
                    codeVerifier,
                    "authorization_code",
                    clientId
            ).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Objects.requireNonNull(token.body()).getIdToken();
    }

    private void preRequest(String codeChallenge) {
        try {
            authApi.getOauth2Authorize(
                    "code",
                    clientId,
                    "openid",
                    redirectUrl,
                    codeChallenge,
                    "S256"
            ).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
