<?php
    require_once 'DataBase.php';
    
    //abstract class Api
    abstract class Api
    {
        protected $method = ''; // ћетод запроса (GET/POST/PUT/DELETE)

        protected $requestUri = [];
        
        protected $action = ''; // Ќазвание метода дл€ действи€
        protected $connect;
        
        // онструктор "вынимает" из запроса все необходимые данные (тип запроса, параметры переданные в URI, параметры переданные в теле запроса)
        public function __construct($requestUri)
        {
            $db = new DataBase();
            $this->connect = $db->getConnection();
            $this->method = $_SERVER['REQUEST_METHOD'];
            $this->requestUri = $requestUri;
        }
          
        // ќбрабатывает запрос
        public function run()
        {
            // ќпредел€ем действие
            $this->action = $this->getAction();
            
            // ѕровер€ем переопределено ли это действие в наследнике
            if (method_exists($this, $this->action))
                return $this->{$this->action}();
            else 
                throw new RuntimeException('Invalid method', 405);
        }
           
        // ћетод API который будет выполн€тс€ в зависимости от типа запроса
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