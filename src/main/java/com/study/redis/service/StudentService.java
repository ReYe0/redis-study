package com.study.redis.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.study.redis.pojo.dto.LoginFormDTO;
import com.study.redis.pojo.Student;
import com.study.redis.pojo.StudentList;
import com.study.redis.pojo.dto.StudentDTO;
import com.study.redis.util.RegexUtils;
import com.study.redis.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.study.redis.constant.SystemConstants.USER_NICK_NAME_PREFIX;

@Service
@Slf4j
public class StudentService {

    public Result sendCode(String phone, HttpSession session) {
        // 1.校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2.如果不符合，返回错误信息
            return Result.fail("手机号格式错误！");
        }
        // 3.符合，生成验证码
        String code = RandomUtil.randomNumbers(6);

        // 4.保存验证码到 session
        session.setAttribute("code",code);
        // 5.发送验证码
        System.out.println("code:"+code);
        // 返回ok
        return Result.ok();
    }

    public Result login(LoginFormDTO loginForm, HttpSession session) {
        // 1.校验手机号
        String phone = loginForm.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2.如果不符合，返回错误信息
            return Result.fail("手机号格式错误！");
        }
        // 3.校验验证码
        Object cacheCode = session.getAttribute("code");
        String code = loginForm.getCode();
        if(cacheCode == null || !cacheCode.toString().equals(code)){
            //3.不一致，报错
            return Result.fail("验证码错误");
        }
        //一致，根据手机号查询用户
        Student stu = StudentList.findByPhone(phone);
        //5.判断用户是否存在
        if(stu == null){
            //不存在，则创建
            stu =  createUserWithPhone(phone);
        }
        //7.保存用户信息到session中
        session.setAttribute("stu", BeanUtil.copyProperties(stu, StudentDTO.class));

        return Result.ok();
    }

    private Student createUserWithPhone(String phone) {
        // 1.创建用户
        Student stu = new Student();
        stu.setId(666l);
        stu.setPhone(phone);
        stu.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
        // 2.保存用户
        StudentList.add(stu);
        return stu;
    }
}
