package com.lr.app;

import edu.emory.mathcs.backport.java.util.Collections;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author liurui
 * @date 2020/4/1 3:33 下午
 */
public class Solution2 {
    public static void main(String[] args) {
        List<Student> list = Collections.singletonList(new Student("2", "22"));

        Student student1 = new Student("1", "22");
        Student student2 = new Student("2", "22");
        Student student3 = new Student("3", "22");
        Student student4 = new Student("4", "22");
        List<Student> list1 = Arrays.asList(student1,student2,student3,student4);


        list1.forEach(e->{
            if ("1".equals(e.getAge())) {
                return;
            }
            System.out.println(e.getAge());
        });


    }
}
