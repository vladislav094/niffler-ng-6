package guru.qa.niffler.config;

import javax.annotation.Nonnull;
import java.util.List;

public interface Config {

    static Config getInstance() {
        return "docker".equals(System.getProperty("test.env"))
                ? DockerConfig.INSTANCE
                : LocalConfig.INSTANCE;
    }

    String frontUrl();

    String spendUrl();

    String spendJdbcUrl();

    String authUrl();

    String authJdbcUrl();

    String gatewayUrl();

    String userdataUrl();

    String userdataJdbcUrl();

    String currencyJdbcUrl();

    String currencyGrpcAddress();

    String kafkaAddress();

    default int currencyGrpcPort() {
        return 8092;
    }

    @Nonnull
    default String ghUrl() {
        return "https://api.github.com/";
    }

    default List<String> kafkaTopics() {
        return List.of("users");
    }
}
