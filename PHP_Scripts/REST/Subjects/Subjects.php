<?php

//GET запрос на предметы по course URI: .../subjects/course
function getSubjectsByCourse($connect)
{
	$course = $_GET["Course"];
    $semestr;

	$dateFirstFebrary = "02-01"; 
	$dateFirstSeptember = "09-01"; 

	$datecurrent = date('m-d'); 
	
	if($datecurrent < $dateFirstFebrary)
	    $semestr = ($course * 2)-1;
	else
	    $semestr = $course * 2;
	 
	 if($datecurrent > $dateFirstSeptember) 
    	 $semestr -=1;
	    
   $query = "SELECT disciplines.NameDiscipline, disciplines.Code_Lector, disciplines.Code_Practice, disciplines.Code_Assistant
            FROM `disciplines`
            where disciplines.Semestr = $semestr ";
   
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

