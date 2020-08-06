<?php

    $requestUri = explode('/', $_SERVER['REQUEST_URI']);
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
    }
?>