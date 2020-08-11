<?php

//GET запрос на расписания группы по id URI: .../groups/id/schedule
function getScheduleById($connect, $id)
{
   $query = "   SELECT Schedule_List.Day, Schedule_List.Pair, Schedule_List.Half, Disciplines.NameDiscipline, Schedule_List.Room, SubjectTypes.SubjectType
                FROM Schedule_List
                JOIN SubjectTypes ON SubjectTypes.Code_SubjectType = Schedule_List.Code_SubjectType
                JOIN Disciplines ON Disciplines.Code_Discipline = Schedule_List.Code_Discp
                WHERE Schedule_List.Code_Schedule = (SELECT Schedule.Code_Schedule
                                                     FROM Schedule
                                                     WHERE Schedule.Code_Group = '$id')";
   
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

//GET запрос на получение всех групп по курсу id URI: .../groups
function getGroupsOnCourse($connect)
{
    $course = $_GET['Course'];
    
    $query = "  SELECT groups.Code_Group as id, groups.GroupName 
                FROM groups
                WHERE groups.Course = '$course'";
    
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