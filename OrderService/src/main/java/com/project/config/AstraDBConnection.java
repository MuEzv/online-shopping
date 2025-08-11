package com.project.config;

import com.datastax.astra.client.DataAPIClient;
import com.datastax.astra.client.Database;
import org.springframework.stereotype.Component;

@Component
public class AstraDBConnection {
    private static final String ASTRA_DB_TOKEN = System.getenv("ASTRA_DB_TOKEN");

    private static final String DATABASE_URL = "https://543d6d3a-525a-4560-92e4-f973cd735aa6-us-east-2.apps.astra.datastax.com";

    private final Database database;

    public AstraDBConnection() {
        if(ASTRA_DB_TOKEN == null) throw new IllegalStateException("AstraDB token is not set in environment variables.");
        if (ASTRA_DB_TOKEN == null || ASTRA_DB_TOKEN.isBlank()) {
            throw new IllegalStateException("Missing ASTRA_DB_TOKEN");
        }
        if (!DATABASE_URL.matches("^https://\\w+-\\w+-\\d+.apps.astra.datastax.com$")) {
            throw new IllegalArgumentException("Invalid DB URL format");
        }
        DataAPIClient client = new DataAPIClient(ASTRA_DB_TOKEN);
        this.database = client.getDatabase(DATABASE_URL);
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