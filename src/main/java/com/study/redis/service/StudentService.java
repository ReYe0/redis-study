package com.study.redis.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.study.redis.pojo.dto.LoginFormDTO;
import com.study.redis.pojo.Student;
import com.study.redis.pojo.StudentList;
import com.study.redis.pojo.dto.StudentDTO;
import com.study.redis.util.RegexUtils;
import com.study.redis.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.study.redis.constant.RedisConstants.*;
import static com.study.redis.constant.SystemConstants.USER_NICK_NAME_PREFIX;

@Service
@Slf4j
public class StudentService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public Result sendCode(String phone, HttpSession session) {
        // 1.校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2.如果不符合，返回错误信息
            return Result.fail("手机号格式错误！");
        }
        // 3.符合，生成验证码
        String code = RandomUtil.randomNumbers(6);

        // 4.保存验证码到 session,之后保存到redis
//        session.setAttribute("code",code);
        stringRedisTemplate.opsForValue().set("test",code);
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone,code,LOGIN_CODE_TTL,TimeUnit.MINUTES);// set key value ex 120
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
        // 3.校验验证码,TODO 之后从redis中获取
//        Object cacheCode = session.getAttribute("code");
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
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
        //7.保存用户信息到session中，TODO 之后用redis代替
//        session.setAttribute("stu", BeanUtil.copyProperties(stu, StudentDTO.class));
        // TODO 7.1.随机生成token，作为登录令牌
        String token = UUID.randomUUID().toString(true);
        //TODO 7.2.将User对象转为HashMap存储
        StudentDTO stuDTO = BeanUtil.copyProperties(stu, StudentDTO.class);
        Map<String, Object> stuMap = BeanUtil.beanToMap(stuDTO, new HashMap<>(),
                CopyOptions.create() // 数据拷贝是的选项
                        .setIgnoreNullValue(true) //忽略空的值
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));//修改字段，将long的id转为String
        //TODO 7.3.存储
        String tokenKey = LOGIN_STU_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, stuMap);
        // TODO 7.4.设置token有效期
        stringRedisTemplate.expire(tokenKey, LOGIN_STU_TTL, TimeUnit.MINUTES);
//        return Result.ok();
        return Result.ok(token);//TODO 返回token
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
