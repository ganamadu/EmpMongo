package com.empmongo.entity;

import com.empmongo.enums.Status;
import com.empmongo.enums.StudentType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document
public class Student implements Serializable {

    @Id
    private String id;
    private Integer sno;
    private String sname;
    private Status status;
    private StudentType studentType;

    private boolean primaryStudent;
    private String subject;

    @Version  // ✅ Enables Optimistic Locking
    private Long version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSno() {
        return sno;
    }

    public void setSno(Integer sno) {
        this.sno = sno;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public StudentType getStudentType() {
        return studentType;
    }

    public void setStudentType(StudentType studentType) {
        this.studentType = studentType;
    }

    public boolean isPrimaryStudent() {
        return primaryStudent;
    }

    public void setPrimaryStudent(boolean primaryStudent) {
        this.primaryStudent = primaryStudent;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", sno=" + sno +
                ", sname='" + sname + '\'' +
                ", status=" + status +
                ", studentType=" + studentType +
                ", primaryStudent=" + primaryStudent +
                ", subject='" + subject + '\'' +
                ", version=" + version +
                '}';
    }
}
