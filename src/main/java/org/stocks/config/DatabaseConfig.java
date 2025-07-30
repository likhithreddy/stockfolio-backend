package org.stocks.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Value("${DB_HOST:localhost}")
    private String dbHost;

    @Value("${DB_PORT:3306}")
    private String dbPort;

    @Value("${DB_NAME:stockportfolio}")
    private String dbName;

    @Value("${DB_USERNAME:root}")
    private String dbUsername;

    @Value("${DB_PASSWORD:}")
    private String dbPassword;

    @Bean
    public DataSource dataSource(){
        MysqlDataSource ds = new MysqlDataSource();
        String url = String.format("jdbc:mysql://%s:%s/%s", dbHost, dbPort, dbName);
        ds.setURL(url);
        ds.setUser(dbUsername);
        ds.setPassword(dbPassword);
        return ds;
    }
}