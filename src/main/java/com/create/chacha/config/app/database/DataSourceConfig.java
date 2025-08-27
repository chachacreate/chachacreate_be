package com.create.chacha.config.app.database;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

//@Configuration
public class DataSourceConfig {

    @Value("${oracle.wallet.driverClassName}")
    String driverClassName;
    @Value("${oracle.wallet.url}")
    String url;
    @Value("${oracle.wallet.submodule.path}")
    String subModulePath;
    @Value("${oracle.wallet.path}")
    String walletPath;
    @Value("${oracle.wallet.username}")
    String userName;
    @Value("${oracle.wallet.password}")
    String password;

    @Bean
    public javax.sql.DataSource dataSource() {
        String walletDir = Paths.get(subModulePath, walletPath)
                .toAbsolutePath()
                .toString()
                .replace("\\", "/"); // <- 중요!

        String fullUrl = url + walletDir;

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(fullUrl);
        ds.setUsername(userName);
        ds.setPassword(password);
        ds.setDriverClassName(driverClassName);

        System.out.println("Using Wallet folder: " + walletDir);
        return ds;
    }
}