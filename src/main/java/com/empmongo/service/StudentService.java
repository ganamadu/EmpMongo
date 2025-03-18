package com.empmongo.service;

import com.empmongo.entity.Student;
import com.empmongo.enums.Status;
import com.empmongo.enums.StudentType;
import com.empmongo.exception.EntityNotFoundException;
import com.empmongo.repo.StudentRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
public class StudentService {
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final String KEY = "STD";

    private final StudentRepo studentRepo;
    private final ReactiveRedisOperations<String, Student> redisStudentOperations;
    private final ReactiveHashOperations<String, String, Student> reactiveValueOps;
    private final ReactiveHashOperations<String, String, String> reactiveJsonValueOps;
    @Autowired

    public StudentService(StudentRepo studentRepo, ReactiveRedisOperations<String, Student> redisStudentOperations) {
        this.studentRepo = studentRepo;
        this.redisStudentOperations = redisStudentOperations;
        this.reactiveValueOps = redisStudentOperations.opsForHash();
        this.reactiveJsonValueOps = redisStudentOperations.opsForHash();
    }

    public Mono<Student> save(Student student) {
        logger.info("Calling the save method {}", student);
        return Mono.just(student)
                .flatMap(studentRepo::save)
                .flatMap(stud -> reactiveValueOps.put(KEY, String.valueOf(stud.getSno()), stud)
                        .doOnEach(val -> logger.info("Setting accountToken in cache + {}", stud.getSno()))
                        .thenReturn(stud))
                .onErrorResume(DuplicateKeyException.class,
                        cause -> getStudentBySno(student.getSno()));
    }

    public Mono<Student> saveNew(Student student) {
        String redisKey = "students:subject:" + student.getSubject();
        return reactiveValueOps.remove(KEY, redisKey)
                .then(Mono.just(student))
                .flatMap(studentRepo::save)
                .flatMap(stud -> reactiveValueOps.put(KEY, String.valueOf(stud.getSno()), stud)
                        .doOnEach(val -> logger.info("Setting accountToken in cache + {}", stud.getSno()))
                        .thenReturn(stud))
                .onErrorResume(DuplicateKeyException.class,
                        cause -> getStudentBySno(student.getSno()));
    }

    public Mono<Student> getStudentId(String id) {
        logger.info("Calling the getStudentId method id {}", id);
        return studentRepo.findById(id);
    }

  /*  public Mono<Student> getStudentBySno(Integer sno) {
        logger.info("Call the getStudentBySno DB method: " + sno);
        return reactiveValueOps.get(KEY, String.valueOf(sno))
                .filter(stud -> !stud.isPrimaryStudent() && !Status.INACTIVE.equals(stud.getStatus()))
                .doOnNext(val -> logger.info("Get the student no from redis cache {} " + val.getId()))
                .switchIfEmpty(
                        studentRepo.findBySno(sno)
                                .filter(stud -> !stud.isPrimaryStudent() && !Status.INACTIVE.equals(stud.getStatus()))
                                .flatMap(stud -> reactiveValueOps.put(KEY, String.valueOf(stud.getSno()), stud)
                                        .doOnNext(val -> logger.info("Setting the student no in cache {} " + val))
                                        .thenReturn(stud)
                                        .switchIfEmpty(Mono.error(Exception::new))));
    }*/

