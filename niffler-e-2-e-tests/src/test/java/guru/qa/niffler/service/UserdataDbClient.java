package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.ImplDao.auth.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.ImplDao.auth.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.ImplDao.auth.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.ImplDao.auth.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.ImplDao.userdata.UdUserDaoJdbc;
import guru.qa.niffler.data.dao.ImplDao.userdata.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.auth.AuthAuthorityDao;
import guru.qa.niffler.data.dao.auth.AuthUserDao;
import guru.qa.niffler.data.dao.userdata.UdUserDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UdUserRepository;
import guru.qa.niffler.data.repository.implRepository.auth.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.implRepository.userdata.UdUserRepositoryJdbc;
import guru.qa.niffler.data.repository.implRepository.userdata.UdUserRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.JdbcTransactionsTemplate;
import guru.qa.niffler.data.tpl.XaTransactionsTemplate;
import guru.qa.niffler.model.UdUserJson;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserdataDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    // Spring JDBC
    private final AuthUserDao authUserDaoSpringJdbc = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDaoSpringJdbc = new AuthAuthorityDaoSpringJdbc();
    private final UdUserDao udUserDaoSpringJdbc = new UdUserDaoSpringJdbc();
    // JDBC
    private final AuthUserDao authUserDaoJdbc = new AuthUserDaoJdbc();
    private final AuthAuthorityDao authAuthorityDaoJdbc = new AuthAuthorityDaoJdbc();
    private final UdUserDao udUserDaoJdbc = new UdUserDaoJdbc();
    // Repository JDBC
    private final AuthUserRepository authUserRepositoryJdbc = new AuthUserRepositoryJdbc();
    private final UdUserRepository udUserRepositoryJdbc = new UdUserRepositoryJdbc();
    // Repository Spring JDBC
    private final UdUserRepository udUserRepositorySpringJdbc = new UdUserRepositorySpringJdbc();

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
    public UdUserJson creatUserJdbcNotTransaction(UdUserJson user) {
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
                    ae.setUser(cratedAuthUser);
                    ae.setAuthority(a);
                    return ae;
                }
        ).toArray(AuthAuthorityEntity[]::new);

        authAuthorityDaoJdbc.create(userAuthorities);

        return UdUserJson.fromEntity(
                udUserDaoJdbc.createUser(UdUserEntity.fromJson(user)),
                null);
    }

    // JDBC Транзакция с использованием ChainedTransactionManager
    public UdUserJson createUserJdbcTransaction(UdUserJson user) {
        return txTemplate.execute(action -> {
            AuthUserEntity authUser = new AuthUserEntity();
            authUser.setUsername(user.username());
            authUser.setPassword(pe.encode("1234"));
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
            authUserRepositoryJdbc.create(authUser);
            return UdUserJson.fromEntity(
                    udUserDaoJdbc.createUser(UdUserEntity.fromJson(user)),
                    null);
        });
    }

    // JDBC без транзакции
    public List<UdUserJson> getAllUserJdbcNotTransaction() {
        List<UdUserEntity> entities = udUserDaoJdbc.findAll();
        return entities.stream()
                .map(m -> UdUserJson.fromEntity(m, null))
                .toList();
    }

    // JDBC транзакция с использованием ChainedTransactionManager
    public List<UdUserJson> getAllUserJdbcTransaction() {
        return txTemplate.execute(action -> {
            List<UdUserEntity> entities = udUserDaoJdbc.findAll();
            return entities.stream()
                    .map(m -> UdUserJson.fromEntity(m, null))
                    .toList();
        });
    }

    // SpringJdbc без транзакции
    public UdUserJson creatUserSpringJdbcNotTransaction(UdUserJson user) {
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
                    ae.setUser(cratedAuthUser);
                    ae.setAuthority(a);
                    return ae;
                }
        ).toArray(AuthAuthorityEntity[]::new);

        authAuthorityDaoSpringJdbc.create(userAuthorities);

        return UdUserJson.fromEntity(
                udUserDaoSpringJdbc.createUser(UdUserEntity.fromJson(user)),
                null);
    }

    // SpringJdbc транзакция с использованием ChainedTransactionManager
    public UdUserJson createUserSpringJdbcTransaction(UdUserJson user) {
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
                        ae.setUser(cratedAuthUser);
                        ae.setAuthority(a);
                        return ae;
                    }
            ).toArray(AuthAuthorityEntity[]::new);

            authAuthorityDaoSpringJdbc.create(userAuthorities);

            return UdUserJson.fromEntity(
                    udUserDaoSpringJdbc.createUser(UdUserEntity.fromJson(user)),
                    null);
        });
    }

    // SpringJdbc без транзакции
    public List<UdUserJson> getAllUserSpringJdbcNotTransaction() {
        List<UdUserEntity> entities = udUserDaoSpringJdbc.findAll();
        return entities.stream()
                .map(m -> UdUserJson.fromEntity(m, null))
                .toList();
    }

    // SpringJdbc транзакция с использованием ChainedTransactionManager
    public List<UdUserJson> getAllUserSpringJdbcTransaction() {
        return txTemplate.execute(action -> {
            List<UdUserEntity> entities = udUserDaoSpringJdbc.findAll();
            return entities.stream()
                    .map(m -> UdUserJson.fromEntity(m, null))
                    .toList();
        });
    }

    // REPOSITORY JDBC
    public UdUserJson getUserByNameJdbc(String name) {
        return jdbcTxTemplate.execute(() -> {
            Optional<UdUserEntity> ue = udUserRepositoryJdbc.findByUsername(name);
            return UdUserJson.fromEntity(ue.orElseThrow(), null);
        });
    }

    public void createFriendshipRequestJdbc(UdUserJson requester, UdUserJson addressee) {
        jdbcTxTemplate.execute(() -> udUserRepositoryJdbc.addIncomeInvitation(
                UdUserEntity.fromJson(requester), UdUserEntity.fromJson(addressee)
        ));
    }


    // REPOSITORY SPRING JDBC
    public UdUserJson getUserByNameSpringJdbc(String name) {
        return jdbcTxTemplate.execute(() -> {
            Optional<UdUserEntity> user = udUserRepositorySpringJdbc.findByUsername(name);
            return UdUserJson.fromEntity(user.orElseThrow(), null);
        });
    }

    public void createFriendshipRequestSpringJdbc(UdUserJson requester, UdUserJson addressee) {
        jdbcTxTemplate.execute(() -> udUserRepositorySpringJdbc.addIncomeInvitation(
                UdUserEntity.fromJson(requester), UdUserEntity.fromJson(addressee)
        ));
    }

    public void deleteFriendShipSpringJdbc(UdUserJson requester, UdUserJson addressee) {
        jdbcTxTemplate.execute(() -> udUserRepositorySpringJdbc.deleteFriendshipInvitation(
                UdUserEntity.fromJson(requester), UdUserEntity.fromJson(addressee)
        ));
    }
}
