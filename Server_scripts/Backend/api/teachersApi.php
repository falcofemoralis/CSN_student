<?php

// GET запрос возвращающий расписание учителя
function viewTeacherSchedules($url)
{
    $id = explode('/', $url)[3];

    $query = "SELECT schedule_list.Day, schedule_list.Half, schedule_list.Pair, subjects.SubjectName, schedule_list.Room, subjecttypes.SubjectType
    FROM schedule_list
    INNER JOIN subjects ON subjects.Code_Subject = schedule_list.Code_Subject
    INNER JOIN subjecttypes ON subjecttypes.Code_SubjectType = schedule_list.Code_SubjectType
    WHERE schedule_list.Code_Schedule = (SELECT schedule.Code_Schedule FROM schedule WHERE schedule.Code_Teacher = $id)";

    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}

// GET запрос возвращающий всех учителей
function getAllTeacher()
{
    $query = "SELECT teachers.Code_Teacher as id, teachers.FIO 
    FROM teachers";

    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}