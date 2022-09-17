package de.cooperr.cppluginutil;

import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Database connector for MySQL
 */
@Getter
public class DatabaseConnector {
    
    private final MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
    private final PaperPlugin plugin;
    
    /**
     * @param host     address to the database
     * @param port     port to connect
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
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(1000)) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not establish database connection", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }
    
    public boolean executeSqlFile(@NotNull File file) {
        String setup;
        
        try (InputStream in = new FileInputStream(file)) {
            setup = new String(in.readAllBytes());
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not read SQL file \"%s\"".formatted(file.getAbsolutePath()), e);
            return false;
        }
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(setup)) {
            stmt.execute();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to execute SQL file \"%s\"".formatted(file.getAbsolutePath()), e);
            return false;
        }
        return true;
    }
}
