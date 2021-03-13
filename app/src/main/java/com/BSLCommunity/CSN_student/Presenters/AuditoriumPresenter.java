package com.BSLCommunity.CSN_student.Presenters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import androidx.core.content.ContextCompat;

import com.BSLCommunity.CSN_student.Models.AuditoriumModel;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.AuditoriumView;

public class AuditoriumPresenter {
    static class ImageScale {
        int x_left, x_right;
        int y_top, y_bot;

        public ImageScale(int x_left, int x_right, int y_top, int y_bot) {
            this.x_left = x_left;
            this.x_right = x_right;
            this.y_top = y_top;
            this.y_bot = y_bot;
        }
    } // Класс, в котором хранится расположение карт относительно лаяуту

    private final int[][] buildingsMaps; // Карты корпусов(этажей), где [1][0] - 3 корпус 1 этаж (нету плана 2-ого корпуса)
    private final ImageScale[][] imageScales; // Массив расположений карт
    public int selectedBuilding = -1, selectedFloor = -1; // Индексы выбранного корпуса\этажа
    public int audBuilding = 0, audFloor = 0; // Индексы корпуса\этажа, где находится аудитория
    private final AuditoriumView auditoriumView;
    private final AuditoriumModel auditoriumModel;

    public AuditoriumPresenter(AuditoriumView auditoriumView, Context context) {
        this.buildingsMaps = new int[][]{
                {R.drawable.building1_1, R.drawable.building1_2, R.drawable.building1_3},
                {R.drawable.building3_1, R.drawable.building3_2, R.drawable.building3_3, R.drawable.building3_4, R.drawable.building3_5},
                {R.drawable.building4_1, R.drawable.building4_2, R.drawable.building4_3, R.drawable.building4_4},
                {R.drawable.building5_1, R.drawable.building5_2, R.drawable.building5_3, R.drawable.building5_4}};

        this.imageScales = new ImageScale[][]{
                {new ImageScale(0, 400, 213, 387), new ImageScale(0, 400, 216, 384), new ImageScale(0, 400, 219, 382)},
                {new ImageScale(0, 400, 219, 381), new ImageScale(0, 400, 228, 372), new ImageScale(0, 400, 214, 385), new ImageScale(0, 400, 214, 385), new ImageScale(0, 400, 214, 385)},
                {new ImageScale(0, 400, 64, 536), new ImageScale(0, 400, 84, 517), new ImageScale(0, 400, 88, 511), new ImageScale(0, 400, 88, 511)},
                {new ImageScale(121, 278, 0, 600), new ImageScale(101, 299, 0, 600), new ImageScale(101, 299, 0, 600), new ImageScale(101, 299, 0, 600)}};
        this.auditoriumView = auditoriumView;
        this.auditoriumModel = new AuditoriumModel(context);
    }

    /**
     * Поиск аудитории по строке
     *
     * @param s - текст, который написал пользователь
     * @return Обьект аудитории
     */
    public AuditoriumModel.Auditorium searchAuditorium(String s) {
        final AuditoriumModel.Auditorium auditoriumInfo = auditoriumModel.findAuditoriumInfoByName(s.toLowerCase());

        if (auditoriumInfo != null) {
            // Задаем параметры вью (высота, ширина), (коррдинаты x,y)
            audBuilding = auditoriumInfo.building - 1;
            audFloor = auditoriumInfo.floor - 1;

            //получение индекса корпуса в массив, но т.к 2 отсуствует, то (3)2 -> 1, а (1)0 -> 0
            if (audBuilding != 0) audBuilding--;

            auditoriumView.setFloorTabs(buildingsMaps[audBuilding].length);
            auditoriumView.selectTabs(audBuilding, audFloor);

            return auditoriumInfo;
        }

        return null;
    }

    /**
     * Смена карты
     *
     * @param building - номер корпуса
     * @param floor    - номер этажа
     */
    public void changeMap(int building, int floor) {
        boolean shouldUpdate = false;
        if (building != selectedBuilding) {
            selectedBuilding = building;
            auditoriumView.setFloorTabs(buildingsMaps[building].length);
            shouldUpdate = true;
        }

        if (floor != selectedFloor) {
            selectedFloor = floor;
            shouldUpdate = true;
        }

        if (shouldUpdate) {
            auditoriumView.updateMap(buildingsMaps[building][floor]);
        }
    }

    /**
     * Изменение карты с отмеченной аудиторией
     *
     * @param auditorium - обьект аудтории, в котором хранится его расположение на карте
     * @param context    - контекст приложения, нужен для взятия изображений карты корпуса и квадрата аудитории
     */
    public void changeAuditoriumMap(AuditoriumModel.Auditorium auditorium, Context context) {
        // Крафтим изображение корпуса с аудиторием (Мы буквально склеиваем их)
        final Drawable bottomDrawable = ContextCompat.getDrawable(context, buildingsMaps[audBuilding][audFloor]);
        final Drawable topDrawable = (ContextCompat.getDrawable(context, R.drawable.auditoriumsquare));
        topDrawable.setTint(context.getColor(R.color.main_color_3));
        final LayerDrawable newMap = new LayerDrawable(new Drawable[]{topDrawable, bottomDrawable});

        int height = bottomDrawable.getIntrinsicHeight();
        int width = bottomDrawable.getIntrinsicWidth();

        double dx = (auditorium.x - imageScales[audBuilding][audFloor].x_left) / (double) (imageScales[audBuilding][audFloor].x_right - imageScales[audBuilding][audFloor].x_left);
        double dy = (auditorium.y - imageScales[audBuilding][audFloor].y_top) / (double) (imageScales[audBuilding][audFloor].y_bot - imageScales[audBuilding][audFloor].y_top);

        double newX = dx * width;
        double newY = dy * height;

        newMap.setLayerInset(0, (int) newX, (int) newY, 0, 0);
        newMap.setLayerSize(0,
                (auditorium.width * width) / (imageScales[audBuilding][audFloor].x_right - imageScales[audBuilding][audFloor].x_left),
                (auditorium.height * height) / (imageScales[audBuilding][audFloor].y_bot - imageScales[audBuilding][audFloor].y_top));

        auditoriumView.updateAuditoriumMap(newMap);
    }
}
