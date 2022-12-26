package com.study.redis.controller;

import com.study.redis.pojo.dto.LoginFormDTO;
import com.study.redis.service.StudentService;
import com.study.redis.util.Result;
import com.study.redis.util.StudentHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/student")
public class StudentController {

    @Resource
    private StudentService studentService;
    /**
     * 发送手机验证码
     */
    @PostMapping("code")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session) {
        // 发送短信验证码并保存验证码
        return studentService.sendCode(phone, session);
    }

    /**
     * 登录功能
     * @param loginForm 登录参数，包含手机号、验证码；或者手机号、密码
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm, HttpSession session){
        return studentService.login(loginForm,session);
    }

    /**
     * 登出功能
     * @return 无
     */
    @PostMapping("/logout")
    public Result logout(){
        return Result.fail("功能未完成");
    }

    /**
     * 获取当前登录的用户并返回
     * @return com.study.redis.util.Result
     * @author xuy
     * @date 2022/12/26 15:49
     */
    @GetMapping("/me")
    public Result me(){
        return Result.ok(StudentHolder.getStudent());
    }
}
