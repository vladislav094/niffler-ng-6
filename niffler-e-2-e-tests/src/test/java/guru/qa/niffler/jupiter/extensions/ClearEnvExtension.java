package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.jdbc.DataSources;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class ClearEnvExtension implements SuiteExtension {

    private final Config CFG = Config.getInstance();
    private final JdbcTemplate authDb = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    private final JdbcTemplate spendDb = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    private final JdbcTemplate userdataDb = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

    @Override
    public void beforeAll(ExtensionContext context) {

//        authDb.execute("TRUNCATE TABLE authority, \"user\" CASCADE;");
//        spendDb.execute("TRUNCATE TABLE category, spend CASCADE;");
//        userdataDb.execute("TRUNCATE TABLE friendship, \"user\"  CASCADE;");
    }
}
