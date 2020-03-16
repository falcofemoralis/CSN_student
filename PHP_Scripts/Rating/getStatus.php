<?php

if($_SERVER["REQUEST_METHOD"]=="POST"){
    include 'connection.php';
    getStatus();
}

function getStatus()
{
    global $connect;
    
    $NickName = $_POST["NickName"]; 
    $NameDiscp = $_POST["NameDiscp"]; 
    
    $query = " 	SELECT rating.status FROM rating 
		JOIN registration ON registration.Code_User = rating.Code_User 
		WHERE registration.NickName = '$NickName' AND (SELECT disciplines.NameDiscp FROM disciplines WHERE disciplines.Code_Discp = rating.Code_Discp) = '$NameDiscp'";
        
    $result = mysqli_query($connect, $query);
    
    $res = mysqli_fetch_assoc($result);
            
    echo json_encode($res);        
    mysqli_close($connect);
}

?>