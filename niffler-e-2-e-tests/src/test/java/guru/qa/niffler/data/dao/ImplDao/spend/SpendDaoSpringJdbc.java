package guru.qa.niffler.data.dao.ImplDao.spend;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.spend.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.DataSources.getDataSource;

public class SpendDaoSpringJdbc implements SpendDao {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.spendJdbcUrl();

    @Override
    public SpendEntity create(SpendEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(url));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                                    "VALUES (?, ?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, spend.getUsername());
                    ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
                    ps.setString(3, spend.getCurrency().name());
                    ps.setDouble(4, spend.getAmount());
                    ps.setString(5, spend.getDescription());
                    ps.setObject(6, spend.getCategory().getId());
                    return ps;
                }, kh);
        UUID generatedKey = (UUID) kh.getKeys().get("id");
        spend.setId(generatedKey);

        return spend;
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(url));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE spend SET spend_date = ?," +
                            "currency = ?," +
                            "amount = ?," +
                            "description = ?" +
                            " WHERE id = ?",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setString(2, spend.getCurrency().name());
            ps.setDouble(3, spend.getAmount());
            ps.setString(4, spend.getDescription());
            ps.setObject(5, spend.getId());
            return ps;
        }, kh);
        return spend;
    }


    @Override
    public Optional<SpendEntity> findById(UUID id) {
        return Optional.ofNullable(
                new JdbcTemplate(getDataSource(url)).queryForObject(
                        "SELECT * FROM spend WHERE id = ?",
                        SpendEntityRowMapper.instance,
                        id
                )
        );
    }

    @Override
    public List<SpendEntity> findAll() {
        return new JdbcTemplate(getDataSource(url)).query(
                "SELECT * FROM spend",
                SpendEntityRowMapper.instance
        );
    }

    @Override
    public void remove(SpendEntity spend) {
        new JdbcTemplate(getDataSource(url)).update(
                "DELETE FROM spend WHERE id = ?",
                spend.getId()
        );
    }
}
