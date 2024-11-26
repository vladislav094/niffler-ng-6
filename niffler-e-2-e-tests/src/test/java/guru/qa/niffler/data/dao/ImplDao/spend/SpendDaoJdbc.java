package guru.qa.niffler.data.dao.ImplDao.spend;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.spend.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.jdbc.Connections.holder;

public class SpendDaoJdbc implements SpendDao {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.spendJdbcUrl();

    @Override
    public SpendEntity create(SpendEntity spend) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());

            ps.executeUpdate();

            UUID generateKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generateKey = rs.getObject("id", UUID.class);
                } else throw new SQLException("Can't find ID in ResultSet");
            }

            spend.setId(generateKey);
            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        try(PreparedStatement ps = holder(url).connection().prepareStatement(
                "UPDATE spend SET spend_date = ?, currency = ?, amount = ?, description = ? WHERE id = ?"
        )) {
            ps.setDate(1, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setString(2, spend.getCurrency().name());
            ps.setDouble(3, spend.getAmount());
            ps.setString(4, spend.getDescription());
            ps.setObject(5, spend.getId());
            int rowsUpdate = ps.executeUpdate();
            if (rowsUpdate == 0) {
                throw new SQLException("Failed to update spend. Spend not found");
            }
            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM spend WHERE  id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.ofNullable(SpendEntityRowMapper.instance.mapRow(rs, rs.getRow()));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAll() {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM spend")) {
            ps.execute();
            List<SpendEntity> result = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    result.add(SpendEntityRowMapper.instance.mapRow(rs, rs.getRow()));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(SpendEntity spend) {
        UUID categoryId = spend.getId();
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "DELETE FROM spend WHERE id = ?"
        )) {
            ps.setObject(1, categoryId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
