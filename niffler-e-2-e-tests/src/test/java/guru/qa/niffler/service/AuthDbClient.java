package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.auth.AuthAuthorityDao;
import guru.qa.niffler.data.dao.auth.AuthUserDao;
import guru.qa.niffler.data.daoImplementation.auth.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.daoImplementation.auth.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionsTemplate;
import guru.qa.niffler.model.AuthAuthorityJson;
import guru.qa.niffler.model.AuthUserJson;

import java.util.List;

public class AuthDbClient {

    private static final Config CFG = Config.getInstance();

    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();

    private final JdbcTransactionsTemplate jdbcTxTemplate = new JdbcTransactionsTemplate(
            CFG.userdataJdbcUrl());

    //Authority table
    public List<AuthAuthorityJson> getAllAuthority() {
        return jdbcTxTemplate.execute(() -> {
            List<AuthAuthorityEntity> entities = authAuthorityDao.findAll();
            return entities.stream()
                    .map(AuthAuthorityJson::fromEntity)
                    .toList();
        });
    }

    //User table
    public List<AuthUserJson> getAllAuthUser() {
        return jdbcTxTemplate.execute(() -> {
            List<AuthUserEntity> entities = authUserDao.findAll();
            return entities.stream()
                    .map(AuthUserJson::fromEntity)
                    .toList();
        });
    }
}
