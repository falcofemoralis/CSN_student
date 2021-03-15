package com.BSLCommunity.CSN_student.Models;

import android.content.Context;
import android.util.Log;

import com.BSLCommunity.CSN_student.APIs.CacheApi;
import com.BSLCommunity.CSN_student.Constants.CacheStatusType;
import com.BSLCommunity.CSN_student.Constants.ProgressType;
import com.BSLCommunity.CSN_student.Managers.FileManager;
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
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DataModel {
    public static DataModel instance = null;
    public static boolean isFailed = false;
    public static final String DATA_FILE_NAME = "Cache";

    public static class Cache {
        @SerializedName("creationTime")
        public int creationTime;
        @SerializedName("groupsApi")
        public String groupsApi;
        @SerializedName("subjectsApi")
        public String subjectsApi;
        @SerializedName("teachersApi")
        public String teachersApi;
    }

    public Cache clientCache;
    public Cache serverCache;
    public ArrayList<Thread> threads = new ArrayList<>();
    private Context context;

    private Retrofit retrofit;

    private DataModel() {
    }

    public static DataModel getDataModel(Context context) {
        if (instance == null) {
            instance = new DataModel();
            instance.init();
            instance.context = context;
        }
        return instance;
    }

    public void init() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl(CacheApi.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        try {
            String data = FileManager.readFile(DATA_FILE_NAME);
            Type type = new TypeToken<Cache>() {
            }.getType();
            clientCache = (new Gson()).fromJson(data, type);
        } catch (Exception e) {
            clientCache = null;
        }
    }

    // проверка кеша
    public void checkCache(final ExCallable<CacheStatusType> exCallable) {
        if (clientCache != null) {
            CacheApi cacheApi = retrofit.create(CacheApi.class);
            Call<String> call = cacheApi.checkCache(clientCache.creationTime);
            Log.d("CACHE_API", "cr time: " + clientCache.creationTime);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                    boolean isOld = Boolean.parseBoolean(response.body());
                    Log.d("CACHE_API", "should download: " + isOld);
                    if (isOld) {
                        exCallable.call(CacheStatusType.CACHE_NEED_UPDATE);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                }
            });
        } else {
            exCallable.call(CacheStatusType.NO_CACHE);
        }
    }

    // скачивание данных
    public void downloadCache(final ExCallable<ProgressType> exCallable) {
        CacheApi cacheApi = retrofit.create(CacheApi.class);
        Call<Cache> call = cacheApi.downloadCache();

        // Скачиваем кеш файл
        call.enqueue(new Callback<Cache>() {
            @Override
            public void onResponse(@NotNull Call<Cache> call, @NotNull Response<Cache> response) {
                serverCache = response.body();
                Log.d("CACHE_API", "downloaded!" + serverCache);
                exCallable.call(ProgressType.SET_MAX);
            }

            @Override
            public void onFailure(@NotNull Call<Cache> call, @NotNull Throwable t) {
                // Кеш не был скачан успешно
                exCallable.fail(-1);
                Log.d("CACHE_API", "Fail to get cache!" + t.toString());
            }
        });
    }

    public int initDataToDownload() {
        boolean downloadAll = false;

        if (clientCache == null)
            downloadAll = true;

        if (downloadAll || !serverCache.groupsApi.equals(clientCache.groupsApi))
            threads.add(GroupModel.getGroupModel().getAllGroups(new ExCallable<ArrayList<GroupModel.Group>>() {
                @Override
                public void call(ArrayList<GroupModel.Group> data) {
                    Log.d("CACHE_API", "downloaded groups!");
                }

                @Override
                public void fail(int idResString) {
                    isFailed = true;
                }
            }));

        if (downloadAll || !serverCache.groupsApi.equals(clientCache.groupsApi))
            threads.add(GroupModel.getGroupModel().loadSchedule(new ExCallable<Integer>() {
                @Override
                public void call(Integer data) {
                    Log.d("CACHE_API", "downloaded groups schedule!");
                }

                @Override
                public void fail(int idResString) {
                    isFailed = true;
                }
            }));

        if (downloadAll || !serverCache.teachersApi.equals(clientCache.teachersApi))
            threads.add(TeacherModel.getTeacherModel().getAllTeachers(new ExCallable<ArrayList<TeacherModel.Teacher>>() {
                @Override
                public void call(ArrayList<TeacherModel.Teacher> data) {
                    Log.d("CACHE_API", "downloaded teachers!");
                }

                @Override
                public void fail(int idResString) {
                    isFailed = true;
                }
            }));
        if (downloadAll || !serverCache.teachersApi.equals(clientCache.teachersApi))
            threads.add(TeacherModel.getTeacherModel().loadSchedule(new ExCallable<Integer>() {
                @Override
                public void call(Integer data) {
                    Log.d("CACHE_API", "downloaded teacher schedule!");
                }

                @Override
                public void fail(int idResString) {
                    isFailed = true;
                }
            }));
        if (downloadAll || !serverCache.subjectsApi.equals(clientCache.subjectsApi))
            threads.add(SubjectModel.getSubjectModel().getGroupSubjects(UserData.getUserData().user.getGroupId(), new ExCallable<ArrayList<Subject>>() {
                @Override
                public void call(ArrayList<Subject> data) {
                    Log.d("CACHE_API", "downloaded subjects!");
                }

                @Override
                public void fail(int idResString) {
                    isFailed = true;
                }
            }));
        if (downloadAll || !serverCache.subjectsApi.equals(clientCache.subjectsApi))
            threads.add(SubjectModel.getSubjectModel().downloadSubjectImages(new ExCallable<Integer>() {
                @Override
                public void call(Integer data) {
                    Log.d("CACHE_API", "downloaded images!");
                }

                @Override
                public void fail(int idResString) {
                    isFailed = true;
                }
            }, context));
        return threads.size();
    }

    public void downloadData(final ExCallable<ProgressType> exCallable) {
        (new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < threads.size(); i++) {
                    Thread thread = threads.get(i);
                    try {
                        thread.run();
                        thread.join();
                        Log.d("CACHE_API", "downloaded!" + i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (isFailed) {
                        break;
                    } else {
                        Log.d("CACHE_API", "Update!" + i);
                        exCallable.call(ProgressType.UPDATE_PROGRESS);
                    }
                }
                if (isFailed) {
                    Log.d("CACHE_API", "Failed to download");
                    exCallable.fail(-1);
                } else {
                    exCallable.call(ProgressType.SET_OK);
                }
            }
        }).start();
    }

    public void save() {
        try {
            String data = (new Gson()).toJson(serverCache);
            FileManager.writeFile(DATA_FILE_NAME, data, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
