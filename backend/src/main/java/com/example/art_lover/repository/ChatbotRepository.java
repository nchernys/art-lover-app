package com.example.art_lover.repository;

import java.util.Optional;
import java.util.List;

// to connect the spring boot app to mongo 
import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.art_lover.model.ChatbotMessageModel;

public interface ChatbotRepository extends MongoRepository<ChatbotMessageModel, String> {

    List<ChatbotMessageModel> findAllByUserId(String userId);

    void deleteAllByUserId(String userId);
}
