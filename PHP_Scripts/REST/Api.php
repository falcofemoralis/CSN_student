<?php
require_once 'DataBase.php';

    //abstract class Api
    abstract class Api
    {
        protected $method = ''; // Метод запроса (GET/POST/PUT/DELETE)
        
        protected $requestUri = [];
        
        protected $action = ''; // Название метода для действия
        protected $connect;
        
        //Конструктор "вынимает" из запроса все необходимые данные (тип запроса, параметры переданные в URI, параметры переданные в теле запроса)
        public function __construct($requestUri)
        {
            $db = new DataBase();
            $this->connect = $db->getConnection();
            $this->method = $_SERVER['REQUEST_METHOD'];
            $this->requestUri = $requestUri;
        }
        
        // Обрабатывает запрос
        public function run()
        {
            // Определяем действие
            $this->action = $this->getAction();
            
            // Проверяем переопределено ли это действие в наследнике
            return $this->{$this->action}();
        }
        
        // Метод API который будет выполнятся в зависимости от типа запроса
        public function getAction()
        {
            switch ($this->method)
            {
                case "POST":
                    return 'createAction';
                    break;
                case "GET":
                    return 'viewAction';
                    break;
                case "DELETE":
                    return 'deleteAction';
                    break;
                case "PUT":
                    return 'updateAction';
                    break;
            }
        }
        
        abstract protected function createAction();
        abstract protected function viewAction();
        abstract protected function deleteAction();
        abstract protected function updateAction();
    }

?>