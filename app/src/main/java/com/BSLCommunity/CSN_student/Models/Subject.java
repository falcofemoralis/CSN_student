package com.BSLCommunity.CSN_student.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.BSLCommunity.CSN_student.APIs.SubjectApi;
import com.google.gson.annotations.SerializedName;

import java.io.File;

public class Subject {
    @SerializedName("teachers")
    public int[] idTeachers;
    @SerializedName("name")
    public String name;
    @SerializedName("imgPath")
    public String imgPath;

    public Subject(int[] idTeachers, String name, String imgPath) {
        this.idTeachers = idTeachers;
        this.name = name;
        this.imgPath = imgPath;
    }

    public String getImgPath() {
        if (imgPath != null)
            return SubjectApi.BASE_URL + imgPath;
        else return null;
    }

    public String getImgName() {
        if (imgPath != null) {
            String[] arr = imgPath.split("/");
            return arr[arr.length - 1];
        } else {
            return null;
        }
    }

    /**
     * Получение изображение с устройства
     *
     * @param context - контекст приложение
     * @return - изображение
     */
    public BitmapDrawable getSubjectImage(final Context context) {
        File imageFile = new File(context.getDir("images", context.MODE_PRIVATE) + "/" + getImgName());

        // Если изображение найдено - возвращаем, если не найдено - его не существует для данной дисциплины
        if (imageFile.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            return new BitmapDrawable(bmp);
        } else
            return null;
    }
}