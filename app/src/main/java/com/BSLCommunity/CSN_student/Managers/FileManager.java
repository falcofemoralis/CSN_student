package com.BSLCommunity.CSN_student.Managers;

import android.util.Log;

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

    /**
     * Удаление файлов пользователя
     */
    public static void deleteAllFiles() {
        for (int i = 0; i < 2; ++i) {
            String path = i == 0 ? ROOT_FILES : ROOT_IMAGES;

            File dir = new File(path);
            File[] files = dir.listFiles();

            if (files != null) {
                for (File file : files) {
                    try {
                        file.delete();
                    } catch (Exception e) {
                        Log.e("FILE_MANAGER", file.getName() + " cannot be deleted");
                    }

                }
            }
        }
    }
}
