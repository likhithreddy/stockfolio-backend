package org.stocks.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Value("${DATABASE_URL:jdbc:mysql://localhost:3306/stockportfolio}")
    private String databaseUrl;

    @Value("${DATABASE_USERNAME:root}")
    private String databaseUsername;

    @Value("${DATABASE_PASSWORD:}")
    private String databasePassword;

    @Bean
    public DataSource dataSource(){
        MysqlDataSource ds = new MysqlDataSource();
        ds.setURL(databaseUrl);
        ds.setUser(databaseUsername);
        ds.setPassword(databasePassword);
        return ds;
    }
}