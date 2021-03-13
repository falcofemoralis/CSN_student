package com.BSLCommunity.CSN_student.ViewInterfaces;

import android.graphics.drawable.LayerDrawable;

public interface AuditoriumView {
    void selectTabs(int audBuilding, int audFloor, int numberOfFloors);

    void updateMap(int drawableMapId);

    void setFloorTabs(int numberOfFloors);

    void updateAuditoriumMap(final LayerDrawable map);
}
