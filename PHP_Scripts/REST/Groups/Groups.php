<?php

//GET запрос на расписания группы по id URI: .../groups/id/schedule
function getScheduleById($connect, $id)
{
    
   $query = "   SELECT schedule_list.Day, schedule_list.Pair, schedule_list.Half, disciplines.NameDiscipline, schedule_list.Room,
                subjecttypes.SubjectType
                FROM schedule_list
                JOIN subjecttypes ON subjecttypes.Code_SubjectType = schedule_list.Code_SubjectType
                JOIN disciplines ON disciplines.Code_Discipline = schedule_list.Code_Discp
                WHERE schedule_list.Code_Schedule = (SELECT schedule.Code_Schedule
                FROM schedule
                WHERE schedule.Code_Group = '$id')";
   
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
                WHERE groups.Course = $course";
    
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