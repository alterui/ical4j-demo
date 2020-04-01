package com.lr.app;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liurui
 * @date 2020/4/1 9:43 上午
 */
public class Solution1 {
    public static void main(String[] args) {
        verifyParams(true, false);
    }

    public static void verifyParams(Boolean isAdd, Boolean isEdit) {
        List<Integer> ids = new LinkedList<>();
        // isAdd = true
        if (!isAdd) {
            ids = Arrays.asList(1, 3, 4);
        }
        // isEdit = false
        if(!isEdit) {
           ids.forEach(System.out::println);
        }
    }
}
