package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.daoImplementation.auth.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.daoImplementation.auth.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.daoImplementation.auth.AuthUserDaoJdbc;
import guru.qa.niffler.data.daoImplementation.auth.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.daoImplementation.userdata.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.model.AuthAuthorityJson;
import guru.qa.niffler.model.AuthUserJson;
import guru.qa.niffler.model.UdUserJson;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static guru.qa.niffler.data.Databases.getDataSource;

public class AuthDbClient {

    private static final Config CFG = Config.getInstance();

    //Authority table
    public List<AuthAuthorityJson> getAllAuthority() {
        return Databases.transaction(connection -> {
            List<AuthAuthorityEntity> entities = new AuthAuthorityDaoJdbc(connection).findAll();
            return entities.stream()
                    .map(AuthAuthorityJson::fromEntity)
                    .toList();
        }, CFG.authJdbcUrl(), Connection.TRANSACTION_READ_COMMITTED);
    }

    //User table
    public List<AuthUserJson> getAllAuthUser() {
        return Databases.transaction(connection -> {
            List<AuthUserEntity> entities = new AuthUserDaoJdbc(connection).findAll();
            return entities.stream()
                    .map(AuthUserJson::fromEntity)
                    .toList();
        }, CFG.authJdbcUrl(), Connection.TRANSACTION_READ_COMMITTED);
    }

    public List<AuthUserJson> getAllAuthUserSpringJdbc() {
        return new AuthUserDaoSpringJdbc(getDataSource(CFG.authJdbcUrl()))
                .findAll().stream()
                .map(AuthUserJson::fromEntity)
                .toList();
    }

    public List<AuthAuthorityJson> getAllAuthAuthoritySpringJdbc() {
        return new AuthAuthorityDaoSpringJdbc(getDataSource(CFG.authJdbcUrl()))
                .findAll().stream()
                .map(AuthAuthorityJson::fromEntity)
                .toList();
    }
}
