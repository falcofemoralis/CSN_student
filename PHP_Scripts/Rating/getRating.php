<?php

if($_SERVER["REQUEST_METHOD"]=="POST"){
    include 'connection.php';
    getData();
}

function getData()
{
    global $connect;
    
    $group = $_POST["NameGroup"];
    $discp = $_POST["NameDiscp"];
    
    if ($group != "ALL")
    {
        $query = "  SELECT registration.NickName, st_groups.NameGroup, disciplines.NameDiscp, rating.status
                    FROM rating
                    JOIN registration ON registration.Code_User = rating.Code_User
                    JOIN st_groups ON st_groups.Code_Group = rating.Code_Group
                    JOIN disciplines ON disciplines.Code_Discp = rating.Code_Discp
                    WHERE st_groups.NameGroup = '$group' AND disciplines.NameDiscp = '$discp'";
    }
    else 
    { 
        $query = "  SELECT registration.NickName, st_groups.NameGroup, disciplines.NameDiscp, rating.status
                    FROM rating
                    JOIN registration ON registration.Code_User = rating.Code_User
                    JOIN st_groups ON st_groups.Code_Group = rating.Code_Group
                    JOIN disciplines ON disciplines.Code_Discp = rating.Code_Discp
                    WHERE disciplines.NameDiscp = '$discp'";
    }
    
    $result = mysqli_query($connect, $query);
    
    if ($result != NULL)
        $number_of_row = mysqli_num_rows($result);
    else 
        $number_of_row = 0;
    
    $temp_array = array();
    
    if ($number_of_row > 0)
    {
        while ($row = mysqli_fetch_assoc($result))
            $temp_array[] = $row;
    }
    
    echo json_encode(array("Rating" => $temp_array));
    
    mysqli_close($connect);
}

?>