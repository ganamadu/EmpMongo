package com.empmongo.repo;

import com.empmongo.entity.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface StudentRepo extends ReactiveMongoRepository<Student, String>, CustomizeStudentRepository {
    static final Logger logger = LoggerFactory.getLogger(StudentRepo.class);

    Mono<Student> findById(String id);

    Mono<Student> findBySno(Integer sno);

    Mono<Student> findBySnoAndStatus(Integer sno, String status);
}
