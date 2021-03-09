<?php

// GET запрос возвращающий расписание учителя
function viewTeacherSchedules($url)
{
    $id = explode('/', $url)[3];

    $query = "  SELECT schedule_list.Day, schedule_list.Half, schedule_list.Pair, disciplines.NameDiscipline, schedule_list.Room, subjecttypes.SubjectType FROM schedule_list
                JOIN disciplines ON disciplines.Code_Discipline = schedule_list.Code_Discp
                JOIN subjecttypes ON subjecttypes.Code_SubjectType = schedule_list.Code_SubjectType
                WHERE (disciplines.Code_Lector = $id AND schedule_list.Code_SubjectType = 1) 
                      OR ((disciplines.Code_Practice = $id OR disciplines.Code_Assistant = $id) AND schedule_list.Code_SubjectType = 2)";

    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}

// GET запрос возвращающий всех учителей
function getAllTeacher()
{
    $query = "  SELECT teachers.Code_Teacher as id, teachers.FIO FROM teachers";
    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}