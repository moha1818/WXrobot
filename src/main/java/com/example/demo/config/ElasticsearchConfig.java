package com.example.demo.config;

import com.example.demo.service.impl.ElasticSearchServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: moha
 * @create: 2019-12-13 16:03
 */
//@Configuration
public class ElasticsearchConfig {
    @Bean(destroyMethod = "close")
    public ElasticSearchServiceImpl elasticSearchService() throws Exception {
        ElasticSearchServiceImpl elasticSearchService =
                new ElasticSearchServiceImpl("127.0.0.1",9200,"http");
        return elasticSearchService;
    }
}
