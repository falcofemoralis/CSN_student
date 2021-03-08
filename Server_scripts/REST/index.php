<?php

// Возникла сложность с GET запросами, их параметрі передаются вместе с адресной строкой,
// потому необходимо удалить все значения после ?, так же необходимо добавлять в конце '?' чтобы функция могла нормально спарсить
$requestUri = explode('/', stristr($_SERVER['REQUEST_URI'] . '?', '?', true));
array_shift($requestUri); // Делается сдвиг потому первый элемент всегда пустой ''

$typesData = array('teachers', 'groups');

if ($requestUri[0] == 'admin') {
    include 'templates/admin.php';
}

if (array_shift($requestUri) == 'api') {
    $apiName = array_shift($requestUri);

    switch ($apiName) {
        case "users":
            include 'Users/UsersApi.php';
            $userApi = new UsersApi($requestUri);
            $userApi->run();
            break;
        case "groups":
            include 'Groups/GroupsApi.php';
            $groupsApi = new GroupsApi($requestUri);
            $groupsApi->run();
            break;
        case "teachers":
            include 'Teachers/TeachersApi.php';
            $teachersApi = new TeachersApi($requestUri);
            $teachersApi->run();
            break;
        case "subjects":
            include 'Subjects/SubjectsApi.php';
            $subjectsApi = new SubjectsApi($requestUri);
            $subjectsApi->run();
            break;
        case "admin":
            require_once 'Admin/admin.php';
            convertData();
            //inserInDatabase();
        case "cache":
            if ($_SERVER['REQUEST_METHOD'] == 'POST' && $_POST['password'] == '4fb3a58c295349029ada9a93a3b4eeb28979d40b5078f7c2deecdb88992811f7')
                for ($i = 0; $i < 2; $i++)
                    createJSON($typesData[$i]);
            break;
    }
}