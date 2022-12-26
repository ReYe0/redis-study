package com.study.redis.util;

import com.study.redis.pojo.dto.StudentDTO;

public class StudentHolder {
    private static final ThreadLocal<StudentDTO> tl = new ThreadLocal<>();

    public static void saveStudent(StudentDTO user){
        tl.set(user);
    }

    public static StudentDTO getStudent(){
        return tl.get();
    }

    public static void removeStudent(){
        tl.remove();
    }
}