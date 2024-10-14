package guru.qa.niffler.data.daoImplementation.userdata;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.userdata.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.DataSources.getDataSource;

public class UserdataUserDaoSpringJdbc implements UserdataUserDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public UserdataUserEntity createUser(UserdataUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(CFG.userdataJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name)" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public Optional<UserdataUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE id = ?",
                        UserdataUserEntityRowMapper.instance,
                        id
                )
        );
    }

    @Override
    public Optional<UserdataUserEntity> findByUsername(String username) {
        return Optional.ofNullable(
                new JdbcTemplate(getDataSource(CFG.userdataJdbcUrl())).queryForObject(
                        "SELECT * FROM \"user\" WHERE username = ?",
                        UserdataUserEntityRowMapper.instance,
                        username
                )
        );
    }

    @Override
    public List<UserdataUserEntity> findAll() {
        return new JdbcTemplate(getDataSource(CFG.userdataJdbcUrl())).query(
                "SELECT * FROM \"user\"",
                UserdataUserEntityRowMapper.instance
        );
    }

    @Override
    public void delete(UserdataUserEntity user) {
        new JdbcTemplate(getDataSource(CFG.userdataJdbcUrl())).update(
                "DELETE FROM \"user\" WHERE id = ?",
                user.getId()
        );
    }
}
