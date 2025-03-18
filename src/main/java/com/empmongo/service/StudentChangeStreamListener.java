package com.empmongo.service;

import com.empmongo.entity.Student;
import com.empmongo.enums.Status;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class StudentChangeStreamListener {
    private static final Logger logger = LoggerFactory.getLogger(StudentChangeStreamListener.class);

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    private StudentService studentService;

    public StudentChangeStreamListener(ReactiveMongoTemplate reactiveMongoTemplate, StudentService studentService) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.studentService = studentService;
    }


    @PostConstruct
    public void init() {
        watchChanges();
    }


    public void watchChanges() {
        Flux<ChangeStreamEvent<Student>> changeStream = reactiveMongoTemplate.changeStream("student", ChangeStreamOptions.builder().build(), Student.class);

        changeStream.subscribe(changeEvent -> {
            String  operationType = changeEvent.getOperationType().getValue();
            logger.info("Change Deteched: "+operationType);
            switch (operationType) {
                case "insert":
                    logger.info("Inserted: {}", changeEvent.getBody());
                    break;
                case "update":
                    if("API".equals(changeEvent.getBody().getSubject())) {
                        logger.info("API update detected: {} ", changeEvent.getBody());
                    } else {
                        Student student = changeEvent.getBody();
                        student.setStatus(Status.INACTIVE);
                        student.setPrimaryStudent(false);
                        studentService.updateRedisStatusBySno(student)
                                .doOnSuccess(updatedStudent -> System.out.println("Student updated in Redis: " + updatedStudent))
                                .doOnError(error -> System.err.println("Failed to update student in Redis: " + error.getMessage()))
                                .subscribe();
                        logger.info("Direct DB update detected: {}", changeEvent.getBody());
                    }
                    break;
                case "delete":
                    logger.info("Deleted: {}",changeEvent.getBody());
            }
        }, error -> {
            logger.info("Error in change stream: {}",error);
        });
    }

    /*@PostConstruct
    public void watchChanges() {
        ChangeStreamRequest<Document> request = ChangeStreamRequest.builder()
                .collection("student")
                .filter(new Document())
                .publishTo(new MessageListener<ChangeStreamDocument<Document>, Document>() {

                    @Override
                    public void onMessage(Message<ChangeStreamDocument<Document>, Document> event) {
                        ChangeStreamDocument<Document> rawEvent = event.getRaw();
                        handleEvent(rawEvent);
                    }
                })
                .build();

        Subscription subscription = reactiveMongoTemplate.changeStream(request, Document.class);
    }


    private void handleEvent(ChangeStreamDocument<Document> event) {
        OperationType operationType = event.getOperationType();
        System.out.println("Change detected: " + operationType);

        switch (operationType) {
            case INSERT:
                System.out.println("New Student Added: " + event.getFullDocument().toJson());
                break;
            case UPDATE:
                System.out.println("Student Updated: " + event.getUpdateDescription());
                break;
            case DELETE:
                System.out.println("Student Deleted: " + event.getDocumentKey().toJson());
                break;
            default:
                System.out.println("Other Operation: " + operationType);
        }
    }*/


}
