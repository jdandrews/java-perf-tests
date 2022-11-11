package com.oracle.jsc.perf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    private static InputStreamReader r = new InputStreamReader(System.in);
    private static BufferedReader br = new BufferedReader(r);

    public static void main(String[] args) {
        System.out.println("Hello, world.");
        readLine("press return to exit:");
    }

    private static String readLine(String prompt) {
        String line = "";
        try {
            System.out.println(prompt);
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }
}
