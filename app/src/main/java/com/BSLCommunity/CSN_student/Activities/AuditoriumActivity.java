package com.BSLCommunity.CSN_student.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import com.BSLCommunity.CSN_student.Objects.AuditoriumsList;
import com.BSLCommunity.CSN_student.R;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.otaliastudios.zoom.ZoomLayout;

public class AuditoriumActivity extends AppCompatActivity {
    ImageView selectedAudImage, selectedBuildingImage;   //view аудитории, и выбранного корпуса(этажа)
    // ZoomImageView selectedBuildingImage;
    int[][] buildingsMaps; //карты корпусов(этажей), где [1][0] - 3 корпус 1 этаж (нету плана 2-ого корпуса)
    int selectedBuilding, selectedFloor; //индекс выбранного корпуса
    TabLayout building_tl, floor_tl; //лаяуты выбора коруса, этажа
    AuditoriumsList auditoriumsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditorium);

        //получаем необходимые объекты
        selectedAudImage = findViewById(R.id.selectedAud);
        selectedBuildingImage = findViewById(R.id.selectedBuilding);
        selectedBuildingImage.setImageDrawable(getDrawable(R.drawable.building1_1));
        building_tl = findViewById(R.id.building_tl);
        floor_tl = findViewById(R.id.floor_tl);
        buildingsMaps = new int[][]{
                {R.drawable.building1_1, R.drawable.building1_2, R.drawable.building1_3},
                {R.drawable.building3_1, R.drawable.building3_2, R.drawable.building3_3, R.drawable.building3_4, R.drawable.building3_5},
                {R.drawable.building4_1, R.drawable.building4_2, R.drawable.building4_3, R.drawable.building4_4},
                {R.drawable.building5_1, R.drawable.building5_2, R.drawable.building5_3, R.drawable.building5_4}};

        //устанавливаем максимальное приближение для зума 5 (стандартное 2.5)
        ZoomLayout zoomLayout = findViewById(R.id.zoomLayout);
        zoomLayout.setMaxZoom(5);
        zoomLayout.setMinZoom(1);

        auditoriumsList = new AuditoriumsList(this);

        //ставим листенер на вкладки корпусов
        building_tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //устанавливаем изображение корпуса (1 этажа) и сохраняем индекс корпуса
                selectedBuildingImage.setImageDrawable(getDrawable(buildingsMaps[building_tl.getSelectedTabPosition()][0]));
                selectedBuilding = building_tl.getSelectedTabPosition();

                //очищаем этажи пред корпуса и устанавливаем вкладки этажей нового корпуса
                floor_tl.removeAllTabs();
                setFloorTabs();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //возращаем аудиторию в исходное состаяние
                //      selectedAudImage.setVisibility(View.GONE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        floor_tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //устанавливаем изображение этажа (по выбранному корпусу) и сохраняем индекс этажа
                selectedBuildingImage.setImageDrawable(getDrawable(buildingsMaps[selectedBuilding][floor_tl.getSelectedTabPosition()]));
                selectedFloor = floor_tl.getSelectedTabPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //возращаем аудиторию в исходное состаяние
                selectedAudImage.setVisibility(View.GONE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //создаем меню поиска
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        //устанавливаем конфигурацию для SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Введите 51 или акт.зал"); //hint
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        //searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                final AuditoriumsList.Auditorium auditoriumInfo = auditoriumsList.getInfo(s.toLowerCase());
                //сравниваем набранным номер аудитории
                if (auditoriumInfo != null) {
                    //задаем параметры вью (высота, ширина), (коррдинаты x,y)

                    RelativeLayout.LayoutParams parameter = (RelativeLayout.LayoutParams) selectedAudImage.getLayoutParams();
                    parameter.height = getUnitFromDp(auditoriumInfo.height);
                    parameter.width = getUnitFromDp(auditoriumInfo.width);
                    parameter.setMargins(getUnitFromDp(auditoriumInfo.x), getUnitFromDp(auditoriumInfo.y), parameter.rightMargin, parameter.bottomMargin); // left, top, right, bottom
                    selectedAudImage.setLayoutParams(parameter);

                    int audBuilding = auditoriumInfo.building - 1;
                    int audFloor = auditoriumInfo.floor - 1;
                    //получение индекса корпуса в массив, но т.к 2 отсуствует, то (3)2 -> 1, а (1)0 -> 0
                    if (audBuilding != 0) audBuilding--;

                    //устанавливаем вкладку нужного корпуса
                    TabLayout.Tab buildingTab = building_tl.getTabAt(audBuilding);
                    buildingTab.select();

                    //устанавливаем вкладки этажей нужного корпуса
                    floor_tl.removeAllTabs();
                    setFloorTabs();
                    TabLayout.Tab floorTab = floor_tl.getTabAt(audFloor);
                    floorTab.select();

                    //TODO возможность увелечения кач-ва и использованием ZoomImage
                  /*  Drawable bottom = ContextCompat.getDrawable(getApplicationContext(), buildingsMaps[audBuilding][audFloor]);
                    Drawable top =  ContextCompat.getDrawable(getApplicationContext(), R.drawable.auditoriumsquare);
                    top.setTint(getColor(R.color.auditoriumColor));
                    final LayerDrawable layer = new LayerDrawable(new Drawable[]{top, bottom});
                    selectedBuildingImage.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                                   int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            //get the real height after it is calculated
                            int height = selectedBuildingImage.getHeight();
                            int  = selectedBuildingImage.get();
                            //set the height half of the available space for example purposes,
                            //the drawables will have half of the available height
                          //  int halfHeight = height / 2;
                            //the bottom drawable need to start when the top drawable ends
                            layer.setLayerInset(0, auditoriumInfo.x+(height/2), auditoriumInfo.y-(height/20), 0, 0);
                            //the top drawable need to end before the bottom drawable starts
                            layer.setLayerSize(0,auditoriumInfo.width+88, auditoriumInfo.height+110);

                            selectedBuildingImage.setImageDrawable(layer);
                            selectedBuildingImage.removeOnLayoutChangeListener(this);
                        }
                    });*/

                    selectedBuildingImage.setImageDrawable(getDrawable(buildingsMaps[audBuilding][audFloor]));

                    //включаем вью аудитории
                    selectedAudImage.setVisibility(View.VISIBLE);

                } else {
                    //убираем вью аудитории
                    try {
                        selectedAudImage.setVisibility(View.GONE);
                        // selectedBuildingImage.setImageDrawable(getDrawable(buildingsMaps[audBuilding][audFloor]));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    //перевод dp юнитов в пиксели
    private int getUnitFromDp(int dp) {
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    //считаем кол-во этажей и устанавливаем их вкладки
    private void setFloorTabs() {
        for (int i = 0; i < buildingsMaps[building_tl.getSelectedTabPosition()].length; ++i) {
            TabItem tabItem = new TabItem(AuditoriumActivity.this);
            tabItem.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            floor_tl.addView(tabItem);
            floor_tl.getTabAt(i).setText("Этаж " + (i + 1));
        }
    }
}