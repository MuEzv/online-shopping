package com.project.config;

import com.datastax.astra.client.DataAPIClient;
import com.datastax.astra.client.Database;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AstraDBConnection {
    @Value("${ASTRA_DB_TOKEN}") // Spring property
    private String ASTRA_DB_TOKEN;
//    private static final String ASTRA_DB_TOKEN = System.getenv("ASTRA_DB_TOKEN");

    private static final String DATABASE_URL = "https://543d6d3a-525a-4560-92e4-f973cd735aa6-us-east-2.apps.astra.datastax.com";

    private Database database;

    @PostConstruct
    public void initialize(){
        if(ASTRA_DB_TOKEN == null) throw new IllegalStateException("AstraDB token is not set in environment variables.");
        DataAPIClient client = new DataAPIClient(ASTRA_DB_TOKEN);
        this.database = client.getDatabase(DATABASE_URL);
    }
    public AstraDBConnection() {
//        if(ASTRA_DB_TOKEN == null) throw new IllegalStateException("AstraDB token is not set in environment variables.");
//        DataAPIClient client = new DataAPIClient(ASTRA_DB_TOKEN);
//        this.database = client.getDatabase(DATABASE_URL);
    }

    public Database getDatabase() {
        return database;
    }

    // Add connection health check
    public boolean isHealthy() {
        try {
            // Verify namespace availability
            String namespace = database.getNamespaceName();
            // Execute a simple command
            database.listCollectionNames().findFirst();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}