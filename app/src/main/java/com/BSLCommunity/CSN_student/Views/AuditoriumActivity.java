package com.BSLCommunity.CSN_student.Views;

import android.app.SearchManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.BSLCommunity.CSN_student.Managers.AnimationManager;
import com.BSLCommunity.CSN_student.Models.AuditoriumModel;
import com.BSLCommunity.CSN_student.Presenters.AuditoriumPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.AuditoriumView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.otaliastudios.zoom.ZoomImageView;

// Форма аудиторий в университете
public class AuditoriumActivity extends BaseActivity implements AuditoriumView {
    private ZoomImageView selectedBuildingImage; // View выбранного корпуса (этажа)
    private TabLayout buildingTabs, floorTabs; // Лаяуты выбора коруса, этажа
    private AuditoriumPresenter auditoriumPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnimationManager.setAnimation(getWindow(), this);
        setContentView(R.layout.activity_auditorium);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTitle(getString(R.string.auditoriums_search));

        buildingTabs = findViewById(R.id.building_tl);
        floorTabs = findViewById(R.id.floor_tl);
        selectedBuildingImage = findViewById(R.id.selectedBuilding);
        selectedBuildingImage.setMaxZoom(5); // Устанавливаем максимальное приближение для зума 5 (стандартное 2.5)
        selectedBuildingImage.setMinZoom(1); // Минимальный зум

        auditoriumPresenter = new AuditoriumPresenter(this, getApplicationContext());
        auditoriumPresenter.changeMap(0, 0);

        buildingTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Устанавливаем изображение выбраного корпуса, 1 этажа
                auditoriumPresenter.changeMap(buildingTabs.getSelectedTabPosition(), 0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        floorTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Устанавливаем изображение этажа (по выбранному корпусу)
                auditoriumPresenter.changeMap(auditoriumPresenter.selectedBuilding, floorTabs.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    /**
     * Строка поиска аудитории
     * @param menu - меню
     * @return true если было создано меню
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Создаем меню поиска
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // Устанавливаем конфигурацию для SearchView
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getString(R.string.aud_search_hint));
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setSubmitButtonEnabled(true);

        // Обработчик поиска
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                AuditoriumModel.Auditorium auditorium = auditoriumPresenter.searchAuditorium(s);

                if (auditorium != null)
                    auditoriumPresenter.changeAuditoriumMap(auditorium, getApplicationContext());
                else
                    Toast.makeText(getApplicationContext(), R.string.no_auditorium, Toast.LENGTH_SHORT).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public void selectTabs(int audBuilding, int audFloor) {
        buildingTabs.getTabAt(audBuilding).select();
        floorTabs.getTabAt(audFloor).select();
    }

    @Override
    public void updateMap(int drawableMapId) {
        selectedBuildingImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), drawableMapId));
        selectedBuildingImage.zoomTo(1, false);
    }

    public void updateAuditoriumMap(final LayerDrawable map) {
        selectedBuildingImage.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                selectedBuildingImage.setImageDrawable(map);
                selectedBuildingImage.removeOnLayoutChangeListener(this);
            }
        });

        selectedBuildingImage.zoomTo(1, false);
    }

    @Override
    public void setFloorTabs(int numberOfFloors) {
        floorTabs.removeAllTabs();

        for (int i = 0; i < numberOfFloors; ++i) {
            TabItem tabItem = new TabItem(getApplicationContext());
            tabItem.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            floorTabs.addView(tabItem);
            floorTabs.getTabAt(i).setText(getString(R.string.floor) + (i + 1));
        }
    }
}