<?php

// Возникла сложность с GET запросами, их параметрі передаются вместе с адресной строкой,
// потому необходимо удалить все значения после ?, так же необходимо добавлять в конце '?' чтобы функция могла нормально спарсить
$requestUri = explode('/', stristr($_SERVER['REQUEST_URI'] . '?', '?', true));
array_shift($requestUri); // Делается сдвиг потому первый элемент всегда пустой ''

if (array_shift($requestUri) == 'api')
{
    $apiName = array_shift($requestUri);

    switch ($apiName)
    {
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
    }
}

?>