<?php

require_once 'DataBase.php';
require_once 'Api.php';
require_once 'Groups/Groups.php';

class GroupsApi extends Api
{  
    // Добавление в базу новых данных
    protected function createAction()
    {
        getJSON();
    }
    
    // Обновление данных
    protected function updateAction()
    {
        echo "invalid method";
    }
    
    // Просмотр данных
    protected function viewAction()
    {
        if (empty($this->requestUri))
            getGroupsOnCourse($this->connect);
        else if ($this->requestUri[0] == 'updateList')
            getUpdateList();
        else 
        {
            $id = array_shift($this->requestUri);
            if (array_shift($this->requestUri) == 'schedule')
                getScheduleById($this->connect, $id);
            else 
                echo "invalid method";
        }
    }
    
    // Удаление данных
    protected function deleteAction()
    {
        echo "invalid method";
    }
    
}

?>