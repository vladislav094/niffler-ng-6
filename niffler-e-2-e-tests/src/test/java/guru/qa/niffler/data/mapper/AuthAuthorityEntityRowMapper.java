package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthAuthorityEntityRowMapper implements RowMapper<AuthAuthorityEntity> {

    public static final AuthAuthorityEntityRowMapper instance = new AuthAuthorityEntityRowMapper();

    private AuthAuthorityEntityRowMapper() {
    }

    @Override
    public AuthAuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthAuthorityEntity result = new AuthAuthorityEntity();
        result.setId(UUID.fromString(rs.getString("id")));
        result.setUserId(UUID.fromString(rs.getString("user_id")));
        result.setAuthority(Authority.valueOf(rs.getString("authority")));
        return result;
    }
}