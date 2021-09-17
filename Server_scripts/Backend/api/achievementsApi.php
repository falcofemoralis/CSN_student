<?php

require_once 'DataBase.php';

function getAchievements() {
    $query = "SELECT *  FROM achievements ORDER BY achievements.name";
    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}