<?php

//GET запрос на предметы по group URI: .../subjects/course
function getSubjectsByGroup($connect)
{
    $group = $_GET["Code_Group"];

    $query = "SELECT disciplines.NameDiscipline, disciplines.Code_Lector, disciplines.Code_Practice, disciplines.Code_Assistant
        FROM disciplines INNER JOIN (SELECT DISTINCT schedule_list.Code_Discp
        FROM schedule_list
        WHERE schedule_list.Code_Schedule = 
        (SELECT schedule.Code_Schedule from schedule WHERE schedule.Code_Group = $group)
        ORDER BY schedule_list.Code_Discp) AS subjects ON disciplines.Code_Discipline = subjects.Code_Discp";
   
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

