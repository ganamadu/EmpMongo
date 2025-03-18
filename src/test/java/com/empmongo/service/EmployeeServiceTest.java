package com.empmongo.service;

import com.empmongo.entity.Student;
import com.empmongo.repo.StudentRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class EmployeeServiceTest {

    private final String KEY = "STD";

    @Mock
    private StudentRepo studentRepo;

    @Mock
    private ReactiveRedisOperations<String, Student> redisOperations;

    @Mock
    private ReactiveHashOperations<String, String, Student> reactiveValueOps;

    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        //when(redisOperations.opsForHash()).thenReturn(reactiveValueOps);
        when(redisOperations.opsForHash()).thenAnswer(invocation -> reactiveValueOps);
    }


    @Test
    void getEmpList() {
    }

    @Test
    void getEmpByEmpno() {
    }

    @Test
    void findByEmpId() {
    }

    @Test
    void getEmpById() {
    }

    @Test
    void findById() {
    }
}