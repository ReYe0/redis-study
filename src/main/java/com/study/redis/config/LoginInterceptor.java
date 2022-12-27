package com.study.redis.config;

import com.study.redis.pojo.dto.StudentDTO;
import com.study.redis.util.StudentHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        //1.获取session，TODO 之后获取请求头中的token
//        HttpSession session = request.getSession();
//        //2.获取session中的用户，TODO 之后基于TOKEN获取redis中的用户
//        Object stu = session.getAttribute("stu");
//        //3.判断用户是否存在
//        if(stu == null){
//            //4.不存在，拦截，返回401状态码
//            response.setStatus(401);
//            return false;
//        }
//        //TODO 将查询到的Hash数据转为StudentDTO对象
//
//        //5.存在，保存用户信息到Threadlocal
//        StudentHolder.saveStudent((StudentDTO) stu);
//        //TODO 刷新token有效期
//        //6.放行
//        return true;


        // 1.判断是否需要拦截（ThreadLocal中是否有用户）
        if (StudentHolder.getStudent() == null) {
            // 没有，需要拦截，设置状态码
            response.setStatus(401);
            // 拦截
            return false;
        }
        // 有用户，则放行
        return true;
    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        // 移除用户
//        StudentHolder.removeStudent();
//
//    }
}
