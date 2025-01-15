package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.FriendState;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.service.UsersClient;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static guru.qa.niffler.model.FriendState.*;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static java.util.Objects.requireNonNull;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsersApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final String defaultPassword = "12345";

    private final UserdataApi userdataApi = new RestClient.EmptyClient(CFG.userdataUrl()).create(UserdataApi.class);
    private final AuthApi authApi = new RestClient.EmptyClient(CFG.authUrl()).create(AuthApi.class);


    @Override
    @Nonnull
    @Step("Create user using API")
    public UdUserJson createUser(String username, String password) {
        try {
            authApi.getRegisterForm().execute();
            authApi.registerUser(
                    username,
                    password,
                    password,
                    ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
            ).execute();

            UdUserJson user = requireNonNull(userdataApi.getCurrentUser(username).execute().body());
            return user.addTestData(
                    new TestData(password)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    public List<UdUserJson> getAllUsers(@Nonnull String username, @Nullable String searchQuery) {

        final Response<List<UdUserJson>> response;
        try {
            response = userdataApi.getAllUsers(username, searchQuery)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }

    @Override
    public void createIncomingInvitation(UdUserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final Response<UdUserJson> response;
                final UdUserJson newUser;
                try {
                    newUser = createUser(randomUsername(), defaultPassword);
                    response = userdataApi.sendInvitation(newUser.username(), targetUser.username()).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, response.code());

                targetUser.testData().incomingRequest().add(newUser);
            }
        }
    }

    @Override
    public void createOutcomingInvitation(UdUserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final Response<UdUserJson> response;
                final UdUserJson newUser;
                try {
                    newUser = createUser(randomUsername(), defaultPassword);
                    response = userdataApi.sendInvitation(targetUser.username(), newUser.username()).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, response.code());

                targetUser.testData().outcomingRequest().add(newUser);
            }
        }
    }

    @Override
    public void createFriend(UdUserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final Response<UdUserJson> response;
                final UdUserJson newUser;
                try {
                    newUser = createUser(randomUsername(), defaultPassword);
                    userdataApi.sendInvitation(newUser.username(), targetUser.username()).execute();
                    response = userdataApi.acceptInvitation(targetUser.username(), newUser.username()).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, response.code());

                targetUser.testData().friends().add(response.body());
            }
        }
    }

    @Nonnull
    public List<UdUserJson> getAllOutcomingInvitations(@Nonnull String username, @Nullable String searchQuery) {
        final Response<List<UdUserJson>> response;
        try {
            response = userdataApi.getAllUsers(username, searchQuery).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        assertEquals(SC_OK, response.code());
        if (response.body() == null) {
            return Collections.emptyList();
        }

        return response.body().stream()
                .filter(userJson -> userJson.friendState() != null)
                .filter(userJson -> userJson.friendState().name().equals(INVITE_SENT.name()))
                .toList();
    }

    @Nonnull
    public List<UdUserJson> getAllIncomingInvitations(@Nonnull String username, @Nullable String searchQuery) {

        return getFriendsWithFriendState(username, searchQuery, INVITE_RECEIVED);
    }

    @Nonnull
    public List<UdUserJson> getFriends(@Nonnull String username, @Nullable String searchQuery) {

        return getFriendsWithFriendState(username, searchQuery, FRIEND);
    }

    // используем для получения списка пользователей при запросе на /internal/friends/all
    @NotNull
    private List<UdUserJson> getFriendsWithFriendState(@Nonnull String username, @Nullable String searchQuery, FriendState inviteReceived) {
        final Response<List<UdUserJson>> response;
        try {
            response = userdataApi.getAllFriends(username, searchQuery).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        assertEquals(SC_OK, response.code());
        if (response.body() == null) {
            return Collections.emptyList();
        }

        return response.body().stream()
                .filter(userJson -> userJson.friendState() != null)
                .filter(userJson -> userJson.friendState().name().equals(inviteReceived.name()))
                .toList();
    }
}
