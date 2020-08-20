<?php

//GET запрос на предметы по group URI: .../subjects/course
function getSubjectsByGroup($connect)
{
    $group = $_GET["Code_Group"];

    $query = "SELECT disciplines.NameDiscipline, disciplines.Code_Lector, disciplines.Code_Practice, disciplines.Code_Assistant, disciplines.Image
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

//GET запрос на предметы по group URI: .../subjects?image=...
function getImageSubject() {
    
    $image = $_GET["image"];
    
    // open the file in a binary mode
    $name = 'Subjects/images/' . $image;
    if (file_exists($name))
        $fp = fopen($name, 'rb');
   else
   {
        echo "invalig method";
        return;
   }
   
    // send the right headers
    header("Content-Type: image/png");
    header("Content-Length: " . filesize($name));
    
    // dump the picture and stop the script
    echo fpassthru($fp);
    
}
