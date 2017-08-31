package com.example.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dss on 2017/8/1.
 */

public class CollectionTest {
    public static void main(String[] args) {
        List<String> a = new ArrayList<String>();
        a.add("1");
        a.add("2");
        System.out.printf(a.toString());
        for (String temp : a) {
            if ("2".equals(temp)) {
                a.remove(temp);
            }
        }
        System.out.printf(a.toString());
    }
}
