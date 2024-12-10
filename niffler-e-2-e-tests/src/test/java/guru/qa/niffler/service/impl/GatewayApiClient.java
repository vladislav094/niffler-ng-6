package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.model.rest.FriendJson;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GatewayApiClient extends RestClient {
    private final GatewayApi gatewayApi;

    public GatewayApiClient() {
        super(CFG.gatewayUrl());
        this.gatewayApi = create(GatewayApi.class);
    }

    @Step("send /api/friends/all GET request to niffler-gateway")
    public List<UdUserJson> allFriends(@Nonnull String bearerToken,
                                       @Nullable String searchQuery) {
        final Response<List<UdUserJson>> response;
        try {
            response = gatewayApi.allFriends(bearerToken, searchQuery).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("send /api/friends/remove DELETE request to niffler-gateway")
    public void removeFriend(@Nonnull String bearerToken,
                             @Nullable String targetUsername) {
        final Response<Void> response;
        try {
            response = gatewayApi.removeFriend(bearerToken, targetUsername).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

    @Step("send /api/invitations/accept POST request to niffler-gateway")
    public UdUserJson acceptInvitation(@Nonnull String bearerToken,
                                       FriendJson friend) {
        final Response<UdUserJson> response;
        try {
            response = gatewayApi.acceptInvitation(bearerToken, friend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("send /api/invitations/send POST request to niffler-gateway")
    public UdUserJson sendInvitation(@Nonnull String bearerToken,
                                       FriendJson friend) {
        final Response<UdUserJson> response;
        try {
            response = gatewayApi.sendInvitation(bearerToken, friend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("send /api/invitations/decline POST request to niffler-gateway")
    public UdUserJson declineInvitation(@Nonnull String bearerToken,
                                       FriendJson friend) {
        final Response<UdUserJson> response;
        try {
            response = gatewayApi.declineInvitation(bearerToken, friend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("send /api/users/current GET request to niffler-gateway")
    public UdUserJson getCurrentUser(@Nonnull String bearerToken) {
        final Response<UdUserJson> response;
        try {
            response = gatewayApi.currentUser(bearerToken).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("send /api/users/all GET request to niffler-gateway")
    public List<UdUserJson> allUsers(@Nonnull String bearerToken, @Nullable String searchQuery) {
        final Response<List<UdUserJson>> response;
        try {
            response = gatewayApi.allUsers(bearerToken, searchQuery).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }
}
