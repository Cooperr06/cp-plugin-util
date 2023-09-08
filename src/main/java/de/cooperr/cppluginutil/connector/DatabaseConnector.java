package de.cooperr.cppluginutil.connector;

import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import de.cooperr.cppluginutil.base.PaperPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Database connector for MySQL
 */
public class DatabaseConnector {

    private final PaperPlugin plugin;

    private final MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();

    /**
     * Builds the data source and finally tests it
     *
     * @param plugin   plugin to which this connector should belong
     * @param host     address to the database, if null the default host will be used
     * @param port     port to connect, if null the default port will be used
     * @param database database to use
     * @param user     user to use for login
     * @param password password to use for login
     */
    public DatabaseConnector(@NotNull PaperPlugin plugin, @Nullable String host, @Nullable Integer port, @Nullable String database,
                             @NotNull String user, @Nullable String password) {
        this.plugin = plugin;

        dataSource.setServerName(host == null ? ConnectionUrl.DEFAULT_HOST : host);
        dataSource.setPortNumber(port == null ? ConnectionUrl.DEFAULT_PORT : port);
        if (database != null) {
            dataSource.setDatabaseName(database);
        }
        dataSource.setUser(user);
        if (password != null) {
            dataSource.setPassword(password);
        }

        testDataSource();
    }

    public void testDataSource() {
        try (var connection = dataSource.getConnection()) {
            if (!connection.isValid(1000)) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to database", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    /**
     * Executes a sql command
     *
     * @param sql  sql command
     * @param args arguments for the command
     */
    public void executeSql(@NotNull String sql, @Nullable Object... args) {
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i, args);
            }
            statement.execute();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to execute sql script \"%s\" with args \"%s\"".formatted(sql, args), e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    /**
     * Executes a sql file
     *
     * @param file sql file which will be executed
     */
    public void executeSqlFile(@NotNull File file) {
        var setup = "";
        try (var inputStream = new FileInputStream(file)) {
            setup = new String(inputStream.readAllBytes());
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to read sql file \"%s\"".formatted(file.getName()), e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }

        if (setup.split(";").length > 1) {
            for (var command : setup.split(";")) {
                executeSql(command);
            }
            return;
        }

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(setup)) {
            statement.execute();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to execute sql file \"%s\"".formatted(file.getName()), e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    @NotNull
    public MysqlDataSource dataSource() {
        return dataSource;
    }
}
