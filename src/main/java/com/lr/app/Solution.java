package com.lr.app;

import javax.annotation.PostConstruct;
import java.util.*;

import java.util.stream.Collectors;

/**
 * @author liurui
 * @date 2020/3/31 11:28 上午
 */

public class Solution {

    private static Map<String, Student> studentMap = new HashMap<>();
    static {
        studentMap.put("1", new Student("1","ming"));
        studentMap.put("2", new Student("2","ming"));
    }





    public static void main(String[] args) {

        User user1 = new User(1, "ming");
        User user4 = new User(2, "ming");
        User user2 = new User(1, "kk");


        List<User> list = Arrays.asList(user1,user2,user4);

        Map<Integer, List<User>> map = list.stream().collect(Collectors.groupingBy(e -> {

           /* Student student = getStudent(e.getAge());

            if (student == null) {
                throw new RuntimeException();
            }
            return student.getAge();*/
           return e.getAge();

        }));

        map.forEach((k,v)->{
            System.out.println("K:"+k + v);
        });

//



    }



    private static Student getStudent(int age) {
        return studentMap.get(String.valueOf(age));
    }

}
