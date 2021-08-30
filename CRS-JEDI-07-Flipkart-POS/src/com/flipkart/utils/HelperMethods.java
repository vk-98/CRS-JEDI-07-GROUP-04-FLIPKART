package com.flipkart.utils;

public class HelperMethods {
    public static String repeat(String str, int n) {
        String toReturn = "";
        for (int i = 0; i < n; i++) {
            toReturn += str;
        }
        return toReturn;
    }
}
