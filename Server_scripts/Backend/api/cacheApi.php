<?php

//Создание файла
function createCacheFile()
{
    global $cacheFile;
    $cache = array();
    $apis = ['groupsApi', 'subjectsApi', 'teachersApi', 'achievementApi'];

    $cache['creationTime'] = time();

    for ($i = 0; $i < count($apis); $i++) {
        $cache[$apis[$i]] = time();
    }

    file_put_contents($cacheFile, json_encode($cache));

    echo "Cache updated";
}

//Обновление файла кеша
function updateCacheFile()
{
    global $cacheFile;

    $api = file_get_contents('php://input');
    $cache = json_decode(file_get_contents($cacheFile));

    if ($cache) {
        $cache->{$api} = time();
        $cache->creationTime = time();
        file_put_contents($cacheFile, json_encode($cache));
        echo "Successfully updated";
    } else {
        echo "Cache file doesn't exists";
    }
}

//Проверка файла кеша
function checkCacheFile()
{
    global $cacheFile;

    $cache = json_decode(file_get_contents($cacheFile));

    if ($_GET['creationTime'] != $cache->creationTime) {
        echo "true";
    } else {
        echo "false";
    }
}

//Получение файла кеша
function getCacheFile()
{
    global $cacheFile;

    echo file_get_contents($cacheFile);
}