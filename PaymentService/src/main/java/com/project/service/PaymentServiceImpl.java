package com.project.service;

import com.datastax.astra.client.Database;
import com.project.config.AstraDBConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    private  final Database database;

    @Autowired
    public PaymentServiceImpl(AstraDBConnection connection){
        this.database = connection.getDatabase();
    }

    public void listCollections() {
        database.listCollectionNames().forEach(System.out::println);
    }
}
