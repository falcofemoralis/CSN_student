<?php

require_once 'DataBase.php';
require_once 'Api.php';
require_once 'Users.php';

class UserApi extends Api
{
    protected function createAction()
    {
        if (empty($this->requestUri))
            createUser($this->connect);
        else if (array_shift($this->requestUri) == "rating")
        {
           //createUserRating(); метод не создан
        }
        else
            echo "invalid method";
    }
        
    protected function updateAction()
    {
        if (!empty($this->requestUri))
        {
            $id = array_shift($this->requestUri);
            if (empty($this->requestUri))
                updateUser($this->connect, $id);
            else if (array_shift($this->requestUri) == "rating")
            {
                //updateUserRating(); метод не создан
            }
            else
                echo "invalid method";
        }
    }

    protected function viewAction()
    {
        //userViewById($connect, $id); метод не создан
    }

    protected function deleteAction()
    {
                
    }

}