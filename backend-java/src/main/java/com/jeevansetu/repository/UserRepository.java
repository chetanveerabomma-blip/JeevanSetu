package com.jeevansetu.repository;

import com.jeevansetu.model.Role;
import com.jeevansetu.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    List<User> findByRole(Role role);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
