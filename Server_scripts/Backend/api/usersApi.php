<?php

require_once 'DataBase.php';

// GET запрос на получение всех данных пользователя (при логине) URI: .../users/login
function login()
{
    $nickName = $_GET["nickname"];
    $password = $_GET["password"];

    readUser($nickName, $password);
}

function readUser($nickName, $password)
{
    $query = "  SELECT users.Code_User, users.NickName, users.Password,  groups.Course, groups.GroupName, groups.Code_Group as group_id FROM users
                JOIN groups ON groups.Code_Group = users.Code_Group
                WHERE users.NickName = '$nickName' AND users.Password = '$password'";

    $data = DataBase::execQuery($query, ReturnValue::GET_OBJECT);
    if ($data == "null") {
        http_response_code(404);
        die();
    }

    $data = (array)json_decode($data);
    $data['token'] = createJWT($data['Code_User']);
    unset($data['Code_User']);
    echo json_encode($data);
}

//GET запрос на получение рейтинга юзера по id
function getUserRating()
{
    $headers = getallheaders();
    $id = checkAuth();

    $query = "  SELECT rating.JSON_RATING FROM rating
                WHERE rating.Code_User = '$id'";

    $data = json_decode(DataBase::execQuery($query, ReturnValue::GET_OBJECT));
    if ($data->JSON_RATING) {
        echo $data->JSON_RATING;
    } else {
        http_response_code(404);
        die();
    }
}

//GET запрос на получение публичных данных юзера по id
function userViewById($url)
{
    $id = explode('/', $url)[3];

    $query = "  SELECT users.NickName,  groups.Course, groups.GroupName FROM users
                JOIN rating ON rating.Code_User = users.Code_User
                JOIN groups ON groups.Code_Group = users.Code_Group
                WHERE users.Code_User = '$id'";

    $data = DataBase::execQuery($query, ReturnValue::GET_OBJECT);
    echo $data;
}

//GET запрос на получение публичных данных юзеров по курсу
function usersViewByCourse($url)
{
    $id = explode('/', $url)[4];

    $query = "  SELECT users.NickName,groups.GroupName FROM `users`
                JOIN groups ON groups.Code_Group = users.Code_Group
                WHERE groups.Course = '$id'";

    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}

// POST запрос
function createUser()
{
    $nickName = $_POST["nickname"];
    $password = $_POST["password"];
    $group = $_POST["group"];

    // Проверка на целостность данных
    if ($nickName == NULL || $password == NULL || $group == NULL) {
        http_response_code(400);
        die();
    }

    // Создает нового юзера
    $query = "  INSERT INTO `users`(`NickName`, `Password`, `Code_Group`) 
                VALUES ('$nickName', '$password', (SELECT Code_Group FROM groups WHERE groups.GroupName = '$group'))";

    // Проверка на дубликат
    try {
        DataBase::execQuery($query, ReturnValue::GET_NOTHING);
    } catch (Exception $e) {
        if ($e->getMessage() == '1062') {
            http_response_code(409);
        } else {
            http_response_code(500);
        }
        die();
    }

    // Добавляет пустой рейтинг юзера
    $query = "  INSERT INTO rating(Code_User, JSON_RATING)
                VALUES ((SELECT Code_User FROM users WHERE users.NickName = '$nickName'), null)";

    try {
        DataBase::execQuery($query, ReturnValue::GET_NOTHING);
        readUser($nickName, $password);
    } catch (Exception $e) {
        http_response_code(500);
        die();
    }
}

/* PUT запрос обновления данных юзера 
 * JSON данные:
 * NickName - Ник
 * Password - Пароль
 * GroupName - Название группы
 */
function updateUser($url)
{
    $id = checkAuth();

    $string = file_get_contents('php://input');
    $data = json_decode($string);

    // Получаем данные
    $nickName = $data->NickName;
    $password = $data->Password;

    // Проверка на то, все ли данные пришли
    if ($nickName == NULL || $password == NULL) {
        echo "ERROR";
        return;
    }

    $query = "  UPDATE `users`
                SET `NickName`='$nickName',
                    `Password`='$password'
                    WHERE Code_User = '$id'";

    try {
        DataBase::execQuery($query, ReturnValue::GET_NOTHING);
    } catch (Exception $e) {
        if ($e->getMessage() == '1062') {
            http_response_code(409);
        }
        die();
    }
}

