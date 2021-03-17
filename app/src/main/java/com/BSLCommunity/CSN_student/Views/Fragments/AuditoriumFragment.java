package com.BSLCommunity.CSN_student.Views.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.BSLCommunity.CSN_student.Constants.ActionBarType;
import com.BSLCommunity.CSN_student.Models.AuditoriumModel;
import com.BSLCommunity.CSN_student.Presenters.AuditoriumPresenter;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.ViewInterfaces.AuditoriumView;
import com.BSLCommunity.CSN_student.Views.OnFragmentActionBarChangeListener;
import com.BSLCommunity.CSN_student.Views.OnFragmentInteractionListener;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.otaliastudios.zoom.ZoomImageView;

// Форма аудиторий в университете
public class AuditoriumFragment extends Fragment implements AuditoriumView {
    private ZoomImageView selectedBuildingImage; // View выбранного корпуса (этажа)
    private TabLayout buildingTabs, floorTabs; // Лаяуты выбора коруса, этажа
    private AuditoriumPresenter auditoriumPresenter;

    View currentFragment;
    OnFragmentInteractionListener fragmentListener;
    SearchView searchView = null;
    OnFragmentActionBarChangeListener actionBarChangeListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnFragmentInteractionListener) context;
        actionBarChangeListener = (OnFragmentActionBarChangeListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = inflater.inflate(R.layout.fragment_auditorium, container, false);
        getActivity().setTitle(getString(R.string.auditoriums_search));

        buildingTabs = currentFragment.findViewById(R.id.building_tl);
        floorTabs = currentFragment.findViewById(R.id.floor_tl);
        selectedBuildingImage = currentFragment.findViewById(R.id.selectedBuilding);
        selectedBuildingImage.setMaxZoom(5); // Устанавливаем максимальное приближение для зума 5 (стандартное 2.5)
        selectedBuildingImage.setMinZoom(1); // Минимальный зум

        auditoriumPresenter = new AuditoriumPresenter(this, getContext());
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

        return currentFragment;
    }

    @Override
    public void onResume() {
        actionBarChangeListener.changeActionBarState(true);
        actionBarChangeListener.setActionBarColor(R.color.dark_blue, ActionBarType.STATUS_BAR);
        super.onResume();
    }

    /**
     * Строка поиска аудитории
     *
     * @param menu - меню
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Создаем меню поиска
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }

        if (searchView != null) {
            // Устанавливаем конфигурацию для SearchView
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
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
                        auditoriumPresenter.changeAuditoriumMap(auditorium, getContext());
                    else
                        Toast.makeText(getContext(), R.string.no_auditorium, Toast.LENGTH_SHORT).show();

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void selectTabs(int audBuilding, int audFloor) {
        buildingTabs.getTabAt(audBuilding).select();
        floorTabs.getTabAt(audFloor).select();
    }

    @Override
    public void updateMap(int drawableMapId) {
        selectedBuildingImage.setImageDrawable(ContextCompat.getDrawable(getContext(), drawableMapId));
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
            TabItem tabItem = new TabItem(getContext());
            tabItem.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            floorTabs.addView(tabItem);
            floorTabs.getTabAt(i).setText(getString(R.string.floor) + (i + 1));
        }
    }

    @Override
    public void onPause() {
        actionBarChangeListener.changeActionBarState(false);
        actionBarChangeListener.setActionBarColor(R.color.background, ActionBarType.STATUS_BAR);
        super.onPause();
    }
}