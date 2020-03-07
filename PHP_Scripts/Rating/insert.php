<?php

if ($_SERVER["REQUEST_METHOD"]=="POST")
{
    require 'connection.php';
    createStudent();
}

function createStudent()
{
    global $connect;
    
    $NickName = $_POST["NickName"];
    $Code = $_POST["Code_discp"];
    $Status = $_POST["Status"];
    
    $query = "INSERT INTO `ratings` (`NickName`, `Code_discp`, `Status`) VALUES ('$NickName', '$Code', '$Status');";
    
    mysqli_query($connect, $query) or die (mysqli_error($connect));
    mysqli_close($connect);
}

?>