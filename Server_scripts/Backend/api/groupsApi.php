<?php

//GET запрос на расписания группы по id
function getScheduleById($url)
{
    $id = explode('/', $url)[3];

    $query = "SELECT DISTINCT schedule_list.Day, schedule_list.Pair, schedule_list.Half, subjects.SubjectName, subjecttypes.SubjectType, schedule_list.Room
    FROM schedule_list 
    INNER JOIN subjects on subjects.Code_Subject = schedule_list.Code_Subject 
    INNER JOIN subjecttypes on subjecttypes.Code_SubjectType = schedule_list.Code_SubjectType 
    INNER JOIN schedule on schedule.Code_Schedule = schedule_list.Code_Schedule
    INNER JOIN teachers on teachers.Code_Teacher = schedule.Code_Teacher
    WHERE schedule_list.Code_Group = $id 
    ORDER BY schedule_list.Day, schedule_list.Pair ASC";

    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}

//GET запрос на получение всех групп по курсу
function getGroupsOnCourse($url)
{
    $course = explode('/', $url)[4];

    $query = "SELECT groups.Code_Group, groups.GroupName 
    FROM groups
    WHERE groups.Course = $course";

    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}

//GET запрос на получение всех групп на кафедре URI: .../groups/all
function getAllGroups()
{
    $query = "SELECT * 
    FROM groups";

    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}