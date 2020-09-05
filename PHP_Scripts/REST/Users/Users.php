<?php

// GET запрос на получение всех данных пользователя (при логине) URI: .../users/login
function readUser($connect)
{
    $nickName = $_GET["NickName"];
    $password = $_GET["Password"];
    
    $query = "  SELECT users.Code_User as id, users.NickName, users.Password,  groups.Course, groups.GroupName, groups.Code_Group as group_id FROM users
                JOIN rating ON rating.Code_User = users.Code_User
                JOIN groups ON groups.Code_Group = users.Code_Group
                WHERE users.NickName = '$nickName' AND users.Password = '$password'";
    
    $result = mysqli_query($connect, $query) or die (mysqli_error($connect));
    $res = mysqli_fetch_assoc($result);
    $data = json_encode($res);
    
    // Если данные не были получены с запроса, значит пользователь неправильно ввел данные при логине
    if ($data == null)
    {
        echo "ERROR";
        return;
    }
    
    echo $data;    
    mysqli_close($connect);
}

//GET запрос на получение рейтинга юзера по id URI: .../users/id/rating
function getUserRating($connect, $id)
{
    $query = "  SELECT rating.JSON_RATING FROM rating
                WHERE rating.Code_User = '$id'";

    $result = mysqli_query($connect, $query) or die (mysqli_error($connect));
    $res = mysqli_fetch_assoc($result);
    $data = json_encode($res);
    echo $data;
    
    mysqli_close($connect);
}

//GET запрос на получение публичных данных юзера по id URI: .../users/id
function userViewById($connect, $id) 
{
    $query = "  SELECT users.NickName,  groups.Course, groups.GroupName FROM users
                JOIN rating ON rating.Code_User = users.Code_User
                JOIN groups ON groups.Code_Group = users.Code_Group
                WHERE users.Code_User = '$id'";
    
    $result = mysqli_query($connect, $query) or die (mysqli_error($connect));
    
    $res = mysqli_fetch_assoc($result);
    $data = json_encode($res);
    echo $data;
    
    mysqli_close($connect);
    
} 

//GET запрос на получение публичных данных юзеров по курсу URI: .../users/course/id
function usersViewByCourse($connect, $id)
{
    $query = "  SELECT users.NickName, users.RealName, groups.GroupName FROM `users`
                JOIN groups ON groups.Code_Group = users.Code_Group
                WHERE groups.Course = '$id'";
    
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

// POST запрос URI: .../users
function createUser($connect)
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
    
    // Проверка есть ли в базе пользователь с таким же никнеймом
    $query = "  SELECT * FROM users WHERE users.NickName = '$nickName'";
    $result = mysqli_query($connect, $query) or die (mysqli_error($connect));
    $data = mysqli_fetch_assoc($result);
    if ($data != null)
    {
        echo "Duplicate";
        return;
    }
    
    // Создает нового юзера
    $query = "  INSERT INTO `users`(`NickName`, `Password`, `Code_Group`)
                VALUES ('$nickName','$password', '$codeGroup')";
    
    mysqli_query($connect, $query) or die (mysqli_error($connect));
    
    // Добавляет пустой рейтинг юзера
    $query = "  INSERT INTO rating(Code_User, JSON_RATING)
                    VALUES ((SELECT Code_User FROM users WHERE users.NickName = '$nickName'), '0')";
    
    mysqli_query($connect, $query) or die (mysqli_error($connect));
    mysqli_close($connect);
}

/* PUT запрос обновления данных юзера URI: .../users/id 
 * JSON данные:
 * NickName - Ник
 * Password - Пароль
 * GroupName - Название группы
 */
function updateUser($connect, $id)
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
     
    mysqli_query($connect, $query) or die (mysqli_error($connect));
    mysqli_close($connect);
}

/* PUT запрос обновления рейтинга URI: .../users/id/rating 
 * JSON данные:
 * $rating - JSON строка информации о рейтинге юзера
 */
function updateUserRating($connect, $id) 
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
    
    mysqli_query($connect, $query) or die (mysqli_error($connect));
    mysqli_close($connect);
}

?>