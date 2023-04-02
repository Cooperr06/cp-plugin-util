package de.cooperr.cppluginutil;

import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Database connector for MySQL
 */
public class DatabaseConnector {

    private final MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
    private final PaperPlugin plugin;

    /**
     * Builds the data source and finally tests it
     *
     * @param plugin   plugin to which this connector should belong
     * @param host     address to the database, if null default host will be used
     * @param port     port to connect, if null default port will be used
     * @param database database to use
     * @param user     user to be used for login
     * @param password password to be used for login
     */
    public DatabaseConnector(@NotNull PaperPlugin plugin, @Nullable String host, @Nullable Integer port, @NotNull String database,
                             @NotNull String user, @NotNull String password) {
        this.plugin = plugin;

        dataSource.setServerName(host == null ? ConnectionUrl.DEFAULT_HOST : host);
        dataSource.setPortNumber(port == null ? ConnectionUrl.DEFAULT_PORT : port);
        dataSource.setDatabaseName(database);
        dataSource.setUser(user);
        dataSource.setPassword(password);

        testDataSource(dataSource);
    }

    private void testDataSource(DataSource dataSource) {
        try (var connection = dataSource.getConnection()) {
            if (!connection.isValid(1000)) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not establish database connection", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    /**
     * Executes a sql file
     *
     * @param file sql file which will be executed
     * @return result set or null, if an error occurred or there is no result
     */
    @Nullable
    public ResultSet executeSqlFile(@NotNull File file) {
        var setup = "";

        try (var inputStream = new FileInputStream(file)) {
            setup = new String(inputStream.readAllBytes());
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not read sql file \"%s\"".formatted(file.getAbsolutePath()), e);
            return null;
        }

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(setup)) {
            if (statement.execute()) {
                return statement.getResultSet();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to execute sql file \"%s\"".formatted(file.getAbsolutePath()), e);
            return null;
        }
        return null;
    }

    public ResultSet executeSql(String sql, @Nullable Object... args) {
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i, args);
            }
            if (statement.execute()) {
                return statement.getResultSet();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to execute sql script \"%s\" with args \"%s\"".formatted(sql, args), e);
            return null;
        }
        return null;
    }

    @NotNull
    public MysqlDataSource getDataSource() {
        return dataSource;
    }
}
