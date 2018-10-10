package com.oreilly.reactiveofficers.dao;

import com.oreilly.reactiveofficers.entities.Officer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface OfficerRepository extends ReactiveMongoRepository<Officer, String> {
}
