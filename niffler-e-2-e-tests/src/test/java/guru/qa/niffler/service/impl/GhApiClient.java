package guru.qa.niffler.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.GhApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.service.GhClient;
import guru.qa.niffler.service.RestClient;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.Objects;

public class GhApiClient extends RestClient implements GhClient {

    private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";

    private final GhApi ghApi;

    public GhApiClient() {
        super(CFG.ghUrl());
        this.ghApi = retrofit.create(GhApi.class);
    }

    @NotNull
    @Override
    @SneakyThrows
    public String issueState(String issueNumber) {
        JsonNode response = ghApi.issue("Bearer " + System.getenv(GH_TOKEN_ENV), issueNumber)
                .execute()
                .body();
        return Objects.requireNonNull(response).get("state").asText();
    }
}
