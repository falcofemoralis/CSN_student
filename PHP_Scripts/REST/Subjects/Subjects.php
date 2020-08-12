<?php

//GET запрос на предметы по course URI: .../subjects/course
function getSubjectsByCourse($connect)
{
	$course = $_GET["Course"];

	$date1 = date('Y')."-08-11"; 
	$date2 = date('Y-m-d'); 
	
	if($date1 > $date2)
	    $course += ($course-1);
	else
	    $course *= 2;

   $query = "SELECT disciplines.Code_Discipline, disciplines.NameDiscipline, disciplines.Code_Lector, disciplines.Code_Practice, disciplines.Code_Assistant
            FROM `disciplines`
            where disciplines.Semestr = $course ";
   
   $result = mysqli_query($connect, $query);
   
   if ($result != NULL)
       $number_of_row = mysqli_num_rows($result);
    else
        $number_of_row = 0;
           
    $res_array = array();
           
    if ($number_of_row > 0)
        while ($row = mysqli_fetch_assoc($result))
            $res_array[] = $row;
                   
    echo json_encode($res_array);
                  
    mysqli_close($connect);
}

