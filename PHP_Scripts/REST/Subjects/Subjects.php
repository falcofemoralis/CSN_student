<?php

//GET запрос на предметы по group URI: .../subjects/group
function getSubjectsByGroup()
{
    $group = $_GET["Code_Group"];

    $query = "SELECT Code_Discipline as id, disciplines.NameDiscipline, disciplines.Code_Lector, disciplines.Code_Practice, disciplines.Code_Assistant, disciplines.Image
        FROM disciplines INNER JOIN (SELECT DISTINCT schedule_list.Code_Discp
        FROM schedule_list
        WHERE schedule_list.Code_Schedule = 
        (SELECT schedule.Code_Schedule from schedule WHERE schedule.Code_Group = $group)
        ORDER BY schedule_list.Code_Discp) AS subjects ON disciplines.Code_Discipline = subjects.Code_Discp";
   
    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
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

//GET запрос на все предметы  URI:.../subjects/shortAll
function getShortAllSubjects()
{
    $query = "  SELECT Code_Discipline as id, disciplines.NameDiscipline
                FROM disciplines";
   
    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}