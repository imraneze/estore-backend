package com.estore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {
        "com.estore.customer.repository",
        "com.estore.catalog.repository",
        "com.estore.inventory.repository",
        "com.estore.shopping.repository",
        "com.estore.billing.repository"
})
@EnableMongoRepositories(basePackages = {
        "com.estore.review.repository"
})
public class DatabaseConfig {
}