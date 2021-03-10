<?php

//GET запрос на предметы по group
function getSubjectsByGroup($url)
{
    $id = explode('/', $url)[4];

    $query = "SELECT DISTINCT subjects.SubjectName, teachers.FIO, subjects.Image
    FROM schedule_list 
    INNER JOIN subjects on subjects.Code_Subject = schedule_list.Code_Subject 
    INNER JOIN schedule on schedule.Code_Schedule = schedule_list.Code_Schedule
    INNER JOIN teachers on teachers.Code_Teacher = schedule.Code_Teacher
    WHERE schedule_list.Code_Group = $id 
    ORDER BY subjects.SubjectName DESC";

    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}

//GET запрос на предметы по group URI: .../subjects?image=...
function getImageSubject()
{
    $image = $_GET["image"];

    // open the file in a binary mode
    $name = 'images/subjects/' . $image;
    if (file_exists($name))
        $fp = fopen($name, 'rb');
    else {
        echo "invalig method";
        return;
    }

    // send the right headers
    header("Content-Type: image/png");
    header("Content-Length: " . filesize($name));

    // dump the picture and stop the script
    echo fpassthru($fp);
}

//GET запрос на все предметы
function getShortAllSubjects()
{
    $query = " SELECT subjects.Code_Subject as id, subjects.SubjectName
    FROM subjects";

    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}