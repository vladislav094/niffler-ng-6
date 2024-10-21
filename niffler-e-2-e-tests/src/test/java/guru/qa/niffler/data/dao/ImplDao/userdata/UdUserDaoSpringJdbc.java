package guru.qa.niffler.data.dao.ImplDao.userdata;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.userdata.UdUserDao;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
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

public class UdUserDaoSpringJdbc implements UdUserDao {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.userdataJdbcUrl();

    @Override
    public UdUserEntity create(UdUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(url));
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
    public UdUserEntity update(UdUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(url));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE \"user\" SET currency = ?," +
                            "firstname = ?," +
                            "surname = ?," +
                            "photo = ?," +
                            "photo_small = ?," +
                            "full_name = ? WHERE id = ? AND username = ?",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getCurrency().name());
            ps.setString(2, user.getFirstname());
            ps.setString(3, user.getFullname());
            ps.setBytes(4, user.getPhoto());
            ps.setBytes(5, user.getPhotoSmall());
            ps.setString(6, user.getFullname());
            ps.setObject(7, user.getId());
            ps.setString(8, user.getUsername());
            return ps;
        }, kh);
        return user;
    }

    @Override
    public Optional<UdUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(url));
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE id = ?",
                        UserdataUserEntityRowMapper.instance,
                        id
                ));
    }

    @Override
    public Optional<UdUserEntity> findByUsername(String username) {
        return Optional.ofNullable(
                new JdbcTemplate(getDataSource(url)).queryForObject(
                        "SELECT * FROM \"user\" WHERE username = ?",
                        UserdataUserEntityRowMapper.instance,
                        username
                ));
    }

    @Override
    public List<UdUserEntity> findAll() {
        return new JdbcTemplate(getDataSource(url)).query(
                "SELECT * FROM \"user\"",
                UserdataUserEntityRowMapper.instance
        );
    }

    @Override
    public void remove(UdUserEntity user) {
        new JdbcTemplate(getDataSource(url)).update(
                "DELETE FROM \"user\" WHERE id = ?",
                user.getId()
        );
    }
}
