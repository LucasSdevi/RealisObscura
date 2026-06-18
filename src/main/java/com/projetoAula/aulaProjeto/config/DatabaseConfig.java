package com.projetoAula.aulaProjeto.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    @Value("${DATABASE_URL}")
    private String databaseUrl;

    @Bean
    public DataSource dataSource() {
        try {
            URI uri = new URI(databaseUrl);

            String userInfo = uri.getUserInfo();
            String username = userInfo.split(":")[0];
            String password = userInfo.split(":", 2)[1];

            String host = uri.getHost();
            int port = uri.getPort() <= 0 ? 5432 : uri.getPort();
            String dbName = uri.getPath().substring(1);

            String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);

            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(jdbcUrl);
            ds.setUsername(username);
            ds.setPassword(password);
            ds.setDriverClassName("org.postgresql.Driver");
            return ds;

        } catch (Exception e) {
            throw new RuntimeException(
                "Falha ao processar DATABASE_URL: " + databaseUrl + " — " + e.getMessage(), e);
        }
    }
}
