
package avatar.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;

public class DbManager {

    private static final Logger logger = Logger.getLogger(DbManager.class);

    private static DbManager instance = null;
    private HikariDataSource hikariDataSource;
    private DbExecutor dbExecutor;
    private DbUpdater dbUpdater;
    private DbInserter dbInserter;
    private Connection[] connections;
    private String host;
    private int port;
    private String dbname;
    private String username;
    private String password;
    private String driver;
    private int maxConnections;
    private int minConnections;

    public static DbManager getInstance() {
        if (instance == null) {
            instance = new DbManager();
        }
        return instance;
    }

    private DbManager() {
        try {
            FileInputStream input = new FileInputStream(new File("database.properties"));
            Properties props = new Properties();
            props.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            this.connections = new Connection[4];
            this.driver = props.getProperty("driver");
            this.host = props.getProperty("host");
            this.port = Integer.parseInt(props.getProperty("port"));
            this.dbname = props.getProperty("dbname");
            this.username = props.getProperty("username");
            this.password = props.getProperty("password");
            this.maxConnections = Integer.parseInt(props.getProperty("max_connection"));
            this.minConnections = Integer.parseInt(props.getProperty("min_connection"));
        } catch (IOException ex) {
            logger.error("init ", ex);
        }

    }

    public Connection getConnection() throws SQLException {
        return this.hikariDataSource.getConnection();
    }

    public boolean start() {
        if (this.hikariDataSource != null) {
            logger.warn("DB Connection Pool has already been created.");
            return false;
        } else {
            try {
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.dbname);
                config.setDriverClassName(this.driver);
                config.setUsername(this.username);
                config.setPassword(this.password);
                config.addDataSourceProperty("minimumIdle", this.minConnections);
                config.addDataSourceProperty("maximumPoolSize", this.maxConnections);
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

                this.hikariDataSource = new HikariDataSource(config);
                logger.debug("DB Connection Pool has created.");

                QueryRunner queryRunner = new QueryRunner(this.hikariDataSource);
                this.dbExecutor = new DbExecutor(queryRunner);
                this.dbUpdater = new DbUpdater(queryRunner);
                this.dbInserter = new DbInserter(queryRunner);

                return true;
            } catch (Exception e) {
                logger.error("DB Connection Pool Creation has failed.");
                return false;
            }
        }
    }

    public Connection getConnectionForGame() throws SQLException {
        if (connections[3] == null || connections[3].isClosed() || !connections[3].isValid(0)) {
            connections[3] = getConnection();
        }
        return connections[3];
    }

    public void shutdown() {
        try {
            if (this.hikariDataSource != null) {
                this.hikariDataSource.close();
                logger.debug("DB Connection Pool is shutting down.");
            }
            this.hikariDataSource = null;
        } catch (Exception e) {
            logger.warn("Error when shutting down DB Connection Pool");
        }
    }

    public <T> List<T> selectResultAsListObj(String sql, Class<T> clazz, Object... params) {
        return this.dbExecutor.selectResultAsListObj(sql, clazz, params);
    }

    public <T> T insertResultAsObj(String sql, Object... params) {
        return this.dbInserter.insertResultAsObj(sql, params);
    }

    public int update(String sql, Object... params) {
        return this.dbUpdater.update(sql, params);
    }
}
