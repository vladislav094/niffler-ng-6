package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.auth.AuthAuthorityDao;
import guru.qa.niffler.data.dao.auth.AuthUserDao;
import guru.qa.niffler.data.dao.userdata.UserdataUserDao;
import guru.qa.niffler.data.daoImplementation.auth.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.daoImplementation.auth.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.daoImplementation.auth.AuthUserDaoJdbc;
import guru.qa.niffler.data.daoImplementation.auth.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.daoImplementation.userdata.UserdataUserDaoJdbc;
import guru.qa.niffler.data.daoImplementation.userdata.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.JdbcTransactionsTemplate;
import guru.qa.niffler.data.tpl.XaTransactionsTemplate;
import guru.qa.niffler.model.UserdataUserJson;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;

public class UserdataDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    // SpringJdbc
    private final AuthUserDao authUserDaoSpringJdbc = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDaoSpringJdbc = new AuthAuthorityDaoSpringJdbc();
    private final UserdataUserDao userdataUserDaoSpringJdbc = new UserdataUserDaoSpringJdbc();
    // Jdbc
    private final AuthUserDao authUserDaoJdbc = new AuthUserDaoJdbc();
    private final AuthAuthorityDao authAuthorityDaoJdbc = new AuthAuthorityDaoJdbc();
    private final UserdataUserDao userdataUserDaoJdbc = new UserdataUserDaoJdbc();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(
                            DataSources.getDataSource(CFG.userdataJdbcUrl())),
                    new JdbcTransactionManager(DataSources.getDataSource(CFG.authJdbcUrl()))
            ));

    private final JdbcTransactionsTemplate jdbcTxTemplate = new JdbcTransactionsTemplate(
            CFG.userdataJdbcUrl()
    );

    private final XaTransactionsTemplate xaTxTemplate = new XaTransactionsTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    // JDBC без транзакции
    public UserdataUserJson creatUserJdbcNotTransaction(UserdataUserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("1234"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity cratedAuthUser = authUserDaoJdbc.create(authUser);

        AuthAuthorityEntity[] userAuthorities = Arrays.stream(Authority.values()).map(
                a -> {
                    AuthAuthorityEntity ae = new AuthAuthorityEntity();
                    ae.setUserId(cratedAuthUser.getId());
                    ae.setAuthority(a);
                    return ae;
                }
        ).toArray(AuthAuthorityEntity[]::new);

        authAuthorityDaoJdbc.create(userAuthorities);

        return UserdataUserJson.fromEntity(
                userdataUserDaoJdbc.createUser(UdUserEntity.fromJson(user)),
                null);
    }

    // JDBC Транзакция с использованием ChainedTransactionManager
    public UserdataUserJson createUserJdbcTransaction(UserdataUserJson user) {
        return txTemplate.execute(action -> {
            AuthUserEntity authUser = new AuthUserEntity();
            authUser.setUsername(user.username());
            authUser.setPassword(pe.encode("1234"));
            authUser.setEnabled(true);
            authUser.setAccountNonExpired(true);
            authUser.setAccountNonLocked(true);
            authUser.setCredentialsNonExpired(true);

            AuthUserEntity cratedAuthUser = authUserDaoJdbc.create(authUser);

            AuthAuthorityEntity[] userAuthorities = Arrays.stream(Authority.values()).map(
                    a -> {
                        AuthAuthorityEntity ae = new AuthAuthorityEntity();
                        ae.setUserId(cratedAuthUser.getId());
                        ae.setAuthority(a);
                        return ae;
                    }
            ).toArray(AuthAuthorityEntity[]::new);

            authAuthorityDaoJdbc.create(userAuthorities);

            return UserdataUserJson.fromEntity(
                    userdataUserDaoJdbc.createUser(UdUserEntity.fromJson(user)),
                    null);
        });
    }

    // JDBC без транзакции
    public List<UserdataUserJson> getAllUserJdbcNotTransaction() {
        List<UdUserEntity> entities = userdataUserDaoJdbc.findAll();
        return entities.stream()
                .map(m -> UserdataUserJson.fromEntity(m, null))
                .toList();
    }

    // JDBC транзакция с использованием ChainedTransactionManager
    public List<UserdataUserJson> getAllUserJdbcTransaction() {
        return txTemplate.execute(action -> {
            List<UdUserEntity> entities = userdataUserDaoJdbc.findAll();
            return entities.stream()
                    .map(m -> UserdataUserJson.fromEntity(m, null))
                    .toList();
        });
    }

    // SpringJdbc без транзакции
    public UserdataUserJson creatUserSpringJdbcNotTransaction(UserdataUserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("1234"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity cratedAuthUser = authUserDaoSpringJdbc.create(authUser);

        AuthAuthorityEntity[] userAuthorities = Arrays.stream(Authority.values()).map(
                a -> {
                    AuthAuthorityEntity ae = new AuthAuthorityEntity();
                    ae.setUserId(cratedAuthUser.getId());
                    ae.setAuthority(a);
                    return ae;
                }
        ).toArray(AuthAuthorityEntity[]::new);

        authAuthorityDaoSpringJdbc.create(userAuthorities);

        return UserdataUserJson.fromEntity(
                userdataUserDaoSpringJdbc.createUser(UdUserEntity.fromJson(user)),
                null);
    }

    // SpringJdbc транзакция с использованием ChainedTransactionManager
    public UserdataUserJson createUserSpringJdbcTransaction(UserdataUserJson user) {
        return txTemplate.execute(action -> {
            AuthUserEntity authUser = new AuthUserEntity();
            authUser.setUsername(user.username());
            authUser.setPassword(pe.encode("1234"));
            authUser.setEnabled(true);
            authUser.setAccountNonExpired(true);
            authUser.setAccountNonLocked(true);
            authUser.setCredentialsNonExpired(true);

            AuthUserEntity cratedAuthUser = authUserDaoSpringJdbc.create(authUser);

            AuthAuthorityEntity[] userAuthorities = Arrays.stream(Authority.values()).map(
                    a -> {
                        AuthAuthorityEntity ae = new AuthAuthorityEntity();
                        ae.setUserId(cratedAuthUser.getId());
                        ae.setAuthority(a);
                        return ae;
                    }
            ).toArray(AuthAuthorityEntity[]::new);

            authAuthorityDaoSpringJdbc.create(userAuthorities);

            return UserdataUserJson.fromEntity(
                    userdataUserDaoSpringJdbc.createUser(UdUserEntity.fromJson(user)),
                    null);
        });
    }

    // SpringJdbc без транзакции
    public List<UserdataUserJson> getAllUserSpringJdbcNotTransaction() {
        List<UdUserEntity> entities = userdataUserDaoSpringJdbc.findAll();
        return entities.stream()
                .map(m -> UserdataUserJson.fromEntity(m, null))
                .toList();
    }

    // SpringJdbc транзакция с использованием ChainedTransactionManager
    public List<UserdataUserJson> getAllUserSpringJdbcTransaction() {
        return txTemplate.execute(action -> {
            List<UdUserEntity> entities = userdataUserDaoSpringJdbc.findAll();
            return entities.stream()
                    .map(m -> UserdataUserJson.fromEntity(m, null))
                    .toList();
        });
    }
}
