<?php

// Возникла сложность с GET запросами, их параметры передаются вместе с адресной строкой,
// потому необходимо удалить все значения после ?, так же необходимо добавлять в конце '?' чтобы функция могла нормально спарсить
$requestUri = explode('/', stristr($_SERVER['REQUEST_URI'] . '?', '?', true));
array_shift($requestUri); // Делается сдвиг потому первый элемент всегда пустой ''

if (array_shift($requestUri) == 'api')
{
    $apiName = array_shift($requestUri);
    if ($apiName == "users")
    {
        include 'UserApi.php';
        $userApi = new UserApi($requestUri);
        $userApi->run();
    }
    else if ($apiName == "groups")
    {
        include 'GroupsApi.php';
        $groupsApi = new GroupsApi($requestUri);
        $groupsApi->run();
    }
}
?>