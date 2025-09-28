package pethorses.storage.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import pethorses.config.ConfigManager;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private final HikariDataSource dataSource;

    public DatabaseManager(ConfigManager config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mariadb://" + config.getDatabaseHost() + ":" + config.getDatabasePort() + "/" + config.getDatabaseName());
        hikariConfig.setUsername(config.getDatabaseUser());
        hikariConfig.setPassword(config.getDatabasePassword());
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setConnectionTimeout(10000);
        hikariConfig.setPoolName("PetHorsesPool");
        hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
        this.dataSource = new HikariDataSource(hikariConfig);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        dataSource.close();
    }
}
