package com.BSLCommunity.CSN_student.Objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.Toast;
import com.BSLCommunity.CSN_student.Activities.MainActivity;
import com.BSLCommunity.CSN_student.Managers.JSONHelper;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

public class Subjects {

    static final String DATA_FILE_NAME = "Subjects";

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

        if (response.equals("NOT FOUND")) {
            downloadFromServer(context, callBacks);
            return;
        }

        Gson gson = new Gson();
        subjectsList = gson.fromJson(response, SubjectsList[].class);
    }

    /* Загрузка данных о дисциплине с сервера
     * Параметры:
     * context - контекст приложения или активитиы
     * callback - дальнейшие действия которые необходимо будет выполнить после запроса
     * */
    public static void downloadFromServer(final Context context, final Callable<Void> callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = MainActivity.MAIN_URL + String.format("api/subjects/?Code_Group=%1$s",   User.getInstance().groupId);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "No connection with our server,try later...", Toast.LENGTH_SHORT).show();

                try {
                    String response = JSONHelper.read(context, MainActivity.GROUP_FILE_NAME);
                    Gson gson = new Gson();
                    subjectsList = gson.fromJson(response, SubjectsList[].class);
                    try {
                        callback.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {

                }
            }
        });
        requestQueue.add(request);
    }

    /* Загрузка изображения с сервера (формат Bitmap)
    * Параметры:
    * context - контекст приложения или активити
    * subject - объект дисциплина, из него берется название изображени
    * callback - дальнейшие действия которые необходимо будет выполнить после запроса
    * */
    public static void downloadImageFromServer(final Context context, final int numSubject) {

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = MainActivity.MAIN_URL + String.format("api/subjects?image=%s", subjectsList[numSubject].Image);
        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {

            @Override
            public void onResponse(Bitmap response) {
                saveImage(new BitmapDrawable(response), subjectsList[numSubject].Image, context);
                // Если изображение не последней дисциплины - скачивается следующее. Сделано в запросе для того чтобы ограничить скорость отправки самих запросов, иначе возникают проблемы
                // Временный вариант, есть более умные способы ограничивать частоту запросов
                if (subjectsList.length - 1 != numSubject)
                    downloadImageFromServer(context, numSubject + 1);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "image not found", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(imageRequest);
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
