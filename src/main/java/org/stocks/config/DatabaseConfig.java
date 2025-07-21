package org.stocks.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DatabaseConfig {

    public static String DB_USERNAME;
    public static String DB_PASSWORD;

    @Bean
    public DataSource dataSource(){
        MysqlDataSource ds = new MysqlDataSource();
        ds.setURL("jdbc:mysql://localhost:3306/stockportfolio");
        ds.setUser(DB_USERNAME);
        ds.setPassword(DB_PASSWORD);
        return ds;
    }
}
