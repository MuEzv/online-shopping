package com.project;

import com.datastax.astra.client.Database;
import com.project.config.AstraDBConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@SpringBootApplication(exclude = {
        CassandraDataAutoConfiguration.class,
        org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration.class
})
@EnableFeignClients
public class OrderServiceApplication {

    public static void main(String[] args) {

//        SpringApplication.run(OrderServiceApplication.class, args);
        ApplicationContext context = SpringApplication.run(OrderServiceApplication.class, args);
        AstraDBConnection connection = context.getBean(AstraDBConnection.class);
        System.out.println("AstraDBConnection initialized: " + connection.getDatabase());
    }
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
