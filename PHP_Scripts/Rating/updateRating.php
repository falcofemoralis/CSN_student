<?php

if($_SERVER["REQUEST_METHOD"]=="POST"){
    include 'connection.php';
    updateUser();
}

function updateUser()
{
    global $connect;
    
    $Status = $_POST["Status"];
    $NickName = $_POST["NickName"];
    $NameDiscp = $_POST["NameDiscp"];
    $IDZ = $_POST["IDZ"];
    $IDZ += 0;
    
    $query = "  UPDATE rating
                SET status = '$Status', IDZ = $IDZ
                WHERE Code_User = (SELECT Code_User FROM registration WHERE NickName = '$NickName') AND  Code_Discp = (SELECT Code_Discp FROM disciplines WHERE NameDiscp  = '$NameDiscp')";
    
    mysqli_query($connect, $query) or die (mysqli_error($connect));
    mysqli_close($connect);
}

?>
