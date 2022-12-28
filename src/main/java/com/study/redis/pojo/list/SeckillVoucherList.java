package com.study.redis.pojo.list;

import com.study.redis.pojo.SeckillVoucher;

import java.time.LocalDateTime;

public class SeckillVoucherList {
    private static SeckillVoucher head =  new SeckillVoucher(0l,0,LocalDateTime.now(), LocalDateTime.now(),LocalDateTime.now());


    public static void add(SeckillVoucher voucher) {
        //因为head不能动 所以创建一个临时变量 辅助遍历
        SeckillVoucher temp = head;
        while(true) {
            if(temp.next == null) {
                break;
            }
            temp = temp.next;
        }
        //当while循环退出时 就找到最后一个节点
        //将这个节点的next域指向新的节点
        temp.next=voucher;
    }

    //显示节点
    public static void list() {
        //判断链表是否为空
        if(head.next==null) {
            System.out.println("链表为空");
            return;
        }

        SeckillVoucher temp = head.next;
        while(true) {
            if(temp==null) {
                break;
            }
            System.out.println(temp);
            temp = temp.next;
        }
    }

    public static SeckillVoucher findById(Long id ) {
        if(head == null) {
            System.out.println("链表为空！");
            return new SeckillVoucher(0l,0,LocalDateTime.now(), LocalDateTime.now(),LocalDateTime.now());
        }
        SeckillVoucher cur = head;
        while(cur != null) {
            if(cur.getVoucherId() == id) {
                cur.setNext(null);
                return cur;
            }
            cur = cur.next;
        }
        return null;
    }

    public static Boolean updateById(SeckillVoucher voucher) {
        //判断链表是否为空
        if(head.next==null) {
            System.out.println("链表为空");
            return false;
        }
        SeckillVoucher cur = head;
        while (cur != null){
            if(cur.getVoucherId() == voucher.getVoucherId()){
                //更新逻辑
                cur.setStock(voucher.getStock());
                cur.setBeginTime(voucher.getBeginTime());
                cur.setEndTime(voucher.getEndTime());
                return true;
            }
            cur = cur.next;
        }
        return false;
    }
}
