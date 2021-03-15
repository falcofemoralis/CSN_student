package com.BSLCommunity.CSN_student.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.google.gson.annotations.SerializedName;

import java.io.File;

public class Subject {
    @SerializedName("teachers")
    public int[] idTeachers;
    @SerializedName("name")
    public String name;
    @SerializedName("imgPath")
    public String imgPath;
    @SerializedName("img")
    public String img;

    public Subject(int[] idTeachers, String name, String imgPath, String img) {
        this.idTeachers = idTeachers;
        this.name = name;
        this.imgPath = imgPath;
        this.img = img;
    }

    /* Загрузка изображения с устройства
     * Параметры:
     * context - контекст приложения
     * subject - объект дисциплина, из него берется название изображени
     * */
    public BitmapDrawable getSubjectImage(final Context context) {
        File imageFile = new File(context.getDir("images", context.MODE_PRIVATE) + "/" + img);

        // Если изображение найдено - возвращаем, если не найдено - его не существует для данной дисциплины
        if (imageFile.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            return new BitmapDrawable(bmp);
        } else
            return null;
    }
}