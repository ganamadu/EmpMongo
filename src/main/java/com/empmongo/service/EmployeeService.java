package com.empmongo.service;

import com.empmongo.entity.Emp;
import com.empmongo.repo.EmployeeRepo;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EmployeeService {

    private final String KEY = "EMP";

    private final EmployeeRepo employeeRepo;
    private final ReactiveRedisOperations<String, Emp> redisEmpOperations;
    private final ReactiveHashOperations<String, String, Emp> reactiveValueOps;

    public EmployeeService(EmployeeRepo employeeRepo, ReactiveRedisOperations<String, Emp> redisEmpOperations) {
        this.employeeRepo = employeeRepo;
        this.redisEmpOperations = redisEmpOperations;
        this.reactiveValueOps = redisEmpOperations.opsForHash();
    }

    public Flux<Emp> getEmpList() {
        return employeeRepo.findAll();
    }

    public Mono<Emp> getEmpByEmpno(Integer empno) {
        System.out.println("Call the db: "+empno);
        return employeeRepo.findByEmpno(empno);
    }

    public Mono<Emp> findByEmpId(String id) {
        System.out.println("Call the db  findByEmpId method: "+id);
        return employeeRepo.findById(id);
    }

    public Mono<Emp> getEmpById(Integer empno) {
        System.out.println(reactiveValueOps.get(KEY, String.valueOf(empno))+" "+String.valueOf(empno));
        return reactiveValueOps.get(KEY, String.valueOf(empno))
                .switchIfEmpty(
                        getEmpByEmpno(empno)
                                   .flatMap(emp -> reactiveValueOps.put(KEY, String.valueOf(emp.getEmpno()), emp)
                                       .doOnEach(val -> System.out.println(""))
                               .thenReturn(emp))
                               .switchIfEmpty(Mono.error(Exception::new))
                );
    }

    public Mono<Emp> findById(String id) {
        System.out.println(reactiveValueOps.get(KEY, id)+" "+String.valueOf(id));
        return reactiveValueOps.get(KEY, id)
                .switchIfEmpty(
                        findByEmpId(id)
                                .flatMap(emp -> reactiveValueOps.put(KEY, id, emp)
                                        .doOnEach(val -> System.out.println(""))
                                        .thenReturn(emp))
                                .switchIfEmpty(Mono.error(Exception::new))
                );
    }




    /*public Flux<Emp> getEmpByEmpno(Integer empno) {
        return reactiveValueOps.get(KEY,empno)
                // Fetching cached movies.
                .flatMap(key -> reactiveValueOps.get(KEY, key))
                // If cache is empty, fetch the database for movies
                .switchIfEmpty(employeeRepo.findById(empno)
                        // Persisting the fetched movies in the cache.
                        .flatMap(emp ->
                                reactiveValueOps
                                        .opsForValue()
                                        .set("emp:" + emp.getEmpno(), emp)
                        )
                        // Fetching the movies from the updated cache.
                        .thenMany(reactiveValueOps
                                .keys("emp:*")
                                .flatMap(key -> reactiveValueOps.opsForValue().get(key))
                        )
                );

    }*/
}
