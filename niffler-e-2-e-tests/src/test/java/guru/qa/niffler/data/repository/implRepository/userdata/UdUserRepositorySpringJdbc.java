package guru.qa.niffler.data.repository.implRepository.userdata;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.ImplDao.userdata.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.userdata.UdUserDao;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.data.repository.UdUserRepository;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.DataSources.getDataSource;

public class UdUserRepositorySpringJdbc implements UdUserRepository {

    private static final Config CFG = Config.getInstance();
    private final UdUserDao udUserDao = new UdUserDaoSpringJdbc();

    @Override
    public UdUserEntity create(UdUserEntity user) {
        return udUserDao.createUser(user);
    }

    @Override
    public Optional<UdUserEntity> findById(UUID id) {
        return udUserDao.findById(id);
    }

    @Override
    public Optional<UdUserEntity> findByUsername(String username) {
        return udUserDao.findByUsername(username);
    }

    @Override
    public List<UdUserEntity> findAll() {
        return udUserDao.findAll();
    }

    @Override
    public void delete(UdUserEntity user) {
        udUserDao.delete(user);
    }

    @Override
    public void addIncomeInvitation(UdUserEntity requester, UdUserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(
                "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) " +
                        "VALUES (?, ?, ?, ?)",
                requester.getId(),
                addressee.getId(),
                FriendshipStatus.PENDING.name(),
                new Date(System.currentTimeMillis()));
    }

    @Override
    public void addOutcomeInvitation(UdUserEntity requester, UdUserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(
                "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) " +
                        "VALUES (?, ?, ?, ?)",
                addressee.getId(),
                requester.getId(),
                FriendshipStatus.PENDING.name(),
                new Date(System.currentTimeMillis())
        );
    }

    @Override
    public void addFriend(UdUserEntity requester, UdUserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(
                "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) " +
                        "VALUES (?, ?, ?, ?)",
                requester.getId(),
                addressee.getId(),
                FriendshipStatus.ACCEPTED.name(),
                new Date(System.currentTimeMillis())
        );
        jdbcTemplate.update(
                "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) " +
                        "VALUES (?, ?, ?, ?)",
                addressee.getId(),
                requester.getId(),
                FriendshipStatus.ACCEPTED.name(),
                new Date(System.currentTimeMillis())
        );
    }

    @Override
    public void deleteFriendshipInvitation(UdUserEntity requester, UdUserEntity addressee) {
        new JdbcTemplate(getDataSource(CFG.userdataJdbcUrl())).update(
                "DELETE FROM \"friendship\" WHERE requester_id = ? AND addressee_id = ?",
                requester.getId(), addressee.getId()
        );
    }
}
