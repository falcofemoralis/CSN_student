<?php

require_once 'DataBase.php';
require_once 'Api.php';
require_once 'Users/Users.php';

class UserApi extends Api
{
    // Добавление в базу новых данных
    protected function createAction()
    {
        if (empty($this->requestUri))
            createUser();
        else
            echo "invalid method";
    }
        
    // Обновление данных
    protected function updateAction()
    {       
        /* Проверяем остались ли еще данные в URI, 
         * для обновления данных необходимо знать как минимум id пользователя,
         * чьи данные необхоидмо обновить
         */                   
        if (!empty($this->requestUri))
        {
            $id = array_shift($this->requestUri);
            if (empty($this->requestUri))
                updateUser($id); // Обновление данных юзера по id
            else if (array_shift($this->requestUri) == "rating")         
                updateUserRating($id); // Обновление рейтинга юзера по id
            else
                echo "invalid method";
        }
        else 
            echo "invalid method";
    }

    // Просмотр данных
    protected function viewAction()
    {         
        if (!empty($this->requestUri))
        {
            if ($this->requestUri[0] == "login")
                readUser();
            else
            {
                $id = array_shift($this->requestUri);
                userViewById($id);
            }
        }
        else            
            echo "invalid method";
    }

    // Удаление данных
    protected function deleteAction() {}
}

?>