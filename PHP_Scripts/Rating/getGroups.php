<?php

if($_SERVER["REQUEST_METHOD"]=="POST"){
    include 'connection.php';
    getRating();
}

function getRating()
{
    global $connect;
    
    $group = $_POST["NameGroup"]; 
    $NameDiscp = $_POST["NameDiscp"]; 
    
    $query = "  SELECT NameGroup FROM `st_groups` ";
    
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
            
    echo json_encode($temp_array);
    mysqli_close($connect);
}

?>