/* PUT запрос обновления рейтинга
 * JSON данные:
 * $rating - JSON строка информации о рейтинге юзера
 */
function updateUserRating($url)
{
    $id = checkAuth();

    $rating = file_get_contents('php://input');

    // Проверка на то, все ли данные пришли
    if ($rating == NULL) {
        return;
    }

    $query = "  UPDATE rating
                SET rating.JSON_RATING = '$rating'
                WHERE Code_user = '$id'";

    DataBase::execQuery($query, ReturnValue::GET_NOTHING);
}


/** GET запрос на получение всех пользователей
 */
function getAllUsers()
{
    $query = "SELECT users.Code_User, users.NickName, users.Visits, users.LastOpen 
    FROM users 
    ORDER BY LastOpen DESC";

    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}

/** PUT запрос на обновления числа открытий приложения у юзера */
function updateUserOpen()
{
    $id = checkAuth();
    $count = file_get_contents('php://input');

    $get = "SELECT Visits FROM users WHERE users.Code_User = $id";
    $data  = json_decode(DataBase::execQuery($get, ReturnValue::GET_OBJECT));

    $visits = $data->Visits + $count;

    $update = "UPDATE users SET users.Visits=$visits, users.LastOpen=CONVERT_TZ(NOW(),'SYSTEM','+2:00') WHERE users.Code_User = $id";
    DataBase::execQuery($update, ReturnValue::GET_NOTHING);
}

function updateUserActivity()
{
    $id = checkAuth();

    $dataJson = file_get_contents('php://input');
    $data = json_decode($dataJson, true);

    $insert = "INSERT INTO logs (Code_User, LogType, Info, PerformedOn) VALUES ";
    for($i=0; $i<count($data); ++$i){
        $type = $data[$i]["type"];
        $info = $data[$i]["info"];
        $time = $data[$i]["time"];

        if($info == null){
            $info = "null";
        } else{
            $info = "'$info'";
        }
        $insert .= "($id, $type, $info, '$time')";  

        if($i != count($data) - 1){
            $insert .= ",";
        }   
    }

    DataBase::execQuery($insert, ReturnValue::GET_NOTHING);
}

function getUserLogs($url){
   $id = explode('/', $url)[4];

   $get = "SELECT * FROM logs WHERE logs.Code_User = $id ORDER BY logs.PerformedOn DESC";

   $data = DataBase::execQuery($get, ReturnValue::GET_ARRAY);
   echo $data;
}

function updateUserAchievements($url)
{
    $id = checkAuth();

    $achievements = file_get_contents('php://input');

    if ($achievements == NULL) {
        return;
    }

    $query = "SELECT achievements_user.Code_User FROM achievements_user WHERE Code_User = '$id'";
    $users = DataBase::execQuery($query, ReturnValue::GET_OBJECT);

    if($users == "null"){
        $query = "INSERT INTO `achievements_user` (`Code_User`, `JSON_ACH`) VALUES ('$id', NULL)";
        DataBase::execQuery($query, ReturnValue::GET_NOTHING);
    }

    $query = "UPDATE achievements_user
              SET achievements_user.JSON_ACH = '$achievements'
              WHERE Code_User = '$id'";
    DataBase::execQuery($query, ReturnValue::GET_NOTHING);
}

function getUserAchievements()
{
    $id = checkAuth();

    $query = "  SELECT achievements_user.JSON_ACH FROM achievements_user
                WHERE achievements_user.Code_User = '$id'";

    $data = json_decode(DataBase::execQuery($query, ReturnValue::GET_OBJECT));
    if ($data->JSON_ACH) {
        echo $data->JSON_ACH;
    } else {
        http_response_code(404);
        die();
    }
}