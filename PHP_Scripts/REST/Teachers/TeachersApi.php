<?php

require_once 'DataBase.php';
require_once 'Api.php';
require_once 'Teachers/Teachers.php';

class TeachersApi extends Api
{
    // Добавление в базу новых данных
    protected function createAction()
    {
        echo "invalid method";
    }
    
    // Обновление данных
    protected function updateAction()
    {
         echo "invalid method";
    }
    
    // Просмотр данных
    protected function viewAction()
    {
        if (!empty($this->requestUri))
        {
            if ($this->requestUri[0] == 'all')
                getAllTeacher($this->connect);
            else
            {
                $id = array_shift($this->requestUri);
                if (!empty($this->requestUri) && array_shift($this->requestUri) == 'schedule')
                    viewTeacherSchedules($this->connect, $id);
                else 
                    echo "invalid method";
            }
        }
        else
            echo "invalid method";
    }
    
    // Удаление данных
    protected function deleteAction()
    {
        echo "invalid method";
    }
    
}

?>