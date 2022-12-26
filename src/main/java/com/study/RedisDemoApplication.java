package com.study;

import com.study.redis.pojo.Student;
import com.study.redis.pojo.StudentList;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisDemoApplication {

    public static void main(String[] args) {
        initStu();
        SpringApplication.run(RedisDemoApplication.class, args);
    }

    public static void initStu(){
        StudentList.add(new Student(2l,"15328346433","66666","erye1",""));
        StudentList.add(new Student(3l,"15328346432","77777","erye2",""));
        StudentList.add(new Student(4l,"15328346431","88888","erye3",""));
    }

}
