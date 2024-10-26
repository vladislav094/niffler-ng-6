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
import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.service.UsersClient;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.faker.RandomDataUtils.randomUsername;

public class UserdataDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();
    private final UdUserRepository udUserRepository = new UdUserRepositoryJdbc();
//    private final TransactionTemplate txTemplate = new TransactionTemplate(
//            new ChainedTransactionManager(
//                    new JdbcTransactionManager(
//                            DataSources.getDataSource(CFG.userdataJdbcUrl())),
//                    new JdbcTransactionManager(DataSources.getDataSource(CFG.authJdbcUrl()))
//            ));
//    private final JdbcTransactionsTemplate jdbcTxTemplate = new JdbcTransactionsTemplate(CFG.userdataJdbcUrl());

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    // USERDATA DB, USER TABLE
    @Override
    public UdUserJson createUser(String username, String password) {
        return xaTxTemplate.execute(() -> {
            AuthUserEntity authUser = authUserEntity(username, password);
            authUserRepository.create(authUser);
            return UdUserJson.fromEntity(
                    udUserRepository.create(userEntity(username)),
                    null);
        });
    }

    public UdUserJson getUserByName(String name) {
        Optional<UdUserEntity> user = udUserRepository.findByUsername(name);
        return UdUserJson.fromEntity(user.orElseThrow(), null);
    }

    public UdUserJson getUserById(String id) {
        Optional<UdUserEntity> user = udUserRepository.findById(UUID.fromString(id));
        return UdUserJson.fromEntity(user.orElseThrow(), null);
    }

    public UdUserJson updateUser(UdUserJson user) {
        return xaTxTemplate.execute(() -> {
            UdUserEntity userEntity = UdUserEntity.fromJson(user);
            return UdUserJson.fromEntity(udUserRepository.update(userEntity), null);
        });
    }

    @Override
    public void createIncomingInvitation(UdUserJson targetUser, int count) {
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
    public void createOutcomingInvitation(UdUserJson targetUser, int count) {
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
    public void createFriend(UdUserJson targetUser, int count) {
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

    public void deleteUdUser(UdUserJson user) {
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