    public Mono<Student> getStudentBySno(Integer sno) {
        logger.info("Fetching student with sno: {} ",sno);
        return reactiveValueOps.get(KEY, String.valueOf(sno))
                .doOnNext(stud -> logger.info("Fetched from Redis: " + (stud != null ? stud.getId() : "null")))
                .filter(stud -> {
                    boolean isNotPrimaryInActive = !stud.isPrimaryStudent() && !Status.INACTIVE.equals(stud.getStatus());
                    boolean isPrimary = stud.isPrimaryStudent();
                    logger.info("Checking Redis student conditions: {} ", isNotPrimaryInActive);
                    return isNotPrimaryInActive | isPrimary;
                })
                .switchIfEmpty(
                        studentRepo.findBySno(sno)
                                .doOnNext(stud -> logger.info("Fetched from MongoDB: " + (stud != null ? stud.getId() : "null")))
                                .filter(stud -> {
                                    boolean isNotPrimaryInActive = !stud.isPrimaryStudent() && !Status.INACTIVE.equals(stud.getStatus());
                                    boolean isPrimary = stud.isPrimaryStudent();
                                    logger.info("Checking MongoDB student conditions {} ", isNotPrimaryInActive);
                                    return isNotPrimaryInActive | isPrimary;
                                })
                                .flatMap(stud -> reactiveValueOps.put(KEY, String.valueOf(stud.getSno()), stud)
                                        .doOnSuccess(success -> logger.info("Student cached in Redis {} ", stud.getId()))
                                        .thenReturn(stud)
                                )
                )
                .switchIfEmpty(Mono.error(EntityNotFoundException::new));
    }

    /*public Flux<Student> findByStudentWithSubjectOld(String subject) {
        String redisKey = "students:subject:" + subject;  // ✅ Improved Key Format
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Fetching students with subject: {}", subject);

        return reactiveValueOps.get(KEY,redisKey)
                .filter(Objects::nonNull)
                .cast(List.class)  // ✅ Cast Redis data back into a List
                .flatMapMany(Flux::fromIterable)  // ✅ Convert List<Student> back to Flux
                .doOnNext(stud -> logger.info("Fetched from Redis: {}", stud))
                .switchIfEmpty(
                        studentRepo.findAllCriteria(subject)
                                .collectList()  // ✅ Collect MongoDB results into a List
                                .doOnNext(students -> {
                                    if (!students.isEmpty()) {
                                        logger.info("Fetched from MongoDB: {} students", students.size());
                                        // ✅ Store the list of students in Redis
                                        String jsonString;
                                        try {
                                            jsonString = objectMapper.writeValueAsString(students);

                                            List<Student> studList = objectMapper.readValue(students, new TypeReference<List<Student>>(){});
                                        } catch (JsonProcessingException e) {
                                            return Flux.empty();
                                        }
                                        reactiveValueOps.put(KEY, redisKey, jsonString)
                                                .doOnSuccess(success -> logger.info("Cached {} students in Redis", students.size()))
                                                .subscribe();  // ✅ Ensure the put() operation runs
                                    }
                                })
                                .flatMapMany(Flux::fromIterable)  // ✅ Convert List<Student> back to Flux
                )
                .switchIfEmpty(Mono.error(() -> {
                    logger.error("No students found for subject: {}", subject);
                    return new EntityNotFoundException();
                }));
    }*/

    public Flux<Student> findByStudentWithSubject(String subject) {
        String transactionId = MDC.get("X-Transaction-ID");
        String redisKey = "students:subject:" + subject;
        ObjectMapper objectMapper = new ObjectMapper();

        return reactiveValueOps.get(KEY, redisKey)
                .cast(String.class)  // ✅ Ensure Redis data is treated as a JSON string
                .flatMapMany(jsonData -> {
                    try {
                        List<Student> studList = objectMapper.readValue(jsonData, new TypeReference<List<Student>>() {});  // ✅ Correct Instantiation
                        return Flux.fromIterable(studList);  // ✅ Return as Flux
                    } catch (JsonProcessingException e) {
                        logger.error("Failed to deserialize data from Redis", e);
                        return Flux.empty();
                    }
                })
                .switchIfEmpty(
                        studentRepo.findAllCriteria(subject)
                                .collectList()
                                .flatMapMany(students -> {
                                    if (students.isEmpty()) {
                                        return Flux.error(new EntityNotFoundException());
                                    }

                                    try {
                                        String studentListJson = objectMapper.writeValueAsString(students);  // ✅ Serialize as JSON
                                        return reactiveJsonValueOps.put(KEY, redisKey, studentListJson)
                                                .thenMany(Flux.fromIterable(students));
                                    } catch (JsonProcessingException e) {
                                        return Flux.error(new RuntimeException("Failed to serialize student list", e));
                                    }
                                })
                )
                .doOnNext(stud -> logger.info("Returned Student: {}", stud));
    }


