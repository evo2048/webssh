package com.sda.repositories;

import com.sda.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    public UserEntity findByUsername(String username);
    public UserEntity findByEmail(String email);
}
