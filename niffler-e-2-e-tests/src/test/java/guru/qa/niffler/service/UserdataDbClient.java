package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.daoImplementation.auth.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.daoImplementation.auth.AuthUserDaoJdbc;
import guru.qa.niffler.data.daoImplementation.userdata.UdUserDaoJdbc;
import guru.qa.niffler.data.daoImplementation.auth.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.daoImplementation.auth.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.daoImplementation.userdata.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.model.UdUserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import static guru.qa.niffler.data.Databases.getDataSource;
import static guru.qa.niffler.data.Databases.xaTransaction;

public class UserdataDbClient {

    private static final Config CFG = Config.getInstance();

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UdUserJson createUserSpringJdbc(UdUserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("1234"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        AuthUserEntity cratedAuthUser = new AuthUserDaoSpringJdbc(getDataSource(CFG.authJdbcUrl()))
                .create(authUser);

        AuthAuthorityEntity[] userAuthorities = Arrays.stream(Authority.values()).map(
                a -> {
                    AuthAuthorityEntity ae = new AuthAuthorityEntity();
                    ae.setUserId(cratedAuthUser.getId());
                    ae.setAuthority(a);
                    return ae;
                }
        ).toArray(AuthAuthorityEntity[]::new);

        new AuthAuthorityDaoSpringJdbc(getDataSource(CFG.authJdbcUrl()))
                .create(userAuthorities);

        return UdUserJson.fromEntity(new UdUserDaoSpringJdbc(getDataSource(CFG.userdataJdbcUrl()))
                        .createUser(UdUserEntity.fromJson(user)),
                null);
    }

    public UdUserJson createUser(UdUserJson user) {
        return UdUserJson.fromEntity(xaTransaction(
                Connection.TRANSACTION_READ_COMMITTED, new Databases.XaFunction<>(
                        connection -> {
                            AuthUserEntity authUserEntity = new AuthUserEntity();
                            authUserEntity.setUsername(user.username());
                            authUserEntity.setPassword(pe.encode("1234"));
                            authUserEntity.setEnabled(true);
                            authUserEntity.setAccountNonExpired(true);
                            authUserEntity.setAccountNonLocked(true);
                            authUserEntity.setCredentialsNonExpired(true);
                            new AuthUserDaoJdbc(connection).create(authUserEntity);
                            new AuthAuthorityDaoJdbc(connection).create(Arrays.stream(
                                            Authority.values())
                                    .map(a -> {
                                                AuthAuthorityEntity ae = new AuthAuthorityEntity();
                                                ae.setUserId(authUserEntity.getId());
                                                System.out.println(authUserEntity.getId());
                                                ae.setAuthority(a);
                                                return ae;
                                            }
                                    ).toArray(AuthAuthorityEntity[]::new));
                            return null;
                        },
                        CFG.authJdbcUrl()),
                new Databases.XaFunction<>(
                        connection -> {
                            UdUserEntity userEntity = new UdUserEntity();
                            userEntity.setUsername(user.username());
                            userEntity.setFullname(user.fullname());
                            userEntity.setCurrency(user.currency());
                            return new UdUserDaoJdbc(connection).createUser(userEntity);
                        },
                        CFG.userdataJdbcUrl()
                )
        ), null);
    }

    public List<UdUserJson> getAllUser() {
        return Databases.transaction(connection -> {
            List<UdUserEntity> entities = new UdUserDaoJdbc(connection).findAll();
            return entities.stream()
                    .map(m -> UdUserJson.fromEntity(m, null))
                    .toList();
        }, CFG.userdataJdbcUrl(), Connection.TRANSACTION_READ_COMMITTED);
    }

    public List<UdUserJson> getAllUdUserSpringJdbc() {
        return new UdUserDaoSpringJdbc(getDataSource(CFG.userdataJdbcUrl()))
                .findAll().stream()
                .map(m -> UdUserJson.fromEntity(m, null))
                .toList();
    }
}
