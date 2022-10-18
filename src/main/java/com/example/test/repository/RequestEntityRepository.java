package com.example.test.repository;

import com.example.test.entity.RequestEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * Interface for generic CRUD operations on a repository for RequestEntity
 */
public interface RequestEntityRepository extends ReactiveCrudRepository<RequestEntity, Integer>{
}
