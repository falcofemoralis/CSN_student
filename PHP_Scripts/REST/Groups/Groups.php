<?php

//GET запрос на расписания группы по id URI: .../groups/id/schedule
function getScheduleById($connect, $id)
{
   $query = "   SELECT schedule_List.Day, schedule_List.Pair, schedule_List.Half, disciplines.NameDiscipline, schedule_List.Room, subjectTypes.SubjectType
                FROM schedule_List
                JOIN subjectTypes ON subjectTypes.Code_SubjectType = schedule_List.Code_SubjectType
                JOIN disciplines ON disciplines.Code_Discipline = schedule_List.Code_Discp
                WHERE schedule_List.Code_Schedule = (SELECT schedule.Code_Schedule
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

// Создание JSON фалйа (апдейт листа)
function createJSON($connect)
{
    $query = "SELECT groups.Code_Group as id, groups.GroupName FROM groups";
    
    $result = mysqli_query($connect, $query);
    
    if ($result != NULL)
        $number_of_row = mysqli_num_rows($result);
    else
        $number_of_row = 0;
            
    $response = array();
    $groups = array();
    if ($number_of_row > 0)
        while ($row = mysqli_fetch_assoc($result))
        {
            $id = $row['id'];
            $lastUpdate = date("Y-n-d G:i:s");
                    
            echo $lastUpdate;
                    
            $groups[] = array('id'=>$id, 'lastUpdate'=>$lastUpdate);
        }
            
            
    $response['groups'] = $groups;
            
    $fp = fopen('results.json', 'w');
    fwrite($fp, json_encode($response));
    fclose($fp);      
}

// GET запрос на получение апдейт листа групп URI: .../groups/updateList
function getUpdateList()
{
    $fp = fopen('results.json', 'r');
    $json = fread($fp, filesize('results.json'));
    
    $data = json_decode($json);

    echo json_encode($data);
}

