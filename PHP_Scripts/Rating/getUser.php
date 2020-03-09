<?php

if($_SERVER["REQUEST_METHOD"]=="POST"){
    include 'connection.php';
    getUser();
}

function getUser()
{
    global $connect;
    
    $NickName = $_POST["NickName"];

    $query = "  SELECT registration.NickName, registration.Password, st_groups.NameGroup FROM registration 
                JOIN st_groups ON st_groups.Code_Group = registration.Code_Group 
                WHERE NickName = '$NickName'";

    
    $result = mysqli_query($connect, $query);
    
    $res = mysqli_fetch_assoc($result);
            
    echo json_encode($res);        
    mysqli_close($connect);
}
?>