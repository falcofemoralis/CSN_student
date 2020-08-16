package com.BSLCommunity.CSN_student.Managers;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JSONHelper {
    // Приватный конструктор запрещающий создание экземпляром класса
    private JSONHelper() {
    }

    //Чтение содержимого json файла с assets
    public static String loadJSONFromAsset(Context context, String jsonFileName)
            throws IOException {
        AssetManager manager = context.getAssets();
        InputStream is = manager.open(jsonFileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, "UTF-8");
    }

    // Чтение json файла из files
    // Если файл будет не найден - будет произведено чтения файла json из assets
    public static String read(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException fileNotFound) {
            return "NOT FOUND";
        } catch (IOException ioException) {
            return null;
        }
    }

    // Запись в json файл
    // Если файл не будет найден - создастся новый json файл в директории files
    public static void create(Context context, String fileName, String jsonString) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
            return;
        } catch (FileNotFoundException fileNotFound) {
            return;
        } catch (IOException ioException) {
            return;
        }
    }

    //удаление json файла
    public static void delete(Context context, String fileName) {
        File file = context.getFileStreamPath(fileName);
        file.delete();
    }
}
