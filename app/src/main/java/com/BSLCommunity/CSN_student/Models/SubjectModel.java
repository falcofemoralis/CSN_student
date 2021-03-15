package com.BSLCommunity.CSN_student.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.Toast;

import com.BSLCommunity.CSN_student.APIs.SubjectApi;
import com.BSLCommunity.CSN_student.Managers.FileManager;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.lib.ExCallable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubjectModel {
    public final String DATA_FILE_NAME = "Subjects";

    private static SubjectModel instance;
    private Retrofit retrofit;

    public ArrayList<Subject> subjects;

    private SubjectModel() {
    }

    public static SubjectModel getSubjectModel() {
        if (instance == null) {
            instance = new SubjectModel();
            instance.init();
        }
        return instance;
    }

    // Инциализация
    public void init() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl(SubjectApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        try {
            String data = FileManager.readFile(DATA_FILE_NAME);
            Type type = new TypeToken<ArrayList<Subject>>() {
            }.getType();
            subjects = (new Gson()).fromJson(data, type);
        } catch (Exception e) {
            subjects = new ArrayList<>();
        }
    }

    /* Загрузка данных о дисциплине с сервера
     * Параметры:
     * context - контекст приложения или активитиы
     * callback - дальнейшие действия которые необходимо будет выполнить после запроса
     * */
    public Thread getGroupSubjects(final int idGroup, final ExCallable<ArrayList<Subject>> exCallable) {
        return (new Thread() {
            @Override
            public void run() {
                if (!subjects.isEmpty()) {
                    exCallable.call(subjects);
                    return;
                }

                SubjectApi subjectApi = retrofit.create(SubjectApi.class);
                Call<ArrayList<Subject>> call = subjectApi.groupSubjects(idGroup);
                try {
                    subjects = call.execute().body();
                    save();
                    exCallable.call(subjects);
                } catch (IOException e) {
                    exCallable.fail(R.string.no_connection_server);
                    Log.d("ERROR_API", e.toString());
                }
            }
        });
    }

    public Thread downloadSubjectImages(final ExCallable<Integer> exCallable, final Context context) {
        return (new Thread() {
            @Override
            public void run() {
                for (Subject subject : subjects) {
                    if (subject.imgPath != null) {
                        try {
                            InputStream input = new java.net.URL(subject.imgPath).openStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(input);
                            saveImage(new BitmapDrawable(context.getResources(), bitmap), subject.img, context);
                            exCallable.call(-1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            exCallable.fail(-1);
                        }
                    }
                }
            }
        });
    }

    // Сохраняет данные о предметах в Json файл
    public void save() {
        String data = (new Gson()).toJson(subjects);
        try {
            FileManager.writeFile(DATA_FILE_NAME, data, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Сохраняем изображение дисциплин в дирректорию .../files/images
     * bmp - изображение
     * nameImage - название изображения
     * context - контекст приложения
     * */
    public void saveImage(BitmapDrawable bmp, String nameImage, Context context) {
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

}
