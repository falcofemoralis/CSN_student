<?php
    include 'DataBase.php';
    include 'Api.php';
    include 'Users.php';
    
    $db = new DataBase();
    $api = new Api();
    
    $connect = $db->getConnection();
    
    $user = updateUser($connect, 2);
?>