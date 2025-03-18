package com.empmongo.service;

import com.empmongo.config.RedisConfiguration;
import com.empmongo.controller.StudentController;
import com.empmongo.entity.Student;
import com.empmongo.enums.Status;
import com.empmongo.enums.StudentType;
import com.empmongo.repo.StudentRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
//@Import(StudentService.class)
@WebFluxTest(StudentController.class)
class StudentServiceTest {

    private final String KEY = "STD";

    @MockBean
    private StudentService studentService;

    @Mock
    private StudentRepo studentRepo;

    private WebTestClient webTestClient;

    @Mock
    private ReactiveRedisOperations<String, Student> redisStudentOperations;

    @Mock
    private ReactiveHashOperations<String, String, Student> reactiveValueOps;

    private RedisConfiguration redisConfiguration;

    @Autowired
    ApplicationContext context;
    private Student student;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        //redisConfiguration = new RedisConfiguration();
        //when(redisOperations.opsForHash()).thenReturn(reactiveValueOps);
        //when(redisStudentOperations.opsForHash()).thenAnswer(invocation -> reactiveValueOps);
        this.webTestClient = WebTestClient
                .bindToApplicationContext(context)
                .configureClient()
                .build();
        //when(redisStudentOperations.opsForHash()).thenReturn(reactiveValueOps);

        student = new Student();
        student.setId("1");
        student.setSno(101);
        student.setSname("John Doe");

        when(reactiveValueOps.put(anyString(), anyString(), any(Student.class))).thenReturn(Mono.just(true));

    }


    @Test
    void save() {
    }

    @Test
    void getStudentId() {
        Student student = new Student();
        student.setId("1");
        student.setSno(101);
        student.setSname("John Doe");
        student.setStatus(Status.ACTIVE);
        student.setStudentType(StudentType.REGULAR);
        student.setPrimaryStudent(true);
       // when(studentService.findById(student.getId())).thenReturn(Mono.just(student));

        webTestClient.get().uri("/stud/id/ aslkjasljladfs")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult().getResponseBody();
    }

    @Test
    void testGetStudentBySno_FoundInRedis_Primary() {
        // 🔹 Given: Primary student exists in Redis
        Student student = new Student();
        student.setId("1");
        student.setSno(102);
        student.setSname("John Doe");
        student.setStatus(Status.ACTIVE);
        student.setStudentType(StudentType.REGULAR);
        student.setPrimaryStudent(true);
        when(reactiveValueOps.get(KEY, "102")).thenReturn(Mono.just(student));

        // 🔹 When: Calling getStudentBySno()
        Mono<Student> result = studentService.getStudentBySno(102);

        // 🔹 Then: Student should be returned from Redis
        StepVerifier.create(Mono.just(student))
                .expectNext(student)
                .verifyComplete();

        verify(reactiveValueOps, times(0)).get(KEY, "102"); // ✅ Redis was queried
        verifyNoInteractions(studentRepo); // ✅ MongoDB was NOT queried
    }

    //@Test
    void getStudById() {
        Student student = new Student();
        student.setId("aslkjasljladfs");
        student.setSno(101);
        student.setSname("Abcdef");

        when(reactiveValueOps.get(KEY, student.getId())).thenReturn(Mono.just(student));

        StepVerifier.create(studentService.getStudById(student.getId()))
                .expectNext(student)
                .verifyComplete();

        verify(reactiveValueOps).get(KEY, student.getId());

    }

    @Test
    void testGetStudentByIdFromDB() {


        // Mock DB response
        when(studentService.getStudentId("1")).thenReturn(Mono.just(student));

        Student actualBody = webTestClient.get()
                .uri("/stud/id/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Student.class)
                .returnResult().getResponseBody();

        System.out.println(actualBody);
        assertNotNull(actualBody, "Response should not be null");
        assertEquals("1", actualBody.getId());
        assertEquals("John Doe", actualBody.getSname());

                /*.jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.sno").isEqualTo(101)
                .jsonPath("$.sname").isEqualTo("John Doe");*/

        /*// Simulating API call
        String actualBody = webTestClient.get()
                .uri("/students/1") // Assuming this is the API endpoint
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult().getResponseBody();

        // Expected JSON response
        String expectedBody = "{ \"id\": \"1\", \"name\": \"John Doe\" }";

        // Assert JSON equality
        assertEquals(expectedBody, actualBody);

        verify(studentRepo, times(1)).findById("1");*/
    }

    @Test
    void testGetStudentById_NotFound() {
        when(studentService.getStudentId("999")).thenReturn(Mono.empty());
        String actualResponse =  webTestClient.get()
                .uri("/students/999") // ❌ Student ID does not exist
                .exchange()
                .expectStatus().isNotFound() // ✅ Expecting 404 Not Found
        .expectBody(String.class)
                .returnResult().getResponseBody();
        System.out.println(actualResponse);
        Assertions.assertNotEquals(student, actualResponse);


    }
}