<?php

// Функция возвращающая преподов на курсе, не ясно до конца есть ли в ней необходимость 
function viewTeachersOnCourse($connect) 
{
    // функционал не создан, реализовуется сложновато и не принято решение о его необходимости 
}


// GET запрос возвращающий расписание учителя URI: .../teachers/id/schedule
function viewTeacherSchedules($connect, $id) 
{
    $query = "  SELECT schedule_list.Day, schedule_list.Half, schedule_list.Pair, disciplines.NameDiscipline, schedule_list.Room, subjecttypes.SubjectType FROM schedule_list
                JOIN disciplines ON disciplines.Code_Discipline = schedule_list.Code_Discp
                JOIN subjecttypes ON subjecttypes.Code_SubjectType = schedule_list.Code_SubjectType
                WHERE (disciplines.Code_Lector = $id AND schedule_list.Code_SubjectType = 1) 
                      OR ((disciplines.Code_Practice = $id OR disciplines.Code_Assistant = $id) AND schedule_list.Code_SubjectType = 2)";

    $result = mysqli_query($connect, $query);
    
   // echo "$query";
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

// GET запрос возвращающий всех учителей URI: .../teachers/all
function getAllTeacher($connect)
{
    $query = "  SELECT teachers.Code_Teacher as teacher_id, teachers.FIO FROM teachers";
    
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

?>