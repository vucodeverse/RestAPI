package com.phongvu.restapi.configuration;

import com.phongvu.orm.ConnectionProvider;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class OrmConfig {

    private final DataSource dataSource;

    public OrmConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() {
        if (dataSource instanceof HikariDataSource) {
            ConnectionProvider.init((HikariDataSource) dataSource);
        }
    }
}
