package com.empmongo.controller;

import com.empmongo.entity.Student;
import com.empmongo.enums.StudentType;
import com.empmongo.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/stud", produces = MediaType.APPLICATION_JSON_VALUE)
public class StudentController {
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    @PostMapping
    public Mono<Student> save(@RequestBody Student student) {
        logger.info("Creating the student {}", student);
        return studentService.save(student);
    }

    @GetMapping("/id/{id}")
    public Mono<Student> getStudById(@PathVariable("id") String id) {
        logger.info("Calling the getStudById method {}", id);
        return studentService.getStudentId(id);
    }

    @GetMapping("/radis/{id}")
    public Mono<Student> getStudRadisById(@PathVariable("id") String id) {
        return studentService.getStudById(id);
    }
    @GetMapping("/sno/{sno}")
    public Mono<Student> getStudentBySno(@PathVariable("sno") Integer sno) {
        logger.info("Get the student by sno {}", sno);
        return studentService.getStudentBySno(sno);
    }

    @GetMapping("/sub/{subject}")
    public Flux<Student> getStudentBySubject(@PathVariable("subject") String subject) {
        logger.info("Get the student by subject {}", subject);
        return studentService.findByStudentWithSubject(subject);
    }

    @PutMapping(value = "/status", params = {"sno"})
    public Mono<Student> updateStatusBySno(
            @RequestParam("sno") Integer sno,
            @RequestBody Student student) {
        logger.info("Calling the updateStatusBySno method sno {} and student {}", sno, student);
        return studentService.updateStatusBySno(sno, student);
    }

    @DeleteMapping
    public Mono<String> deleteByIdentifiersAndProductType(
            @RequestParam("productType")StudentType studentType, @RequestBody Student student) {
        logger.info("Calling the deleteByIdentifiersAndProductType method studentType {} and student {}", studentType, student);
        return studentService.deleteAllSnoByStudentType(student, studentType);
    }

    @DeleteMapping("/clear-cache")
    public Mono<String> clearCache() {
        return studentService.clearCache();
    }

}
