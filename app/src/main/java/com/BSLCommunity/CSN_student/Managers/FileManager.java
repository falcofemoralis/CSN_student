package com.BSLCommunity.CSN_student.Managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FileManager {
    private static final String ROOT_FILES = "files";
    private static final String ROOT_IMAGES = "images";

    public static String readFile(String src) throws Exception {
        File file = new File(ROOT_FILES, src);
        StringBuilder data = new StringBuilder();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String text = null;
        while ((text = br.readLine()) != null) {
            data.append(text);
        }
        br.close();

        return data.toString();
    }

    public static void writeFile(String src, String content, boolean append) throws Exception {
        File file = new File(ROOT_FILES, src);

        BufferedWriter bw = new BufferedWriter(new FileWriter(file, append));
        bw.write(content);
        bw.close();
    }
}
