package com.study.redis.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String phone;
    private String password;
    private String nickName;
    private String icon = "";

    public Student next;

    public Student(long id, String phone, String password, String nickName, String icon) {
        this.id = id;
        this.phone = phone;
        this.password = password;
        this.nickName = nickName;
        this.icon = icon;
    }
}
