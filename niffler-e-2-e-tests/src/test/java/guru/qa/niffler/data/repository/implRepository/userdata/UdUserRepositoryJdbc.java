package guru.qa.niffler.data.repository.implRepository.userdata;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.ImplDao.userdata.UdUserDaoJdbc;
import guru.qa.niffler.data.dao.userdata.UdUserDao;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.data.repository.UdUserRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.jdbc.Connections.holder;

public class UdUserRepositoryJdbc implements UdUserRepository {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.userdataJdbcUrl();
    private final UdUserDao udUserDao = new UdUserDaoJdbc();

    @Override
    public UdUserEntity create(UdUserEntity user) {
        return udUserDao.create(user);
    }

    @Override
    public UdUserEntity update(UdUserEntity user) {
        return udUserDao.update(user);
    }

    @Override
    public Optional<UdUserEntity> findById(UUID id) {
        return udUserDao.findById(id);
    }

    @Override
    public Optional<UdUserEntity> findByUsername(String username) {
        return udUserDao.findByUsername(username);
    }

    // Friendship table
    @Override
    public void sendInvitation(UdUserEntity requester, UdUserEntity addressee) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) " +
                        "VALUES (?, ?, ?, ?)"
        )) {
            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setString(3, FriendshipStatus.PENDING.name());
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addFriend(UdUserEntity requester, UdUserEntity addressee) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) " +
                        "VALUES (?, ?, ?, ?)"
        )) {
            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setString(3, FriendshipStatus.ACCEPTED.name());
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ps.addBatch();

            ps.setObject(1, addressee.getId());
            ps.setObject(2, requester.getId());
            ps.setString(3, FriendshipStatus.ACCEPTED.name());
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ps.addBatch();
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(UdUserEntity user) {
        udUserDao.remove(user);
    }
}
