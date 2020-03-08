<?php

if($_SERVER["REQUEST_METHOD"]=="POST"){
    include 'connection.php';
    insertData();
}

function insertData()
{
    global $connect;
    
    $NewNickName = $_POST["NewNickName"];
    $Pass = $_POST["Password"];
    $NameGroup = $_POST["NameGroup"];
    $OldNickName = $_POST["OldNickName"];
    
    
    $query = "  UPDATE registration
                SET NickName = '$NewNickName', Password = '$Pass', Code_Group = (SELECT Code_Group FROM st_groups WHERE NameGroup = '$NameGroup')
                WHERE NickName = '$OldNickName'";
    
    mysqli_query($connect, $query) or die (mysqli_error($connect));
    mysqli_close($connect);
}

?>