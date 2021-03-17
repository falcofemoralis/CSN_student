package com.BSLCommunity.CSN_student.Managers;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Pair;

import com.BSLCommunity.CSN_student.R;

import java.util.ArrayList;
import java.util.Locale;

public class LocaleHelper {
    public static final String DATA_FILE_NAME = "Lang";

    /**
     * Изменение языка в контексте
     *
     * @param context - контекст
     * @return - новый контекст с установленным языком
     */
    public static Context onAttach(Context context) {
        return setLocale(context, getLanguage(context));
    }

    /**
     * Установка языка
     *
     * @param context  - контекст
     * @param language - названия языка
     * @return - новый контекст
     */
    public static Context setLocale(Context context, String language) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }
        return updateResourcesLegacy(context, language);
    }

    /**
     * Получение языка
     *
     * @param context - контекст
     * @return - название языка
     */
    public static String getLanguage(Context context) {
        //стандартный язык устройства. Если в настройках не стоит данный язык, будет взят как стандартный
        String defaultLanguage = Locale.getDefault().getLanguage();

        // Проверяем поддерживает ли наше приложение язык устройства. Если нет, то ставим английский по дефолту
        if (!defaultLanguage.equals("en") && !defaultLanguage.equals("ru") && !defaultLanguage.equals("uk"))
            defaultLanguage = "en";
        try {
            return FileManager.getFileManager(context).readFile(DATA_FILE_NAME);
        } catch (Exception e) {
            return defaultLanguage;
        }
    }

    /**
     * Обновление языка в контексте.  Используется в api 24 (N) и выше
     *
     * @param context  - котнекст
     * @param language - язык
     * @return - новый контекст
     */
    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        return context.createConfigurationContext(configuration);
    }

    /**
     * Устаревший способ обновления языка. Используется в api 23 (M) и ниже
     *
     * @param context  - котнекст
     * @param language - язык
     * @return - новый контекст
     */
    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        configuration.setLayoutDirection(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }

    /**
     * Получения языков. Ключ - его строковое имя. Значения - название. [русский, ru]
     *
     * @param context - контекст
     * @return - название языка
     */
    public static ArrayList<Pair<String, String>> getLanguages(Context context) {
        ArrayList<Pair<String, String>> languages = new ArrayList<>();

        String[] languagesArray = context.getResources().getStringArray(R.array.languages);
        languages.add(new Pair<>(languagesArray[0], "en"));
        languages.add(new Pair<>(languagesArray[1], "ru"));
        languages.add(new Pair<>(languagesArray[2], "uk"));

        return languages;
    }

    /**
     * Изменение и сохранение языка в приложении
     * @param context - контекст
     * @param lang - новый язык
     * @throws Exception - в случе если не удалось сохранить
     */
    public static void changeLanguage(Context context, String lang) throws Exception {
        FileManager.getFileManager(context).writeFile(DATA_FILE_NAME, lang, false);
      //  setLocale(context, lang);
    }
}