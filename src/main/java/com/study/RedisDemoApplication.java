package com.study;

import com.study.redis.pojo.Shop;
import com.study.redis.pojo.Voucher;
import com.study.redis.pojo.list.ShopList;
import com.study.redis.pojo.Student;
import com.study.redis.pojo.list.StudentList;
import com.study.redis.pojo.list.VoucherList;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;

@SpringBootApplication
public class RedisDemoApplication {

    public static void main(String[] args) {
        initStu();
        initShop();
        initVoucher();
        SpringApplication.run(RedisDemoApplication.class, args);
    }

    //初始化学生，代替数据库
    public static void initStu(){
        StudentList.add(new Student(2l,"15328346433","66666","erye1",""));
        StudentList.add(new Student(3l,"15328346432","77777","erye2",""));
        StudentList.add(new Student(4l,"15328346431","88888","erye3",""));
    }

    //初始化商铺，代替数据库
    public static void initShop(){
        ShopList.add(new Shop(2l,"二爷特制奶茶一号","二爷大街一号"));
        ShopList.add(new Shop(3l,"二爷特制奶茶二号","二爷大街二号"));
        ShopList.add(new Shop(4l,"二爷特制奶茶三号","二爷大街三号"));
    }

    //初始化优惠券，代替数据库
    public static void initVoucher(){
        LocalDateTime begin = LocalDateTime.of(2022, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 1, 0, 0, 0);
        VoucherList.add(new Voucher(2l,0l,"","","",0l,0l,0,0,0,begin,end));
        VoucherList.add(new Voucher(3l,1l,"","","",0l,0l,0,0,0,begin,end));
        VoucherList.add(new Voucher(4l,2l,"","","",0l,0l,0,0,0,begin,end));
        VoucherList.add(new Voucher(5l,2l,"","","",0l,0l,0,0,0,begin,end));
    }

}
