package com.hcmute.shopfee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
        SpringApplication.run(ApplicationMain.class, args);
    }
}