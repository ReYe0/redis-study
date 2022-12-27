# redis 学习笔记之实战篇

标签（空格分隔）： redis

---
## 1.短信登陆
### 1.1实现发送短信验证码功能
1）发送验证码
```java
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
```
2）登录
```java
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
```
### 1.2.实现登陆拦截功能
使用threadlocal来做到线程隔离，每个线程操作自己的一份数据。

1）拦截器代码
```java
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取session
        HttpSession session = request.getSession();
        //2.获取session中的用户
        Object stu = session.getAttribute("stu");
        //3.判断用户是否存在
        if(stu == null){
            //4.不存在，拦截，返回401状态码
            response.setStatus(401);
            return false;
        }
        //5.存在，保存用户信息到Threadlocal
        StudentHolder.saveStudent((StudentDTO) stu);
        //6.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        StudentHolder.removeStudent();

    }
}
```
2）让拦截器生效
```java
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录拦截器
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        "/student/code",
                        "/student/login"
                );
    }
}
```
### 1.3.session共享问题
**核心思路分析：**

集群的每个tomcat中都有一份属于自己的session,假设用户第一次访问第一台tomcat，并且把自己的信息存放到第一台服务器的session中，但是第二次这个用户访问到了第二台tomcat，那么在第二台服务器上，肯定没有第一台服务器存放的session，所以此时 整个登录拦截功能就会出现问题，我们能如何解决这个问题呢？早期的方案是session拷贝，就是说虽然每个tomcat上都有不同的session，但是每当任意一台服务器的session修改时，都会同步给其他的Tomcat服务器的session，这样的话，就可以实现session的共享了

但是这种方案具有两个大问题

1、每台服务器中都有完整的一份session数据，服务器压力过大。

2、session拷贝数据时，可能会出现延迟

所以咱们后来采用的方案都是基于redis来完成，我们把session换成redis，redis数据本身就是共享的，就可以避免session共享的问题了

**session共享问题：**多台Tomcat并不共享session存储空间，当请求切换到不同Tomcat服务时，导致数据丢失的问题。
session的替代方案应该满足：
- 数据共享
- 内存存储
- key、value结构

### 1.4.Redis代替session的业务流程
#### 1.4.1.设计key的结构
首先我们要思考一下利用redis来存储数据，那么到底使用哪种结构呢？由于存入的数据比较简单，我们可以考虑使用String，或者是使用哈希，如果使用String，注意他的value，会多占用一点空间，如果使用哈希，则他的value中只会存储他数据本身，如果不是特别在意内存，其实使用String就可以啦。

#### 1.4.2.设计key的具体细节
所以我们可以使用String结构，就是一个简单的key，value键值对的方式，但是关于key的处理，session他是每个用户都有自己的session，但是redis的key是共享的，咱们就不能使用code了

在设计这个key的时候，我们之前讲过需要满足两点

1、key要具有唯一性

2、key要方便携带

如果我们采用phone：手机号这个的数据来存储当然是可以的，但是如果把这样的敏感数据存储到redis中并且从页面中带过来毕竟不太合适，所以我们在后台生成一个随机串token，然后让前端带来这个token就能完成我们的整体逻辑了

#### 1.4.3.整体访问流程
当注册完成后，用户去登录会去校验用户提交的手机号和验证码，是否一致，如果一致，则根据手机号查询用户信息，不存在则新建，最后将用户数据保存到redis，并且生成token作为redis的key，当我们校验用户是否登录时，会去携带着token进行访问，从redis中取出token对应的value，判断是否存在这个数据，如果没有则拦截，如果存在则将其保存到threadLocal中，并且放行。

### 1.5.用Redis代替session存储短信验证码
```java
//session.setAttribute("code",code);
// set key value ex 120
stringRedisTemplate.opsForValue().set("login:code"+phone,code,2,TimeUnit.MINUTES);
```

### 1.6.基于Redis实现短信登陆
```java
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
```
### 1.7.修改优化拦截器
目前位置，上述代码只能在登陆的时候刷新token，其他访问路径不刷新token，因此修改拦截器：
- 保证所有路径都会刷新token
- 判断用户是否存在，且拦截路径访问

所以使用两个拦截器：
1）所有路劲刷新token
```java
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
```
2）判断用户是否存在，且拦截路径访问
```java
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
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
```
3）配置拦截器生效
```java
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录拦截器
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        "/student/code",
                        "/student/login"
                ).order(1);// order(1) 后执行，值越小，执行等级越高
        // token刷新的拦截器
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).addPathPatterns("/**").order(0);// order(0) 先执行
    }
```