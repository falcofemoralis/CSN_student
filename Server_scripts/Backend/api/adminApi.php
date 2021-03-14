<?php

require_once "./scripts/converter.php";

$cacheFile = 'cache.json';


//Конвертирование файла
function convertFile()
{
    $version = explode(".", phpversion())[1];

    if ($version != 1) {
        echo "Change PHP version to 7.1";
        return;
    }
    convertData(file_get_contents($_FILES['file']['tmp_name']));
    echo "Convertation completed successfully";
}

//Загрузка в базу
function processSchedule()
{
    if (inserInDatabase()) {
        resetDatabase();
    } else {
        echo "Successfully inserted";
    }
}

//Очищение базы
function clearSchedule()
{
    resetDatabase();
    echo "Successfully reseted";
}

//Для доступа с реакта
function cors()
{
    // Allow from any origin
    if (isset($_SERVER['HTTP_ORIGIN'])) {
        // Decide if the origin in $_SERVER['HTTP_ORIGIN'] is one
        // you want to allow, and if so:
        header("Access-Control-Allow-Origin: {$_SERVER['HTTP_ORIGIN']}");
        header('Access-Control-Allow-Credentials: true');
        header('Access-Control-Max-Age: 86400');    // cache for 1 day
    }

    // Access-Control headers are received during OPTIONS requests
    if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {

        if (isset($_SERVER['HTTP_ACCESS_CONTROL_REQUEST_METHOD']))
            // may also be using PUT, PATCH, HEAD etc
            header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");

        if (isset($_SERVER['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']))
            header("Access-Control-Allow-Headers: {$_SERVER['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']}");

        exit(0);
    }
}

//Создание файла
function createCacheFile()
{
    global $cacheFile;
    $cache = array();
    $apis = ['groupsApi', 'subjectsApi', 'teachersApi'];

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

    if ($_GET['creationTime'] == $cache->creationTime) {
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