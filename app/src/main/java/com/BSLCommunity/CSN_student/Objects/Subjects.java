package com.BSLCommunity.CSN_student.Objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.Toast;

import com.BSLCommunity.CSN_student.Managers.DBHelper;
import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.BSLCommunity.CSN_student.R;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

public class Subjects {

    public static final String DATA_FILE_NAME = "Subjects";

    public class SubjectsList {
        public int id, Code_Lector, Code_Practice, Code_Assistant;
        public String NameDiscipline, Image;
    }
    public static SubjectsList[] subjectsList;

    // Инциализация
    public static void init(Context context, final Callable<Void> callBacks) {

        if (subjectsList != null)
            return;

        String response = JSONHelper.read(context, DATA_FILE_NAME);

        if (response.equals("NOT FOUND") || response.equals("null")) {
            downloadFromServer(context, callBacks);
            return;
        }

        Gson gson = new Gson();
        subjectsList = gson.fromJson(response, SubjectsList[].class);

        // После скачивания всех данных вызывается callBack, у объекта который инициировал скачиввание данных с сервер, если это необходимо
        try {
            callBacks.call();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Загрузка данных о дисциплине с сервера
     * Параметры:
     * context - контекст приложения или активитиы
     * callback - дальнейшие действия которые необходимо будет выполнить после запроса
     * */
    public static void downloadFromServer(final Context context, final Callable<Void> callback) {
        String apiUrl = String.format("api/subjects/group?Code_Group=%1$s", User.getInstance().groupId);
        DBHelper.getRequest(context, apiUrl, DBHelper.TypeRequest.STRING, new DBHelper.CallBack<String>() {

            @Override
            public void call(String response) {
                Gson gson = new Gson();
                subjectsList = gson.fromJson(response, SubjectsList[].class);
                save(context);
                try {
                    callback.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Начинаем скачивание изображений
                downloadImageFromServer(context, 0);

                try {
                    callback.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(String message) {
                Toast.makeText(context, R.string.no_connection_server, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* Загрузка изображения с сервера (формат Bitmap)
    * Параметры:
    * context - контекст приложения или активити
    * subject - объект дисциплина, из него берется название изображени
    * callback - дальнейшие действия которые необходимо будет выполнить после запроса
    * */
    public static void downloadImageFromServer(final Context context, final int numSubject) {
        String apiUrl = String.format("api/subjects?image=%s", subjectsList[numSubject].Image);
        DBHelper.getRequest(context, apiUrl, DBHelper.TypeRequest.IMAGE, new DBHelper.CallBack<Bitmap>() {
            @Override
            public void call(Bitmap response) {
                saveImage(new BitmapDrawable(context.getResources(), response), subjectsList[numSubject].Image, context);
                // Если изображение не последней дисциплины - скачивается следующее. Сделано в запросе для того чтобы ограничить скорость отправки самих запросов, иначе возникают проблемы
                // Временный вариант, есть более умные способы ограничивать частоту запросов
                if (subjectsList.length - 1 != numSubject)
                    downloadImageFromServer(context, numSubject + 1);
            }

            @Override
            public void fail(String message) {
                Toast.makeText(context, "image not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* Загрузка изображения с устройства
     * Параметры:
     * context - контекст приложения
     * subject - объект дисциплина, из него берется название изображени
     * */
    public static BitmapDrawable getSubjectImage(final Context context, final SubjectsList subject) {
        File imageFile = new File(context.getDir("images", context.MODE_PRIVATE) + "/" + subject.Image);

        // Если изображение найдено - возвращаем, если не найдено - его не существует для данной дисциплины
        if (imageFile.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            return new BitmapDrawable(bmp);
        }
        else
            return null;
    }

    // Сохраняет данные о предметах в Json файл
    public static void save(Context appContext) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(subjectsList);
        JSONHelper.create(appContext, DATA_FILE_NAME, jsonString);
    }

    /* Сохраняем изображение дисциплин в дирректорию .../files/images
    * bmp - изображение
    * nameImage - название изображения
    *  context - контекст приложения
    * */
    public static void saveImage(BitmapDrawable bmp, String nameImage, Context context) {

        try {
            // Создаем файл изображение
            File imageFile = new File(context.getDir("images", context.MODE_PRIVATE) + "/" + nameImage);
            if (!imageFile.exists())
                imageFile.createNewFile();
            FileOutputStream out = new FileOutputStream(imageFile);
            // Записываем изображение
            bmp.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            Toast.makeText(context, nameImage + " can't save", Toast.LENGTH_SHORT).show();
        }

    }

    // Удаление данных
    public static void delete(Context context) {
        // Удаление файла с информацией о дисциплинах
        JSONHelper.delete(context, DATA_FILE_NAME);

        // Удаление всех изображений
        for (int i = 0; i < subjectsList.length; ++i) {
            File imageFile = new File(context.getDir("images", context.MODE_PRIVATE) + "/" + subjectsList[i].Image);

             if (imageFile.exists())
                imageFile.delete();
        }

        subjectsList = null;
    }
}
