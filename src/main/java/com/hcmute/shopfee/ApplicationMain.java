package com.hcmute.shopfee;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.integration.IntegrationDataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
//@EnableMongoRepositories(basePackages = "com.hcmute.shopfee.repository.database")
@EnableElasticsearchRepositories(basePackages = "com.hcmute.shopfee.repository.elasticsearch")
//@ComponentScan(basePackages = "com.hcmute.shopfee.repository.")
@EnableAsync
@EnableJpaAuditing
public class ApplicationMain {

    public static void main(String[] args) {
//        TimeZone.setDefault(TimeZone.getTimeZone("GMT+7:00"));
        SpringApplication.run(ApplicationMain.class, args);
    }

//    @PostConstruct
//    void started() {
//        TimeZone.setDefault(TimeZone.getTimeZone("GMT+0"));
//    }
}