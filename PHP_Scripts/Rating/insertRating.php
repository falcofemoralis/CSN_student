<?php

if($_SERVER["REQUEST_METHOD"]=="POST"){
    include 'connection.php';
    insertRating();
}

function insertRating()
{
    global $connect;
    
    $NickName = $_POST["NickName"];
    $NameDiscp = $_POST["NameDiscp"];
    $Status = $_POST["Status"];
    
    $query = "  INSERT INTO rating (Code_User, Code_Discp, status)
                VALUES
                ((SELECT Code_User FROM registration WHERE NickName = '$NickName'), (SELECT Code_Discp FROM disciplines WHERE NameDiscp  = '$NameDiscp'), '$Status')";
    
    mysqli_query($connect, $query) or die (mysqli_error($connect));
    mysqli_close($connect);
}

?>