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
    
    $query = "  SELECT registration.NickName, st_groups.NameGroup, disciplines.NameDiscp, rating.status, rating.IDZ FROM rating
		JOIN registration ON registration.Code_User = rating.Code_User
		JOIN st_groups ON st_groups.Code_Group = (SELECT Code_group FROM registration WHERE registration.Code_User = rating.Code_User)
		JOIN disciplines ON disciplines.Code_Discp = rating.Code_Discp ";
    
    if ($group != "ALL")
        $query .= "WHERE st_groups.NameGroup = '$group' AND disciplines.NameDiscp = '$NameDiscp'";
    else
	    $query .= "WHERE disciplines.NameDiscp = '$NameDiscp'";
    
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
