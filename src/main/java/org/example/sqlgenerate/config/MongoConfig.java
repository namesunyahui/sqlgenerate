package org.example.sqlgenerate.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {


    @Autowired
    private MongoClient mongoClient;

    @Bean
    public MongoDatabase getMongoDatabase() {
        return mongoClient.getDatabase("admin");
    }
}
