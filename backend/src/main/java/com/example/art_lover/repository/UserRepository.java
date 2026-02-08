package com.example.art_lover.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.art_lover.model.UserModel;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserModel, String> {
    Optional<UserModel> findByEmail(String email);

    Optional<UserModel> deleteByEmail(String email);
}