package com.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public void sendMessage(String topic, String key, String message){
        kafkaTemplate.send(topic, key, message)
                .addCallback(
                        result -> {
                            assert result != null;
                            System.out.println("Message sent successfully: " + result.getProducerRecord());
                        },
                        ex -> System.err.println("Failed to send message: " + ex.getMessage())
                );
    }
    @Transactional
    public void sendDefaultMessage(String message){
        kafkaTemplate.sendDefault(message)
                .addCallback(
                        result -> System.out.println("Default message sent successfully: " + result.getProducerRecord()),
                        ex -> System.err.println("Failed to send default message: " + ex.getMessage())
                );
    }
}
