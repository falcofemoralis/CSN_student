<?php

require_once 'DataBase.php';

// GET запрос на получение всех данных пользователя (при логине) URI: .../users/login
function readUser()
{ 
    $nickName = $_GET["NickName"];
    $password = $_GET["Password"];
    
    $query = "  SELECT users.Code_User as id, users.NickName, users.Password,  groups.Course, groups.GroupName, groups.Code_Group as group_id FROM users
                JOIN rating ON rating.Code_User = users.Code_User
                JOIN groups ON groups.Code_Group = users.Code_Group
                WHERE users.NickName = '$nickName' AND users.Password = '$password'";
    
    $data = DataBase::execQuery($query, ReturnValue::GET_OBJECT);
    if ($data == null)
    {
        echo "ERROR";
        return;
    }

    echo $data;    
}

//GET запрос на получение рейтинга юзера по id URI: .../users/id/rating
function getUserRating($id)
{
    $query = "  SELECT rating.JSON_RATING FROM rating
                WHERE rating.Code_User = '$id'";

    $data = DataBase::execQuery($query, ReturnValue::GET_OBJECT);
    echo $data;
}

//GET запрос на получение публичных данных юзера по id URI: .../users/id
function userViewById($id) 
{
    $query = "  SELECT users.NickName,  groups.Course, groups.GroupName FROM users
                JOIN rating ON rating.Code_User = users.Code_User
                JOIN groups ON groups.Code_Group = users.Code_Group
                WHERE users.Code_User = '$id'";
    
    $data = DataBase::execQuery($query, ReturnValue::GET_OBJECT);
    echo $data;  
} 

//GET запрос на получение публичных данных юзеров по курсу URI: .../users/course/id
function usersViewByCourse($id)
{
    $query = "  SELECT users.NickName, users.RealName, groups.GroupName FROM `users`
                JOIN groups ON groups.Code_Group = users.Code_Group
                WHERE groups.Course = '$id'";
    
    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}

// POST запрос URI: .../users
function createUser()
{      
    $nickName = $_POST["NickName"];
    $password = $_POST["Password"];
    $codeGroup = $_POST["CodeGroup"];
    
    // Проверка на целостность данных
    if ($nickName == NULL || $password == NULL || $codeGroup == NULL)
    {
        echo "ERROR";
        return;
    }
    
    // Создает нового юзера
    $query = "  INSERT INTO `users`(`NickName`, `Password`, `Code_Group`)
                VALUES ('$nickName','$password', '$codeGroup')";

    // Проверка на дубликат
    try {
        DataBase::execQuery($query, ReturnValue::GET_NOTHING);
    }
    catch (Exception $e) {
        if ($e->getMessage() == '1062')
            echo 'Duplicate';
        return;
    }

    // Добавляет пустой рейтинг юзера
    $query = "  INSERT INTO rating(Code_User, JSON_RATING)
                    VALUES ((SELECT Code_User FROM users WHERE users.NickName = '$nickName'), '0')";    
    DataBase::execQuery($query, ReturnValue::GET_NOTHING);
}

/* PUT запрос обновления данных юзера URI: .../users/id 
 * JSON данные:
 * NickName - Ник
 * Password - Пароль
 * GroupName - Название группы
 */
function updateUser($id)
{
    $string = file_get_contents('php://input');
    parse_str($string, $data);

    // Получаем данные
    $nickName = $data['NickName'];
    $password = $data['Password'];  
    $oldPassword = $data['OldPassword'];
    
    // Проверка на то, все ли данные пришли
    if ($nickName == NULL || $password == NULL || $oldPassword == NULL)
    {
        echo "ERROR";
        return;
    }
         
    $query = "  UPDATE `users`
                SET `NickName`='$nickName',
                    `Password`='$password'
                    WHERE Code_User = '$id' AND Password = '$oldPassword'";
     
    $data = DataBase::execQuery($query, ReturnValue::GET_NOTHING);
}

/* PUT запрос обновления рейтинга URI: .../users/id/rating 
 * JSON данные:
 * $rating - JSON строка информации о рейтинге юзера
 */
function updateUserRating($id) 
{
    $rating = file_get_contents('php://input');

    // Проверка на то, все ли данные пришли
    if ($rating == NULL )
    {
        echo "ERROR";
        return;
    }
    
    $query = "  UPDATE rating
                SET rating.JSON_RATING = '$rating'
                WHERE Code_user = '$id'";

    $data = DataBase::execQuery($query, ReturnValue::GET_NOTHING);
}

?>