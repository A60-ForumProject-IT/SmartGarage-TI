package com.telerikacademy.web.smartgarageti;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class TestConfig {
    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
        dataSource.setUrl(System.getenv("database.url"));
        dataSource.setUsername(System.getenv("database.username"));
        dataSource.setPassword(System.getenv("database.password"));

        return dataSource;
    }
}
