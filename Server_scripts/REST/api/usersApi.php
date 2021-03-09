<?php

require_once 'DataBase.php';

// GET запрос на получение всех данных пользователя (при логине)
function readUser()
{
    $nickName = $_GET["NickName"];
    $password = $_GET["Password"];

    $query = "  SELECT users.Code_User as id, users.NickName, users.Password,  groups.Course, groups.GroupName, groups.Code_Group as group_id FROM users
                JOIN rating ON rating.Code_User = users.Code_User
                JOIN groups ON groups.Code_Group = users.Code_Group
                WHERE users.NickName = '$nickName' AND users.Password = '$password'";

    $data = DataBase::execQuery($query, ReturnValue::GET_OBJECT);
    if ($data == null) {
        echo "ERROR";
        return;
    }

    echo $data;
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

    $query = "  SELECT users.NickName, users.RealName, groups.GroupName FROM `users`
                JOIN groups ON groups.Code_Group = users.Code_Group
                WHERE groups.Course = '$id'";

    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}

// POST запрос
function createUser()
{
    $nickName = $_POST["NickName"];
    $password = $_POST["Password"];
    $codeGroup = $_POST["CodeGroup"];

    // Проверка на целостность данных
    if ($nickName == NULL || $password == NULL || $codeGroup == NULL) {
        echo "ERROR";
        return;
    }

    // Создает нового юзера
    $query = "  INSERT INTO `users`(`NickName`, `Password`, `Code_Group`)
                VALUES ('$nickName','$password', '$codeGroup')";

    // Проверка на дубликат
    try {
        DataBase::execQuery($query, ReturnValue::GET_NOTHING);
    } catch (Exception $e) {
        if ($e->getMessage() == '1062')
            echo 'Duplicate';
        return;
    }

    // Добавляет пустой рейтинг юзера
    $query = "  INSERT INTO rating(Code_User, JSON_RATING)
                    VALUES ((SELECT Code_User FROM users WHERE users.NickName = '$nickName'), '0')";
    DataBase::execQuery($query, ReturnValue::GET_NOTHING);
}

/* PUT запрос обновления данных юзера 
 * JSON данные:
 * NickName - Ник
 * Password - Пароль
 * GroupName - Название группы
 */
function updateUser($url)
{
    $id = explode('/', $url)[3];

    $string = file_get_contents('php://input');
    parse_str($string, $data);

    // Получаем данные
    $nickName = $data['NickName'];
    $password = $data['Password'];
    $oldPassword = $data['OldPassword'];

    // Проверка на то, все ли данные пришли
    if ($nickName == NULL || $password == NULL || $oldPassword == NULL) {
        echo "ERROR";
        return;
    }

    $query = "  UPDATE `users`
                SET `NickName`='$nickName',
                    `Password`='$password'
                    WHERE Code_User = '$id' AND Password = '$oldPassword'";

    $data = DataBase::execQuery($query, ReturnValue::GET_NOTHING);
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

    $data = DataBase::execQuery($query, ReturnValue::GET_NOTHING);
}