package com.profitsoft.notification.microservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import java.time.Duration;

/**
 * The config class of an application
 */
@Configuration
public class ElasticsearchClientConfig extends ElasticsearchConfiguration {

    @Value("${elasticsearch.address}")
    String esAddress;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(esAddress)
                .withSocketTimeout(Duration.ofMinutes(2L))
                .build();
    }
}