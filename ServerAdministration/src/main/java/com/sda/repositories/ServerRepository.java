package com.sda.repositories;

import com.sda.entities.ServerCredentialsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends JpaRepository<ServerCredentialsEntity, Integer> {

}
