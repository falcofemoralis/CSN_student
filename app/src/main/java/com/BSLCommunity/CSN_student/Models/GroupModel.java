package com.BSLCommunity.CSN_student.Models;

import android.util.Log;

import com.BSLCommunity.CSN_student.APIs.GroupApi;
import com.BSLCommunity.CSN_student.Managers.FileManager;
import com.BSLCommunity.CSN_student.R;
import com.BSLCommunity.CSN_student.lib.ExCallable;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupModel {
    public static final String DATA_FILE_NAME = "Groups";

    public static GroupModel instance = null;

    public static class Group {
        @SerializedName("Code_Group")
        public int id;
        @SerializedName("GroupName")
        public String groupName;
        @SerializedName("Course")
        public int course;
        @SerializedName("ScheduleList")
        public ArrayList<ScheduleList> scheduleList;
    }

    public static ArrayList<Group> groups;

    private Retrofit retrofit;

    private GroupModel() {
    }

    public static GroupModel getGroupModel() {
        if (instance == null) {
            instance = new GroupModel();
            instance.init();
        }
        return instance;
    }

    /**
     * Инициализация групп, если группы были скачаны когда либо - берутся из хранилища устрйства
     */
    public void init() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl(GroupApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        try {
            String data = FileManager.readFile(DATA_FILE_NAME);
            Type type = new TypeToken<ArrayList<Group>>() {
            }.getType();
            groups = (new Gson()).fromJson(data, type);
        } catch (Exception e) {
            groups = new ArrayList<>();
        }
    }

    /**
     * Получение только имен групп с сервера (или с устройства если уже скачаны)
     *
     * @param exCallable - колбек
     */
    public void getGroupNames(final ExCallable<ArrayList<Group>> exCallable) {
        if (!groups.isEmpty()) {
            exCallable.call(groups);
            return;
        }

        GroupApi groupApi = retrofit.create(GroupApi.class);
        Call<ArrayList<Group>> call = groupApi.getGroupNames();
        call.enqueue(new Callback<ArrayList<Group>>() {

            @Override
            public void onResponse(Call<ArrayList<Group>> call, Response<ArrayList<Group>> response) {
                groups = response.body();
                save();
                exCallable.call(groups);
            }

            @Override
            public void onFailure(Call<ArrayList<Group>> call, Throwable t) {
                exCallable.fail(R.string.no_connection_server);
                Log.d("ERROR_API", t.toString());
            }
        });
    }

    /**
     * Получение всех академических групп.
     *
     * @param exCallable - колбек
     */
    public void getAllGroups(final ExCallable<ArrayList<Group>> exCallable) {
        GroupApi groupApi = retrofit.create(GroupApi.class);
        Call<ArrayList<Group>> call = groupApi.allGroups();

        call.enqueue(new Callback<ArrayList<Group>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<Group>> call, @NotNull retrofit2.Response<ArrayList<Group>> response) {
                groups = response.body();
                save();
                exCallable.call(groups);

            }

            @Override
            public void onFailure(@NotNull Call<ArrayList<Group>> call, @NotNull Throwable t) {
                exCallable.fail(R.string.no_connection_server);
                Log.d("ERROR_API", t.toString());
            }
        });
    }

    /**
     * Получение групп по курсу
     *
     * @param course - номер курса
     * @return - список найденных групп по курсу
     */
    public ArrayList<String> getGroupsOnCourse(int course) {
        ArrayList<String> grOnCourse = new ArrayList<>();
        for (int i = 0; i < groups.size(); ++i) {
            if (groups.get(i).course == course) {
                grOnCourse.add(groups.get(i).groupName);
            }
        }

        return grOnCourse;
    }

    /**
     * Сохранение данных
     */
    public void save() {
        String data = (new Gson()).toJson(groups);
        try {
            FileManager.writeFile(DATA_FILE_NAME, data, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Поиск группы по id
     *
     * @param id - id группы
     * @return найденная группа или null если она не существет
     */
    public Group findById(int id) {
        for (int i = 0; i < groups.size(); ++i)
            if (groups.get(i).id == id)
                return groups.get(i);
        return null;
    }

    /**
     * Поиск группы по имени
     *
     * @param groupName - имя группы
     * @return найденная группа или null если она не существет
     */
    public Group findByName(String groupName) {
        for (int i = 0; i < groups.size(); ++i)
            if (groups.get(i).groupName.equals(groupName))
                return groups.get(i);
        return null;
    }
}
