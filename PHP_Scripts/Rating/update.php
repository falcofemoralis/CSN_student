<?php

if ($_SERVER["REQUEST_METHOD"]=="POST")
{
    require 'connection.php';
    UpdateRating();
}

function UpdateRating()
{
    global $connect;
    
    $NickName = $_POST["NickName"];
    $Code = $_POST["Code_discp"];
    $Status = $_POST["Status"];
    
    $query = "UPDATE `ratings` SET `Status`='$Status' WHERE `NickName` = '$NickName' AND `Code_discp` ='$Code'";

    mysqli_query($connect, $query) or die (mysqli_error($connect));
    mysqli_close($connect);
}

?>
