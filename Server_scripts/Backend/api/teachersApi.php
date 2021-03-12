<?php

class ScheduleList
{
    public $Day;
    public $Half;
    public $Pair;
    public $SubjectName;
    public $Room;
    public $SubjectType;
    public $Groups;

    function __construct($Day, $Half, $Pair, $SubjectName, $Room, $SubjectType, $GroupName)
    {
        $this->Day = $Day;
        $this->Half = $Half;
        $this->Pair = $Pair;
        $this->SubjectName = $SubjectName;
        $this->Room = $Room;
        $this->SubjectType = $SubjectType;
        $this->Groups = array();
        array_push($this->Groups, $GroupName);
    }

    public function addGroup($GroupName)
    {
        array_push($this->Groups, $GroupName);
    }

    public function compareData($oldData)
    {
        if (
            $oldData->Day === $this->Day &&
            $oldData->Half === $this->Half &&
            $oldData->Pair === $this->Pair &&
            $oldData->SubjectName === $this->SubjectName &&
            $oldData->Room === $this->Room &&
            $oldData->SubjectType === $this->SubjectType
        ) {
            return true;
        } else {
            return false;
        }
    }
}

// GET запрос возвращающий расписание учителя
function viewTeacherSchedules($url)
{
    $id = explode('/', $url)[3];

    $query = "SELECT schedule_list.Day, schedule_list.Half, schedule_list.Pair, subjects.SubjectName, schedule_list.Room, subjecttypes.SubjectType, groups.GroupName
    FROM schedule_list
    INNER JOIN subjects ON subjects.Code_Subject = schedule_list.Code_Subject
    INNER JOIN subjecttypes ON subjecttypes.Code_SubjectType = schedule_list.Code_SubjectType
    INNER JOIN groups on groups.Code_Group = schedule_list.Code_Group
    WHERE schedule_list.Code_Schedule = (SELECT schedule.Code_Schedule FROM schedule WHERE schedule.Code_Teacher = $id)";

    $data = json_decode(DataBase::execQuery($query, ReturnValue::GET_ARRAY));

    $scheduleLists = array();
    $added = false;
    for ($i = 0; $i < count($data); ++$i) {
        $dataList = $data[$i];

        for ($j = 0; $j < count($scheduleLists); ++$j) {
            if ($scheduleLists[$j]->compareData($dataList)) {
                $scheduleLists[$j]->addGroup($data[$i]->GroupName);
                $added = true;
                break;
            }
        }

        if (!$added) {
            array_push($scheduleLists, new ScheduleList(
                $dataList->Day,
                $dataList->Half,
                $dataList->Pair,
                $dataList->SubjectName,
                $dataList->Room,
                $dataList->SubjectType,
                $dataList->GroupName
            ));
        } else {
            $added = false;
        }
    }

    echo json_encode($scheduleLists);
}

// GET запрос возвращающий всех учителей
function getAllTeacher()
{
    $query = "SELECT teachers.Code_Teacher, teachers.FIO 
    FROM teachers";

    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}