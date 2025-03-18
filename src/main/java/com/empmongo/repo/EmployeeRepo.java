package com.empmongo.repo;

import com.empmongo.entity.Emp;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface EmployeeRepo extends ReactiveMongoRepository<Emp, Integer> {

    Mono<Emp> findByEmpno(Integer empno);

    Mono<Emp> findById(String id);

}
