package com.BSLCommunity.CSN_student.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.BSLCommunity.CSN_student.Models.AnotherUserList;
import com.BSLCommunity.CSN_student.Models.GroupModel;
import com.BSLCommunity.CSN_student.Models.LocalData;
import com.BSLCommunity.CSN_student.Models.TeacherModel;
import com.BSLCommunity.CSN_student.R;

import java.io.File;
import java.util.concurrent.Callable;

public class DownloadService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //запуск сервис в фоне в зависимости от версии Т.е. мы создаем уведомление и назначаем ему ID. Сервис переходит в режим неуязвимости, а в статус-баре появится уведомление.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) startMyOwnForeground();
        else startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.BSLCommunity.Download";
        String channelName = "Download Service";

        //создаем канал для уведомлений
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        //создаем уведомление (то что в верхней части экрана)
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSmallIcon(R.drawable.app_icon)
                .build();
        startForeground(2, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        final Thread downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                initAllData(); //скачиваем все данные
            }
        });
        downloadThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // Инициализация всех данных
    public void initAllData() {
        // Скачиваем все необходимые апдейт листы для проверки актуальности данных и проверяем данные
//        LocalData.downloadUpdateList(getApplicationContext(), LocalData.updateListGroups, LocalData.TypeData.groups, new Callable<Void>() {
//            @Override
//            public Void call() throws Exception {
//                Log.d("DownloadService", "Download groups");
//                GroupModel.init(getApplicationContext(), User.getInstance().course, new Callable<Void>() {
//                    @Override
//                    public Void call() throws Exception {
//                        Log.d("DownloadService", "Groups downloaded");
//
//                       // LocalData.checkUpdate(getApplicationContext(), LocalData.TypeData.groups);
//                        isDownloadedGroups = true;
//                        stopService("groups");
//                        return null;
//                    }
//                });
//                return null;
//            }
//        });

        LocalData.downloadUpdateList(getApplicationContext(), LocalData.updateListTeachers, LocalData.TypeData.teachers, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                return null;
            }
        });

//        SubjectModel.init(getApplicationContext(), new Callable<Void>() {
//            @Override
//            public Void call() throws Exception {
//                stopService("subjects");
//                return null;
//            }
//        });
        AnotherUserList.getUsersFromServer(this);
    }

    //останавлиавем сервис, когда все данные скачаются
    public void stopService(String id) {
        Log.d("DownloadService", id + " tryToStop");

        File fileGr = getApplicationContext().getFileStreamPath(GroupModel.DATA_FILE_NAME);
        File fileTeach = getApplicationContext().getFileStreamPath(TeacherModel.DATA_FILE_NAME);
       // File fileSubj = getApplicationContext().getFileStreamPath(SubjectModel.DATA_FILE_NAME);

//        if (fileGr.exists() && fileTeach.exists() && fileSubj.exists()) {
//            Log.d("DownloadService", "serviceStopped");
//            stopSelf();
//        }
    }
}
