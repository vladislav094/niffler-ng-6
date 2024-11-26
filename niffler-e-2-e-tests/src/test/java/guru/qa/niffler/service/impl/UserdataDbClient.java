package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UdUserRepository;
import guru.qa.niffler.data.repository.implRepository.auth.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.implRepository.auth.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.implRepository.auth.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.data.repository.implRepository.userdata.UdUserRepositoryHibernate;
import guru.qa.niffler.data.repository.implRepository.userdata.UdUserRepositoryJdbc;
import guru.qa.niffler.data.repository.implRepository.userdata.UdUserRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UdUserJson;
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
    public UdUserJson createUser(@Nonnull String username, @Nonnull String password) {
        return requireNonNull(xaTxTemplate.execute(() -> {
            AuthUserEntity authUser = authUserEntity(username, password);
            authUserRepository.create(authUser);
            UdUserJson user = UdUserJson.fromEntity(udUserRepository.create(userEntity(username)),
                    null);
            return user.addTestData(new TestData(password));
        }));
    }

    @Nullable
    public UdUserJson getUserByName(@Nonnull String name) {
        Optional<UdUserEntity> user = udUserRepository.findByUsername(name);
        return UdUserJson.fromEntity(user.orElseThrow(), null);
    }

    @Nullable
    public UdUserJson getUserById(@Nonnull String id) {
        Optional<UdUserEntity> user = udUserRepository.findById(UUID.fromString(id));
        return UdUserJson.fromEntity(user.orElseThrow(), null);
    }

    @Nonnull
    public UdUserJson updateUser(@Nonnull UdUserJson user) {
        return requireNonNull(xaTxTemplate.execute(() -> {
            UdUserEntity userEntity = UdUserEntity.fromJson(user);
            return UdUserJson.fromEntity(udUserRepository.update(userEntity), null);
        }));
    }

    @Override
    public void createIncomingInvitation(@Nonnull UdUserJson targetUser, int count) {
        if (count > 0) {
            UdUserEntity targetEntity = targetUserEntity(targetUser);
            for (int i = 0; i < count; i++) {
                xaTxTemplate.execute(() -> {
                    AuthUserEntity authUser = randomAuthUserEntity();
                    UdUserEntity newUser = udUserRepository.create(userEntity(authUser.getUsername()));
                    udUserRepository.sendInvitation(newUser, targetEntity);
                    return null;
                });
            }
        }
    }

    @Override
    public void createOutcomingInvitation(@Nonnull UdUserJson targetUser, int count) {
        if (count > 0) {
            UdUserEntity targetEntity = targetUserEntity(targetUser);
            for (int i = 0; i < count; i++) {
                xaTxTemplate.execute(() -> {
                    AuthUserEntity authUser = randomAuthUserEntity();
                    UdUserEntity newUser = udUserRepository.create(userEntity(authUser.getUsername()));
                    udUserRepository.sendInvitation(targetEntity, newUser);
                    return null;
                });
            }
        }
    }

    @Override
    public void createFriend(@Nonnull UdUserJson targetUser, int count) {
        if (count > 0) {
            UdUserEntity targetEntity = targetUserEntity(targetUser);
            for (int i = 0; i < count; i++) {
                xaTxTemplate.execute(() -> {
                    AuthUserEntity authUser = randomAuthUserEntity();
                    UdUserEntity newUser = udUserRepository.create(userEntity(authUser.getUsername()));
                    udUserRepository.addFriend(targetEntity, newUser);
                    return null;
                });
            }
        }
    }

    public void deleteUdUser(@Nonnull UdUserJson user) {
        xaTxTemplate.execute(() -> {
            udUserRepository.remove(UdUserEntity.fromJson(user));
            return null;
        });
    }

    private UdUserEntity targetUserEntity(UdUserJson user) {
        return udUserRepository.findById(user.id())
                .orElseThrow();
    }

    private AuthUserEntity randomAuthUserEntity() {
        String username = randomUsername();
        AuthUserEntity authUser = authUserEntity(username, "1234");
        return authUserRepository.create(authUser);
    }

    private UdUserEntity userEntity(String username) {
        UdUserEntity ue = new UdUserEntity();
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
