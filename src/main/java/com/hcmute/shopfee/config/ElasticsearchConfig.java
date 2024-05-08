package com.hcmute.shopfee.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
//@EnableElasticsearchRepositories(basePackages = "com.hcmute.shopfee.repository.elasticsearch")
public class ElasticsearchConfig extends ElasticsearchConfiguration {
//    @Value("${spring.elasticsearch.host}")
//    private String host;
//    @Value("${spring.elasticsearch.port}")
//    private String port;
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .build();
    }
}
