package guru.qa.niffler.data.jpa;

import guru.qa.niffler.data.jdbc.DataSources;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityManagers {

    private static final Map<String, EntityManagerFactory> emfs = new ConcurrentHashMap<>();

    private EntityManagers() {
    }

    public static EntityManager em(String jdbcUrl) {
        return emfs.computeIfAbsent(
                jdbcUrl,
                key -> {
                    DataSources.dataSource(jdbcUrl);
                    return Persistence.createEntityManagerFactory(jdbcUrl);
                }
        ).createEntityManager();
    }

    public static void closeAllEmfs() {
        emfs.values().forEach(EntityManagerFactory::close);
    }
}
