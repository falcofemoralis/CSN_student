<?php

//GET запрос на расписания группы по id URI: .../groups/id/schedule
function getScheduleById($id)
{
    
    $query = "   SELECT schedule_list.Day, schedule_list.Pair, schedule_list.Half, disciplines.NameDiscipline, schedule_list.Room,
                subjecttypes.SubjectType
                FROM schedule_list
                JOIN subjecttypes ON subjecttypes.Code_SubjectType = schedule_list.Code_SubjectType
                JOIN disciplines ON disciplines.Code_Discipline = schedule_list.Code_Discp
                WHERE schedule_list.Code_Schedule = (SELECT schedule.Code_Schedule
                FROM schedule
                WHERE schedule.Code_Group = '$id')";
   
    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}

//GET запрос на получение всех групп по курсу id URI: .../groups
function getGroupsOnCourse()
{ 
    $course = $_GET['Course'];
    
    $query = "  SELECT groups.Code_Group as id, groups.GroupName 
                FROM groups
                WHERE groups.Course = $course";
    
    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}

//GET запрос на получение всех групп на кафедре URI: .../groups/all
function getAllGroups() 
{
    $query = "  SELECT groups.Code_Group as id, groups.GroupName 
                FROM groups";
    
    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}

//POST запрос на перезапись всего расписания группы URI: .../groups/id/schedule
function setSchedule($id)
{
    $query = "  SELECT schedule.Code_Schedule
                FROM schedule
                WHERE schedule.Code_Group = '$id'";
    $data = DataBase::execQuery($query, ReturnValue::GET_OBJECT);
    $idSchedule = json_decode($data)->{'Code_Schedule'};

    $query = "  DELETE FROM schedule_list
                WHERE Code_Schedule = '$idSchedule'";
    DataBase::execQuery($query, ReturnValue::GET_NOTHING);

    $schedule = $_POST['schedule'];
    $query = "  INSERT INTO `schedule_list`(`Code_Schedule`, `Day`, `Pair`, `Half`, `Code_Discp`, `Room`, `Code_SubjectType`)
                VALUES ";

    $flag = false;    
    foreach ($schedule as $item)
    {
        $item = json_decode($item);
        $day = $item->{'day'};
        $pair = $item->{'pair'};
        $half = $item->{'half'};
        $codeDiscp = $item->{'codeDiscp'};
        $room = $item->{'room'};
        $subjectType = $item->{'subjectType'};

        if ($half == -1 || $codeDiscp == -1 || $subjectType == -1)
            continue;
            
        if ($flag == true)
            $query .= ',';
        else
            $flag = true;

        $query .= "('$idSchedule', '$day', '$pair', '$half', '$codeDiscp', '$room', '$subjectType')";
    }   

    DataBase::execQuery($query, ReturnValue::GET_NOTHING);
}