    public Mono<Student> getStudById(String id) {
        return reactiveValueOps.get(KEY, id);
    }

    public Mono<Student> updateStatusBySno(Integer sno, Student student) {
        logger.info("Calling the updateStatusBySno method sno {} and student {}", sno, student);
        return Mono.just(student)
                .flatMap(stud -> studentRepo.updateStatusBySno(sno, stud.getStatus()))
                .switchIfEmpty(Mono.error(Exception :: new))
                .flatMap(redisStud -> reactiveValueOps.put(KEY, String.valueOf(redisStud.getSno()), redisStud)
                        .thenReturn(redisStud));

    }

    public Mono<String> deleteAllSnoByStudentType(Student student, StudentType studentType) {
        logger.info("Calling the deleteAllSnoByStudentType method studentType {} and student {}", studentType, student);
        Mono<UpdateResult> deleteStudentDeleteResultSet =
                studentRepo.deleteStudentBySnoAndStudentProductType(student.getSno(), studentType);

        return deleteStudentDeleteResultSet.flatMap(updateResult -> {
            if (updateResult.getModifiedCount() != 0) {
                logger.info("The active Students was successfully deleted");
                return Mono.just("The active Students was successfully deleted");
            } else {
                logger.info("The active Students was not found");
                return Mono.just("The active Students was not found");

            }
        });


       /*return studentRepo deleteStudentDeleteResultSet.flatMap(areSnoDeleted ->{
           if(areSnoDeleted.getModifiedCount() != 0) {
               return Mono.just("The active Students was successfully deleted");
           } else {
               return Mono.just("The active Students was not found");
           }
        });*/
    }

    /*public Mono<Student> updateRedisStatusBySno(Student student) {
        logger.info("Calling the updateRedisStatusBySno with student {}", student);

        return Mono.justOrEmpty(student) // Ensure `student` is not null
                .flatMap(redisStud ->
                        reactiveValueOps.put(KEY, String.valueOf(redisStud.getSno()), redisStud)
                                .flatMap(success -> success ? Mono.just(redisStud) : Mono.error(new Exception("Failed to update Redis")))
                )
                .switchIfEmpty(Mono.error(new Exception("Student is null")));
    }*/

    /*public Mono<Student> updateRedisStatusBySno(Student student) {
        logger.info("Calling updateRedisStatusBySno with student: {}", student);
        return Mono.just(student)
                .flatMap(redisStud -> reactiveValueOps.put(KEY, String.valueOf(redisStud.getSno()), redisStud)
                        .thenReturn(redisStud));

        *//*return Mono.justOrEmpty(student)
                .flatMap(redisStud -> {
                    String key = String.valueOf(redisStud.getSno());
                    return reactiveValueOps.put(KEY, key, redisStud)
                            .doOnSuccess(success -> logger.info("Redis update status for key {}: {}", key, success))
                            .flatMap(success -> success
                                    ? Mono.just(redisStud)
                                    : Mono.error(new Exception("Failed to update Redis for key: " + key))
                            );
                })
                .switchIfEmpty(Mono.error(new Exception("Student is null")));*//*
    }*/

    public Mono<Student> updateRedisStatusBySno(Student student) {
        logger.info("Updating Redis for student: {}", student);

        return Mono.justOrEmpty(student)
                .flatMap(redisStud -> {
                    String key = String.valueOf(redisStud.getSno());
                    return reactiveValueOps.put(KEY, key, redisStud)
                            .doOnSuccess(success -> logger.info("Redis update status for key {}: {}", key, success))
                            .flatMap(success -> success
                                    ? Mono.just(redisStud)
                                    : Mono.error(new Exception("Failed to update Redis for key: " + key))
                            );
                })
                .switchIfEmpty(Mono.error(new Exception("Student is null")));
    }

    public Mono<String > clearCache() {

        logger.info("Call the clearCache method.");

        return reactiveValueOps.keys(KEY)
                .flatMap(k -> reactiveValueOps.remove(KEY, k)
                        .doOnSuccess(success -> logger.info("All caches evicted! {}", success)))
                .then(Mono.just("All caches evicted!"));

    }



}
