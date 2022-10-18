package com.example.test.repository;

import com.example.test.entity.RequestEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RequestEntityRepository extends ReactiveCrudRepository<RequestEntity, String>{
}
