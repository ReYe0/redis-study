package com.study.redis.pojo;

public class ShopList {
    private static Shop head =  new Shop(0l,"","");


    public static void add(Shop shop) {
        //因为head不能动 所以创建一个临时变量 辅助遍历
        Shop temp = head;
        while(true) {
            if(temp.next == null) {
                break;
            }
            temp = temp.next;
        }
        //当while循环退出时 就找到最后一个节点
        //将这个节点的next域指向新的节点
        temp.next=shop;
    }

    //显示节点
    public static void list() {
        //判断链表是否为空
        if(head.next==null) {
            System.out.println("链表为空");
            return;
        }

        Shop temp = head.next;
        while(true) {
            if(temp==null) {
                break;
            }
            System.out.println(temp);
            temp = temp.next;
        }
    }

    public static Shop findById(Long id ) {
        if(head == null) {
            System.out.println("链表为空！");
            return new Shop(9999l,"链表为空","链表为空");
        }
        Shop cur = head;
        while(cur != null) {
            if(cur.getId() == id) {
                cur.setNext(null);
                return cur;
            }
            cur = cur.next;
        }
        return null;
    }

    public static void updateById(Shop shop) {
        //判断链表是否为空
        if(head.next==null) {
            System.out.println("链表为空");
            return;
        }
        Shop cur = head;
        while (cur != null){
            if(cur.getId() == shop.getId()){
                cur.setAddress(shop.getAddress());
                cur.setName(shop.getName());
                return;
            }
            cur = cur.next;
        }
        return;
    }
}
