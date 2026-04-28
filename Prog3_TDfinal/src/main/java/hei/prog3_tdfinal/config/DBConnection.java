package hei.prog3_tdfinal.config;


import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
@Component
public class DBConnection {
    private final String url;
    private final String user;
    private final String password;

    public DBConnection() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        this.url = resolveEnv(dotenv, "DB_URL");
        this.user = resolveEnv(dotenv, "DB_USER");
        this.password = resolveEnv(dotenv, "DB_PASSWORD");

        log.info("Database configuration loaded for {}", url);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private String resolveEnv(Dotenv dotenv, String key) {
        String value = dotenv.get(key);
        if (value == null || value.isBlank()) {
            value = System.getenv(key);
        }
        if (value == null || value.isBlank()) {
            String message = "Missing required database environment variable: " + key;
            log.error(message);
            throw new IllegalStateException(message);
        }
        return value;
    }
}