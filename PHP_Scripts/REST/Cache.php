<?php

// Создание JSON фалйа (апдейт листа)
function createJSON($connect, $entity)
{
    
    if ($entity == 'groups')
        $query = "SELECT groups.Code_Group as id FROM groups";
    else 
        $query = "SELECT teachers.Code_Teacher as id FROM teachers";
        
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
                   
    $response[$entity] = $groups;
            
    $fp = fopen($entity . '.json', 'w');
    fwrite($fp, json_encode($response));
    fclose($fp);
}


// GET запрос на получение апдейт листа групп URI: .../entity/updateList
function getUpdateList($enity)
{
    $fp = fopen($enity . '.json', 'r');
    $json = fread($fp, filesize($enity . '.json'));
    
    $data = json_decode($json);
    
    echo json_encode($data);
}


