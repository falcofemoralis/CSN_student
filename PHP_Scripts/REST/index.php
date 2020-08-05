<?php
    include 'DataBase.php';
    include 'Api.php';
    
    $db = new DataBase();
    $api = new Api();
    
    $connect = $db->getConnection();
    
    $api->run();
    
    array_shift($api->requestUri);
    echo $api->requestUri;
?>