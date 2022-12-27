package com.study.redis.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Shop implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String address;

    public Shop next;


    public Shop(long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;

    }
}
