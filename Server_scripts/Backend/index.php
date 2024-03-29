<?php

require_once 'DataBase.php';
require_once "./api/adminApi.php";
require_once "./api/cacheApi.php";
require_once "./api/groupsApi.php";
require_once "./api/subjectsApi.php";
require_once "./api/teachersApi.php";
require_once "./api/usersApi.php";
require_once "./scripts/token.php";
require_once "./api/achievementsApi.php";

cors();

DataBase::getConnection();

if (!function_exists('getallheaders')) {
    function getallheaders()
    {
        $headers = [];
        foreach ($_SERVER as $name => $value) {
            if (substr($name, 0, 5) == 'HTTP_') {
                $headers[str_replace(' ', '-', ucwords(strtolower(str_replace('_', ' ', substr($name, 5)))))] = $value;
            }
        }
        return $headers;
    }
}

$requestUri = explode('/', stristr($_SERVER['REQUEST_URI'] . '?', '?', true));
array_shift($requestUri); //т.к 1 элемент пустой, поэтому сдигаем

/**
 * @property router - маршрутизация - массив в виде <тип запроса> - <массив(ключ-значение)>, где
 * ключ - регулярное выражение маршрута
 * значение - массив функций которые необходимо выполнить последовательно
 */
$router = array();
$router['GET'] = [
    '/\/api\/groups\/course\/(\d+)/' => ['getGroupsOnCourse'],
    '/\/api\/groups\/all/' => ['getAllGroups'],
    '/\/api\/groups\/names/' => ['getGroupNames'],
    '/\/api\/teachers\/all/' => ['getAllTeacher'],
    '/\/api\/subjects\/group\/(\d+)/' => ['getSubjectsByGroup'],
    '/\/api\/subjects\/shortAll/' => ['getShortAllSubjects'],
    '/\/api\/subjects/' => ['getImageSubject'],
    '/\/api\/users\/login/' => ['login'],
    '/\/api\/users\/all/' => ['getAllUsers'],
    '/\/api\/users\/course\/(\d+)/' => ['usersViewByCourse'],
    '/\/api\/users\/logs\/(\d+)/' => ['getUserLogs'],
    '/\/api\/users\/rating/' => ['getUserRating'],
    '/\/api\/users\/achievements/' => ['getUserAchievements'],
    '/\/api\/users\/(\d+)/' => ['userViewById'],
    '/\/api\/cache\/check/' => ['checkCacheFile'],
    '/\/api\/cache\/download/' => ['getCacheFile'],
    '/\/api\/achievements\/all/' => ['getAchievements'],
    '/\/ne_lez_ona_tebya_sozhret/' => ['getSecretPanel']
];
$router['POST'] = [
    '/\/schedule\/upload/' => ['convertFile'],
    '/\/api\/users/' => ['createUser'],
    '/\/api\/cache\/create/' => ['createCacheFile']
];
$router['PUT'] = [
    '/\/api\/users\/rating/' => ['updateUserRating'],
    '/\/api\/users\/achievements/' => ['updateUserAchievements'],
    '/\/schedule\/new/' => ['processSchedule'],
    '/\/api\/cache\/recreate/' => ['updateCacheFile'],
    '/\/api\/users\/opens/' => ['updateUserOpen'],
    '/\/api\/users\/activity/' => ['updateUserActivity'],
    '/\/api\/users/' => ['updateUser']
];
$router['DELETE'] = [
    '/\/schedule\/reset/' => ['clearSchedule']
];

getRouter("/" . implode('/', $requestUri));

/**
 * Получение роутера по соответствующему запросу
 * @param url - юрл указанный пользователем
 */
function getRouter($url)
{
    global $router;
    $keys = array_keys($router[$_SERVER['REQUEST_METHOD']]);

    for ($i = 0; $i < count($keys); ++$i) {
        if (preg_match($keys[$i], $url)) {
            $funcs = $router[$_SERVER['REQUEST_METHOD']][$keys[$i]];
            for ($j = 0; $j < count($funcs); ++$j)
                $funcs[$j]($url);

            break;
        }
    }
}
