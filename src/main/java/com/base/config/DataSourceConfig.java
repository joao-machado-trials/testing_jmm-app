package com.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DataSourceConfig {

    @Value("${DATABASE_URL}")
    private String databaseUrl;

    @Bean
    public DataSource dataSource() {
        try {
            // Converter o formato postgres://user:pass@host:port/db para JDBC
            URI dbUri = new URI(databaseUrl);

            String[] userInfo = dbUri.getUserInfo().split(":");
            DriverManagerDataSource dataSource = getDriverManagerDataSource(userInfo, dbUri);

            return dataSource;
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid DATABASE_URL format", e);
        }
    }

    private static DriverManagerDataSource getDriverManagerDataSource(String[] userInfo, URI dbUri) {
        String username = userInfo[0];
        String password = userInfo.length > 1 ? userInfo[1] : "";

        String url = String.format("jdbc:postgresql://%s:%d%s",
                dbUri.getHost(),
                dbUri.getPort(),
                dbUri.getPath()
        );

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver");
        return dataSource;
    }
}
