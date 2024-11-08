package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.service.UsersClient;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;

import static guru.qa.niffler.faker.RandomDataUtils.randomUsername;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsersApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final String defaultPassword = "12345";

    private final UserdataApi userdataApi = new RestClient.EmptyClient(CFG.userdataUrl()).create(UserdataApi.class);
    private final AuthApi authApi = new RestClient.EmptyClient(CFG.authUrl()).create(AuthApi.class);


    @Override
    @Nonnull
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
}
