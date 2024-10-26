package guru.qa.niffler.data.jpa;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import guru.qa.niffler.data.tpl.DataSources;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.commons.lang3.StringUtils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class EntityManagers {

    private static final Map<String, EntityManagerFactory> emfs = new ConcurrentHashMap<>();

    private EntityManagers() {
    }

    public static EntityManager em(String jdbcUrl) {
        return emfs.computeIfAbsent(
                jdbcUrl,
                key -> {
                    DataSources.getDataSource(jdbcUrl);
                    return Persistence.createEntityManagerFactory(jdbcUrl);
                }
        ).createEntityManager();
    }
}
