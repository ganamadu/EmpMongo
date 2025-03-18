package com.empmongo.repo;

import com.empmongo.entity.Student;
import com.empmongo.enums.Status;
import com.empmongo.enums.StudentType;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomizeStudentRepository {
    static final Logger logger = LoggerFactory.getLogger(CustomizeStudentRepository.class);

    public Mono<Student> updateStatusBySno(Integer sno, Status status);

    public Mono<UpdateResult> deleteAllBySnoAndStudentType(Integer sno, StudentType studentType);

    public Mono<UpdateResult> deleteStudentBySnoAndStudentProductType(Integer sno, StudentType studentType);

    public Flux<Student> findAllCriteria(String subject);


}
