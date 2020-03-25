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
    $IDZ = $_POST["IDZ"];
    $IDZ += 0;
    
    $query = "  INSERT INTO rating (Code_User, Code_Discp, status, IDZ)
                VALUES
                ((SELECT Code_User FROM registration WHERE NickName = '$NickName'), (SELECT Code_Discp FROM disciplines WHERE NameDiscp  = '$NameDiscp'), '$Status', $IDZ)";
    
    mysqli_query($connect, $query) or die (mysqli_error($connect));
    mysqli_close($connect);
}

?>
