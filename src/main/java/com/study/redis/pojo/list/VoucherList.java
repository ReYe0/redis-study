package com.study.redis.pojo.list;

import com.study.redis.pojo.Voucher;

import java.time.LocalDateTime;

public class VoucherList {
    private static Voucher head =  new Voucher(0l,0l,"","","",0l,0l,0,0,0,LocalDateTime.now(),LocalDateTime.now());


    public static void add(Voucher voucher) {
        //因为head不能动 所以创建一个临时变量 辅助遍历
        Voucher temp = head;
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

        Voucher temp = head.next;
        while(true) {
            if(temp==null) {
                break;
            }
            System.out.println(temp);
            temp = temp.next;
        }
    }

    public static Voucher findById(Long id ) {
        if(head == null) {
            System.out.println("链表为空！");
            return new Voucher(0l,0l,"链表为空","","",0l,0l,0,0,0,LocalDateTime.now(),LocalDateTime.now());
        }
        Voucher cur = head;
        while(cur != null) {
            if(cur.getId() == id) {
                cur.setNext(null);
                return cur;
            }
            cur = cur.next;
        }
        return null;
    }

    public static void updateById(Voucher voucher) {
        //判断链表是否为空
        if(head.next==null) {
            System.out.println("链表为空");
            return;
        }
        Voucher cur = head;
        while (cur != null){
            if(cur.getId() == voucher.getId() && cur.getShopId() == voucher.getShopId()){
                //更新逻辑
                cur.setTitle(voucher.getTitle());
                cur.setSubTitle(voucher.getSubTitle());
                cur.setRules(voucher.getRules());
                cur.setPayValue(voucher.getPayValue());
                cur.setActualValue(voucher.getActualValue());
                cur.setType(voucher.getType());
                cur.setStatus(voucher.getStatus());
                cur.setStock(voucher.getStock());
                cur.setBeginTime(voucher.getBeginTime());
                cur.setEndTime(voucher.getEndTime());
            }
            cur = cur.next;
        }
    }
}
