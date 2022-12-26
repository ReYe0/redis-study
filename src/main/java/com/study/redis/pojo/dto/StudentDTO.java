package com.study.redis.pojo.dto;

import lombok.Data;

@Data
public class StudentDTO {
    private Long id;
    private String nickName;
    private String icon = "";
}
