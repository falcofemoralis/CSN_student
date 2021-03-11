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
                JOIN rating ON rating.Code_User = users.Code_User
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
function getUserRating($url)
{
    $id = explode('/', $url)[3];

    $query = "  SELECT rating.JSON_RATING FROM rating
                WHERE rating.Code_User = '$id'";

    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
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
function createUser($url)
{
    // $arr = getallheaders();
    // $token = $arr['token'];

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
                VALUES ((SELECT Code_User FROM users WHERE users.NickName = '$nickName'), '0')";

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
    $arr = getallheaders();
    $token = $arr['token'];
    $id = checkAuth($token);

    $data = json_decode(file_get_contents('php://input'));
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

    DataBase::execQuery($query, ReturnValue::GET_NOTHING);
}

/* PUT запрос обновления рейтинга
 * JSON данные:
 * $rating - JSON строка информации о рейтинге юзера
 */
function updateUserRating($url)
{
    $id = explode('/', $url)[3];

    $rating = file_get_contents('php://input');

    // Проверка на то, все ли данные пришли
    if ($rating == NULL) {
        echo "ERROR";
        return;
    }

    $query = "  UPDATE rating
                SET rating.JSON_RATING = '$rating'
                WHERE Code_user = '$id'";

<<<<<<< HEAD
    $data = DataBase::execQuery($query, ReturnValue::GET_NOTHING);
}
=======
    DataBase::execQuery($query, ReturnValue::GET_NOTHING);
}


/** GET запрос на получение всех пользователей
 */
function getAllUsers()
{
    $query = "SELECT users.NickName, users.Visits 
    FROM users 
    ORDER BY Visits DESC";

    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}
>>>>>>> origin/dev
