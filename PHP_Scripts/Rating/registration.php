<?php

if($_SERVER["REQUEST_METHOD"]=="POST"){
    include 'connection.php';
    insertData();
}

function insertData()
{
    global $connect;
    
    $NickName = $_POST["NickName"];
    $Pass = $_POST["Password"];
    
    $query = "  INSERT INTO registration(NickName, Password)
                VALUES
                ('$NickName', '$Pass')";
    
    mysqli_query($connect, $query) or die (mysqli_error($connect));
    mysqli_close($connect);
}

?>