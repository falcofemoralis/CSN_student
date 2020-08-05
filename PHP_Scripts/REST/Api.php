<?php

    //abstract class Api
    class Api
    {
        protected $method = ''; // ћетод запроса (GET/POST/PUT/DELETE)

        public $requestUri = [];
        protected $requestParams = [];
        
        protected $action = ''; // Ќазвание метода дл€ действи€
        
        // онструктор "вынимает" из запроса все необходимые данные (тип запроса, параметры переданные в URI, параметры переданные в теле запроса)
        public function __construct()
        {
            $this->method = $_SERVER['REQUEST_METHOD'];
            $this->requestUri = explode('/', $_SERVER['REQUEST_URI']);
            $this->requestParams = $_REQUEST;
            array_shift($this->requestUri); // ƒелаетс€ сдвиг потому первый элемент всегда пустой ''
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
        
        /*abstract protected function createAction();
        abstract protected function viewAction();
        abstract protected function deleteAction();
        abstract protected function updateAction();*/
    }

?>