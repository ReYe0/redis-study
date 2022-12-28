package com.study.redis.pojo.list;

import com.study.redis.pojo.Student;

public class StudentList {
    //先初始化一个头结点 头结点不动 不放具体的数据
    private static Student head = new Student(0l, "","","","");

    /*
     * 添加节点到单向链表中
     * 	思路（不考虑编号顺序时:）
     * 		1.找到当前链表的最后节点
     * 		2.将最后这个节点的next指向新的节点
     */
    public static void add(Student stu) {
        //因为head不能动 所以创建一个临时变量 辅助遍历
        Student temp = head;
        while(true) {
            if(temp.next == null) {
                break;
            }
            temp = temp.next;
        }
        //当while循环退出时 就找到最后一个节点
        //将这个节点的next域指向新的节点
        temp.next=stu;
    }


    //显示节点
    public static void list() {
        //判断链表是否为空
        if(head.next==null) {
            System.out.println("链表为空");
            return;
        }

        Student temp = head.next;
        while(true) {
            if(temp==null) {
                break;
            }
            System.out.println(temp);
            temp = temp.next;
        }
    }

    public static Student findByPhone(String phone) {
        if(head == null) {
            System.out.println("链表为空！");
            return new Student(9999l,"链表为空","链表为空","链表为空","链表为空");
        }
        Student cur = head;
        while(cur != null) {
            if(cur.getPhone() == phone) {
                return cur;
            }
            cur = cur.next;
        }
        return null;
    }
}
