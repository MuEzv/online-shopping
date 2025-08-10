package com.project;

import com.project.config.AstraDBConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(exclude = {CassandraDataAutoConfiguration.class})
public class PaymentServiceApplication {

    public static void main(String[] args) {

//        SpringApplication.run(PaymentServiceApplication.class, args);

        //DB Connection TEST
        ApplicationContext context = SpringApplication.run(PaymentServiceApplication.class, args);
        AstraDBConnection connection = context.getBean(AstraDBConnection.class);
        System.out.println("AstraDBConnection initialized: " + connection.getDatabase());
    }

}
