package com.BSLCommunity.CSN_student.Widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

import com.BSLCommunity.CSN_student.Models.Timer;
import com.BSLCommunity.CSN_student.R;

/**
 * Implementation of App Widget functionality.
 */
public class TimerWidget extends AppWidgetProvider implements Timer.ITimer {
    private static Timer timer;
    private AppWidgetManager appWidgetManager;
    private Context context;
    private String pairText;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Константное обновление не требуется
    }

    @Override
    public void onEnabled(Context context) {
        this.context = context;
        appWidgetManager = AppWidgetManager.getInstance(context);
        timer = new Timer(this);
    }

    @Override
    public void onDisabled(Context context) {
        // Останавливаем таймер и удаляем
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    /**
     * Обновление текста на виджете
     *
     * @param resId - id TextView на виджете
     * @param text  - текст который будет установлен
     */
    public void updateWidgetText(int resId, String text) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.timer_widget);
        ComponentName thisWidget = new ComponentName(context, TimerWidget.class);
        remoteViews.setTextViewText(resId, text);
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    @Override
    public void updateTime(String time) {
        updateWidgetText(R.id.widget_tv_timerCounter, time);
        updateWidgetText(R.id.widget_tv_timer_text, pairText);
    }

    @Override
    public void updatePair(int pair, Timer.TimeType type) {
        String pairRome = Timer.getRomePair(pair);

        if (type == Timer.TimeType.UNTIL_START) {
            pairText = context.getString(R.string.timeStart, pairRome);
        } else {
            pairText = context.getString(R.string.timeUntil, pairRome);
        }
    }
}