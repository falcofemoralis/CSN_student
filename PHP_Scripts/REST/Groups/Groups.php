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
   
    $data = DataBase::execQuery($query, true);
    echo $data;
}

//GET запрос на получение всех групп по курсу id URI: .../groups
function getGroupsOnCourse($connect)
{ 
    $course = $_GET['Course'];
    
    $query = "  SELECT groups.Code_Group as id, groups.GroupName 
                FROM groups
                WHERE groups.Course = $course";
    
    $data = DataBase::execQuery($query, true);
    echo $data;
}

