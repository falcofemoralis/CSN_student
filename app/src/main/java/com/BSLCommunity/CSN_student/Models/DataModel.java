package com.BSLCommunity.CSN_student.Models;

import android.util.Log;

import com.BSLCommunity.CSN_student.APIs.CacheApi;
import com.BSLCommunity.CSN_student.App;
import com.BSLCommunity.CSN_student.Constants.ApiType;
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
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DataModel {
    public static DataModel instance = null;
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

    public Cache clientCache; // Кеш клиента
    public Cache serverCache; // Кеш сервера
    public ArrayList<ApiType> dataToDownload = new ArrayList<>(); // Данные которые необходимо скачать
    private boolean isFailed; // Булевская переменная которая показывает произошла ли ошибка
    private int downloadedDataSize = 0; // Переменная которая показывает сколько данных мы скачали (т.к они выполняется асинхроно, нам нужно подсчитывать сколько скачалось)

    private Retrofit retrofit;

    private DataModel() {
    }

    public static DataModel getDataModel() {
        if (instance == null) {
            instance = new DataModel();
            instance.init();
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
            String data = FileManager.getFileManager(App.getApp().context()).readFile(DATA_FILE_NAME);
            Type type = new TypeToken<Cache>() {
            }.getType();
            clientCache = (new Gson()).fromJson(data, type);
        } catch (Exception e) {
            clientCache = null;
        }
    }

    /**
     * Проверка на валидность кеша. Идет отправка времени кеша на устройстве на сервер, где оно сверается с временем на сервере.
     *
     * @param exCallable - колбек
     */
    public void checkCache(final ExCallable<CacheStatusType> exCallable) {
        if (clientCache != null) {
            CacheApi cacheApi = retrofit.create(CacheApi.class);
            Call<String> call = cacheApi.checkCache(clientCache.creationTime);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                    boolean isOld = Boolean.parseBoolean(response.body());
                    if (isOld)
                        exCallable.call(CacheStatusType.CACHE_NEED_UPDATE);
                    Log.d("CACHE_API", "update?" + isOld);

                }

                @Override
                public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                }
            });
        } else {
            //Кеш отсуствует на устройстве и его не нужно проверять
            exCallable.call(CacheStatusType.NO_CACHE);
        }
    }

    /**
     * Скачивание кеш файла с сервера
     *
     * @param exCallable - колбек
     */
    public void downloadCache(final ExCallable<ProgressType> exCallable) {
        CacheApi cacheApi = retrofit.create(CacheApi.class);
        Call<Cache> call = cacheApi.downloadCache();

        call.enqueue(new Callback<Cache>() {
            @Override
            public void onResponse(@NotNull Call<Cache> call, @NotNull Response<Cache> response) {
                serverCache = response.body();
                if (clientCache != null) {
                    if (!clientCache.subjectsApi.equals(serverCache.subjectsApi))
                        dataToDownload.add(ApiType.SubjectApi);
                    if (!clientCache.groupsApi.equals(serverCache.groupsApi))
                        dataToDownload.add(ApiType.GroupApi);
                    if (!clientCache.teachersApi.equals(serverCache.teachersApi))
                        dataToDownload.add(ApiType.TeacherApi);
                } else {
                    dataToDownload.addAll(Arrays.asList(ApiType.values()));
                }
                exCallable.call(ProgressType.SET_MAX);
            }

            @Override
            public void onFailure(@NotNull Call<Cache> call, @NotNull Throwable t) {
                // Кеш не был скачан успешно
                exCallable.fail(-1);
            }
        });
    }

    /**
     * Скачиваем необходимые данные
     *
     * @param exCallable - колбек
     */
    public void downloadData(final ExCallable<ProgressType> exCallable) {
        isFailed = false;
        downloadedDataSize = 0;

        for (ApiType apiType : dataToDownload) {
            switch (apiType) {
                case GroupApi:
                    GroupModel.getGroupModel().getAllGroups(new ExCallable<ArrayList<GroupModel.Group>>() {
                        @Override
                        public void call(ArrayList<GroupModel.Group> data) {
                            Log.d("TEST_API", "DOWNLOADED groups");
                            setSuccess(exCallable);
                        }

                        @Override
                        public void fail(int idResString) {
                            setFail(exCallable);
                        }
                    });
                    break;
                case TeacherApi:
                    TeacherModel.getTeacherModel().getAllTeachers(new ExCallable<ArrayList<TeacherModel.Teacher>>() {
                        @Override
                        public void call(ArrayList<TeacherModel.Teacher> data) {
                            Log.d("TEST_API", "DOWNLOADED teachers");
                            setSuccess(exCallable);
                        }

                        @Override
                        public void fail(int idResString) {
                            setFail(exCallable);
                        }
                    });
                    break;
                case SubjectApi:
                    SubjectModel.getSubjectModel().getGroupSubjects(UserData.getUserData().user.getGroupId(), new ExCallable<ArrayList<Subject>>() {
                        @Override
                        public void call(ArrayList<Subject> data) {

                            SubjectModel.getSubjectModel().downloadSubjectImages(new ExCallable<Integer>() {
                                @Override
                                public void call(Integer data) {
                                    Log.d("TEST_API", "DOWNLOADED subjects");
                                    setSuccess(exCallable);
                                }

                                @Override
                                public void fail(int idResString) {
                                    setFail(exCallable);
                                }
                            });
                        }

                        @Override
                        public void fail(int idResString) {
                            setFail(exCallable);
                        }
                    });
                    break;
                case UserApi:
                    UserModel.getUserModel().downloadRating(UserData.getUserData().user.getToken(), new ExCallable<Void>() {
                        @Override
                        public void call(Void data) {
                            Log.d("TEST_API", "DOWNLOADED rating");
                            setSuccess(exCallable);
                        }

                        @Override
                        public void fail(int idResString) {
                            setFail(exCallable);
                        }
                    });
                    break;
            }
        }
    }

    /**
     * Обновление прогресса в баре, а также проверка на окончание процесса
     *
     * @param exCallable - колбек
     */
    public void setSuccess(ExCallable<ProgressType> exCallable) {
        if (isFailed) {
            return;
        }

        downloadedDataSize++;
        exCallable.call(ProgressType.UPDATE_PROGRESS);

        Log.d("TEST_API", "setSuccess: " + downloadedDataSize);
        if (downloadedDataSize == dataToDownload.size()) {
            exCallable.call(ProgressType.SET_OK);
        }
    }

    /**
     * Установка ошибки
     *
     * @param exCallable - колбек
     */
    public void setFail(ExCallable<ProgressType> exCallable) {
        isFailed = true;
        exCallable.fail(-1);
    }

    /**
     * Сохранение кеш файла на устройстве
     */
    public void save() {
        try {
            String data = (new Gson()).toJson(serverCache);
            FileManager.getFileManager(App.getApp().context()).writeFile(DATA_FILE_NAME, data, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
