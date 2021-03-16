package com.BSLCommunity.CSN_student.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.Toast;

import com.BSLCommunity.CSN_student.APIs.SubjectApi;
import com.BSLCommunity.CSN_student.App;
import com.BSLCommunity.CSN_student.Managers.FileManager;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.lib.ExCallable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
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

    /**
     * Скачивание предметов группы юзера с сервера
     *
     * @param idGroup    - id группы
     * @param exCallable - колбек
     */
    public void getGroupSubjects(final int idGroup, final ExCallable<ArrayList<Subject>> exCallable) {
        SubjectApi subjectApi = retrofit.create(SubjectApi.class);
        Call<ArrayList<Subject>> call = subjectApi.groupSubjects(idGroup);
        call.enqueue(new Callback<ArrayList<Subject>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<Subject>> call, @NotNull retrofit2.Response<ArrayList<Subject>> response) {
                subjects = response.body();
                save();
                exCallable.call(subjects);
            }

            @Override
            public void onFailure(Call<ArrayList<Subject>> call, Throwable t) {
                exCallable.fail(R.string.no_connection_server);
                Log.d("ERROR_API", t.toString());
            }
        });
    }

    /**
     * Скачивание изображений с сервера
     *
     * @param exCallable - колбек
     */
    public void downloadSubjectImages(final ExCallable<Integer> exCallable) {
        (new Thread() {
            @Override
            public void run() {
                Context context = App.getApp().context();
                boolean isFailed = false;

                for (Subject subject : subjects) {
                    if (subject.imgPath != null) {
                        try {
                            InputStream input = new java.net.URL(subject.getImgPath()).openStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(input);
                            saveImage(new BitmapDrawable(context.getResources(), bitmap), subject.getImgName(), context);
                            exCallable.call(-1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            exCallable.fail(-1);
                        }
                    }
                }

                if (!isFailed) {
                    exCallable.call(-1);
                }
            }
        }).start();
    }

    /**
     * Сохранение предметов на устройстве
     */
    public void save() {
        String data = (new Gson()).toJson(subjects);
        try {
            FileManager.writeFile(DATA_FILE_NAME, data, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Сохранение изображения на устройстве
     *
     * @param bmp       - изображение
     * @param nameImage - название изображения
     * @param context   - контекст
     */
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
            Toast.makeText(context, nameImage + " failed to save", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Поиск предмета по id
     *
     * @param id - id предмета
     * @return найденный предмет или null если он не существет
     */
    public Subject findById(int id) {
        for (int i = 0; i < subjects.size(); ++i)
            if (subjects.get(i).idSubject == id)
                return subjects.get(i);
        return null;
    }
}
