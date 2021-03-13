package com.BSLCommunity.CSN_student.ViewInterfaces;

import android.graphics.drawable.LayerDrawable;

public interface AuditoriumView {
    /**
     * Выбор вкладок при поиске аудитории
     *
     * @param audBuilding - корпус, где находится аудитория
     * @param audFloor    - этаж
     */
    void selectTabs(int audBuilding, int audFloor);

    /**
     * Обновление карты
     *
     * @param drawableMapId - id изображения
     */
    void updateMap(int drawableMapId);

    /**
     * Установка вкладок этажей в корпусе
     *
     * @param numberOfFloors - кол-во этажей в корпусе
     */
    void setFloorTabs(int numberOfFloors);

    /**
     * Обновленик карты с выбранной аудиторией
     *
     * @param map - карта с установленный аудиторией
     */
    void updateAuditoriumMap(final LayerDrawable map);
}
