package com.study.redis.pojo.list;

import com.study.redis.pojo.SeckillVoucher;
import com.study.redis.pojo.VoucherOrder;

import java.time.LocalDateTime;

public class VoucherOrderList {
    private static VoucherOrder head =  new VoucherOrder(0l,0l, 0l,0,0,LocalDateTime.now(),LocalDateTime.now(),LocalDateTime.now(),LocalDateTime.now(),LocalDateTime.now());


    public static void add(VoucherOrder voucher) {
        //因为head不能动 所以创建一个临时变量 辅助遍历
        VoucherOrder temp = head;
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

        VoucherOrder temp = head.next;
        while(true) {
            if(temp==null) {
                break;
            }
            System.out.println(temp);
            temp = temp.next;
        }
    }

    public static VoucherOrder findById(Long voucherId ,Long userId) {
        if(head == null) {
            System.out.println("链表为空！");
            return new VoucherOrder(0l,0l, 0l,0,0,LocalDateTime.now(),LocalDateTime.now(),LocalDateTime.now(),LocalDateTime.now(),LocalDateTime.now());
        }
        VoucherOrder cur = head;
        while(cur != null) {
            if(cur.getVoucherId() == voucherId && cur.getUserId() == userId) {
                cur.setNext(null);
                return cur;
            }
            cur = cur.next;
        }
        return null;
    }

    public static Boolean updateById(VoucherOrder voucher) {
        //判断链表是否为空
        if(head.next==null) {
            System.out.println("链表为空");
            return false;
        }
        VoucherOrder cur = head;
        while (cur != null){
            if(cur.getVoucherId() == voucher.getVoucherId()){
                //更新逻辑
                return true;
            }
            cur = cur.next;
        }
        return false;
    }
}
