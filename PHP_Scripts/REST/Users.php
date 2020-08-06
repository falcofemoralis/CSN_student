<?php

// GET запрос URI: users/login
function readUser($connect)
{
    $nickName = $_GET["NickName"];
    $password = $_GET["Password"];
    
    $query = "  SELECT users.Code_User, users.NickName, users.Password,  groups.Course, groups.GroupName FROM users
                JOIN rating ON rating.Code_User = users.Code_User
                JOIN groups ON groups.Code_Group = users.Code_Group
                WHERE users.NickName = '$nickName' AND users.Password = '$password'";
    
    $result = mysqli_query($connect, $query) or die (mysqli_error($connect));
    
    if (mysqli_num_rows($result) == null)
    {
        echo "null";
        return;
    }
    
    $res = mysqli_fetch_assoc($result);
    $data = json_encode($res);
    echo $data;
    
    mysqli_close($connect);
}


// POST запрос URI: users/
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
    
    $query = "  INSERT INTO `users`(`NickName`, `Password`, `Code_Group`)
                VALUES ('$nickName','$password', (SELECT groups.Code_Group FROM groups WHERE groups.GroupName = '$group'))";
    
   mysqli_query($connect, $query) or die (mysqli_error($connect));
   mysqli_close($connect);
}

/* PUT запрос URI: users/id 
 * ƒанные юзера которые можно изменить:
 * NickName - ник
 * Password - пароль 
 * GroupName - group
 */
function updateUser($connect, $id)
{
    $json = file_get_contents('php://input');
    $data = json_decode($json);

    $nickName = $data->{'NickName'};
    $password = $data->{'Password'};
    $groupName = $data->{'GroupName'};
   
    
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

?>