<?php

// GET запрос на получение всех данных пользователя (при логине) URI: .../users/login
function readUser($connect)
{
    $nickName = $_GET["NickName"];
    $password = $_GET["Password"];
    
    $query = "  SELECT users.Code_User as id, users.NickName, users.Password,  groups.Course, groups.GroupName FROM users
                JOIN rating ON rating.Code_User = users.Code_User
                JOIN groups ON groups.Code_Group = users.Code_Group
                WHERE users.NickName = '$nickName' AND users.Password = '$password'";
    
    $result = mysqli_query($connect, $query) or die (mysqli_error($connect));
    
    // Если данные не были получены с запроса, значит пользователь неправильно ввел данные при логине
    if (mysqli_num_rows($result) == null)
    {
        echo "ERROR";
        return;
    }
    
    $res = mysqli_fetch_assoc($result);
    $data = json_encode($res);
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

// POST запрос URI: .../users
function createUser($connect)
{
    $nickName = $_POST["NickName"];
    $password = $_POST["Password"];
    $group = $_POST["Group"];
    
    if ($nickName == NULL || $password == NULL || $group == NULL)
    {
        echo "ERROR";
        return;
    }
    
    // Создает нового юзера
    $query = "  INSERT INTO `users`(`NickName`, `Password`, `Code_Group`)
                VALUES ('$nickName','$password', (SELECT groups.Code_Group FROM groups WHERE groups.GroupName = '$group'))";
    
    mysqli_query($connect, $query) or die (mysqli_error($connect));
    
    
    // Добавляет пустой рейтинг юзера
    $query = "  INSERT INTO rating(Code_User, JSON_RATING)
                VALUES ((SELECT Code_User FROM users WHERE users.NickName = '$nickName'), '')";
    
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
    $json = file_get_contents('php://input');
    $data = json_decode($json);

    // Получаем данные
    $nickName = $data->{'NickName'};
    $password = $data->{'Password'};
    $groupName = $data->{'GroupName'};
   
    // Проверка на то, все ли данные пришли
    if ($nickName == NULL || $password == NULL || $groupName == NULL)
    {
        echo "ERROR";
        return;
    }
         
    $query = "  UPDATE `users`
                SET `NickName`='$nickName',
                    `Password`='$password',
                    `Code_Group`= (SELECT Code_Group FROM groups WHERE groups.GroupName = '$groupName') 
                    WHERE Code_User = '$id'";
    
    
    mysqli_query($connect, $query) or die (mysqli_error($connect));
    mysqli_close($connect);
}

/* PUT запрос обновления рейтинга URI: .../users/id/rating 
 * JSON данные:
 * Rating - JSON строка информации о рейтинге юзера
 */
function updateUserRating($connect, $id) 
{
    $json = file_get_contents('php://input');
    $data = json_decode($json);
    
    $rating = $data->{'Rating'};
    
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