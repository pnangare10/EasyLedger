package com.example.demo;

import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
//        String str = "aba";
//        StringBuilder sb = new StringBuilder();
//        sb.append(str);
//        System.out.println(str.equals(sb.reverse().toString()));

        int[] firstArr = {1,2,3};
        int[] secondArr = Arrays.copyOf(firstArr, firstArr.length);
        System.out.println(secondArr);
    }
}
