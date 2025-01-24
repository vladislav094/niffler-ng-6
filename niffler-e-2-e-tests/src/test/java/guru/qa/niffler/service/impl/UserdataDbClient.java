package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UdUserRepository;
import guru.qa.niffler.data.repository.implRepository.auth.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.data.repository.implRepository.userdata.UdUserRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.model.rest.TestData;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.UsersClient;
import io.qameta.allure.Step;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class UserdataDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositorySpringJdbc();
    private final UdUserRepository udUserRepository = new UdUserRepositorySpringJdbc();

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    // USERDATA DB, USER TABLE
    @Override
    @Nonnull
    @Step("Create user using SQL")
    public UserJson createUser(@Nonnull String username, @Nonnull String password) {
        return requireNonNull(xaTxTemplate.execute(() -> {
            AuthUserEntity authUser = authUserEntity(username, password);
            authUserRepository.create(authUser);
            UserJson user = UserJson.fromEntity(udUserRepository.create(userEntity(username)),
                    null);
            return user.addTestData(new TestData(password));
        }));
    }

    @Nullable
    public UserJson getUserByName(@Nonnull String name) {
        Optional<UserEntity> user = udUserRepository.findByUsername(name);
        return UserJson.fromEntity(user.orElseThrow(), null);
    }

    @Nullable
    public UserJson getUserById(@Nonnull String id) {
        Optional<UserEntity> user = udUserRepository.findById(UUID.fromString(id));
        return UserJson.fromEntity(user.orElseThrow(), null);
    }

    @Nonnull
    public UserJson updateUser(@Nonnull UserJson user) {
        return requireNonNull(xaTxTemplate.execute(() -> {
            UserEntity userEntity = UserEntity.fromJson(user);
            return UserJson.fromEntity(udUserRepository.update(userEntity), null);
        }));
    }

    @Override
    public void createIncomingInvitation(@Nonnull UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = targetUserEntity(targetUser);
            for (int i = 0; i < count; i++) {
                xaTxTemplate.execute(() -> {
                    AuthUserEntity authUser = randomAuthUserEntity();
                    UserEntity newUser = udUserRepository.create(userEntity(authUser.getUsername()));
                    udUserRepository.sendInvitation(newUser, targetEntity);
                    return null;
                });
            }
        }
    }

    @Override
    public void createOutcomingInvitation(@Nonnull UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = targetUserEntity(targetUser);
            for (int i = 0; i < count; i++) {
                xaTxTemplate.execute(() -> {
                    AuthUserEntity authUser = randomAuthUserEntity();
                    UserEntity newUser = udUserRepository.create(userEntity(authUser.getUsername()));
                    udUserRepository.sendInvitation(targetEntity, newUser);
                    return null;
                });
            }
        }
    }

    @Override
    public void createFriend(@Nonnull UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = targetUserEntity(targetUser);
            for (int i = 0; i < count; i++) {
                xaTxTemplate.execute(() -> {
                    AuthUserEntity authUser = randomAuthUserEntity();
                    UserEntity newUser = udUserRepository.create(userEntity(authUser.getUsername()));
                    udUserRepository.addFriend(targetEntity, newUser);
                    return null;
                });
            }
        }
    }

    public void deleteUdUser(@Nonnull UserJson user) {
        xaTxTemplate.execute(() -> {
            udUserRepository.remove(UserEntity.fromJson(user));
            return null;
        });
    }

    private UserEntity targetUserEntity(UserJson user) {
        return udUserRepository.findById(user.id())
                .orElseThrow();
    }

    private AuthUserEntity randomAuthUserEntity() {
        String username = randomUsername();
        AuthUserEntity authUser = authUserEntity(username, "1234");
        return authUserRepository.create(authUser);
    }

    private UserEntity userEntity(String username) {
        UserEntity ue = new UserEntity();
        ue.setUsername(username);
        ue.setCurrency(CurrencyValues.RUB);
        return ue;
    }

    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
                        a -> {
                            AuthAuthorityEntity ae = new AuthAuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(a);
                            return ae;
                        }
                ).toList()
        );
        return authUser;
    }
}
