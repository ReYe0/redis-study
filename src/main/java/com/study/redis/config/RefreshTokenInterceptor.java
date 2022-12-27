package com.study.redis.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.study.redis.pojo.dto.StudentDTO;
import com.study.redis.util.StudentHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.study.redis.constant.RedisConstants.*;

public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.获取请求头中的token
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            return true;
        }
        // 2.基于TOKEN获取redis中的用户
        String key  = LOGIN_STU_KEY + token;
        Map<Object, Object> stuMap = stringRedisTemplate.opsForHash().entries(key);
        // 3.判断用户是否存在
        if (stuMap.isEmpty()) {
            return true;
        }
        // 5.将查询到的hash数据转为UserDTO
        StudentDTO studentDTO = BeanUtil.fillBeanWithMap(stuMap, new StudentDTO(), false);
        // 6.存在，保存用户信息到 ThreadLocal
        StudentHolder.saveStudent(studentDTO);
        // 7.刷新token有效期
        stringRedisTemplate.expire(key, LOGIN_STU_TTL, TimeUnit.MINUTES);
        // 8.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        StudentHolder.removeStudent();
    }
}
