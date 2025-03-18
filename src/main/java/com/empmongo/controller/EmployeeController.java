package com.empmongo.controller;

import com.empmongo.entity.Emp;
import com.empmongo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/emp")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public Flux<Emp> getEmpList() {
        return employeeService.getEmpList();
    }



    @GetMapping("/{empno}")
    public Mono<Emp> getEmp(@PathVariable("empno") Integer empno) {
        return employeeService.getEmpByEmpno(empno);
    }

    @GetMapping("/id/{id}")
    public Mono<Emp> getEmp(@PathVariable("id") String id) {
        return employeeService.findByEmpId(id);
    }

    @GetMapping("/radis/{empno}")
    public Mono<Emp> getEmpByEno(@PathVariable("empno") Integer empno) {
        System.out.println("Call the radis endpoint: "+empno);
        return employeeService.getEmpById(empno);
    }

    @GetMapping("/radis/id/{id}")
    public Mono<Emp> getEmpByEno(@PathVariable("id") String id) {
        System.out.println("Call the radis endpoint: "+id);
        return employeeService.findById(id);
    }

}
