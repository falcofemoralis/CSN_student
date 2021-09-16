<?php

require_once 'DataBase.php';

function getAchievements() {
    $query = "SELECT *  FROM achievements";
    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}