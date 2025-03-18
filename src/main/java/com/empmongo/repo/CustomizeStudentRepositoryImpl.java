package com.empmongo.repo;

import com.empmongo.entity.Student;
import com.empmongo.enums.Status;
import com.empmongo.enums.StudentType;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

public class CustomizeStudentRepositoryImpl implements  CustomizeStudentRepository {
    private static final Logger logger = LoggerFactory.getLogger(CustomizeStudentRepositoryImpl.class);

    @Autowired
    ReactiveMongoOperations mongoOperations;
    @Override
    public Mono<Student> updateStatusBySno(Integer sno, Status status) {
        logger.info("Calling the updateStatusBySno method sno {} and status {}", sno, status);
        return mongoOperations
                .findAndModify(query(where("sno").is(sno)),
                        new Update()
                                .set("status", status)
                                        .set("source","API"),
                        FindAndModifyOptions.options().returnNew(true),
                        Student.class);
    }

    @Override
    public Mono<UpdateResult> deleteAllBySnoAndStudentType(Integer sno, StudentType studentType) {
        logger.info("Calling the deleteAllBySnoAndStudentType method sno {} and studentType {}", sno, studentType);
        return mongoOperations
                .updateMulti(query(where("sno").is(sno)
                        .and("studentType").is(studentType)
                        .and("status").is("Active")),
                        update("status", "InActive"), Student.class);
    }

    @Override
    public Mono<UpdateResult> deleteStudentBySnoAndStudentProductType(Integer sno, StudentType studentType) {
        logger.info("Calling the deleteStudentBySnoAndStudentProductType method sno {} and studentType {}", sno, studentType);
        Criteria criteria = where("studentType").is(studentType)
                .and("status").is(Status.ACTIVE);

        if(sno != null) {
            criteria.and("sno").is(sno)
                    .and("primaryStudent").is(false);
        }

        logger.info("Criteria Query {}", criteria);

        return mongoOperations.updateMulti(query(criteria),
                update("status", Status.INACTIVE), Student.class);
    }

    public Flux<Student> findAllCriteria(String subject) {
        var andCriteria = new ArrayList<Criteria>();
        if(subject != null) {
            andCriteria.add(where("subject").is(subject));
        }
        Criteria criterion = new Criteria().andOperator(andCriteria);
        return mongoOperations.find(new Query().addCriteria(criterion), Student.class);
    }


}
