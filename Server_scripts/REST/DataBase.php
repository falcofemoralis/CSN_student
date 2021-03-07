<?php
    class ReturnValue {
        const GET_NOTHING = 0;
        const GET_OBJECT = 1;
        const GET_ARRAY = 2;
    }

    class DataBase
    {
        private static $host = "localhost";
        private static $username = "root";
        private static $password = "root";
        private static $db_name = "a0466974_csn";
        private static $connect = null;
        
        private static function getConnection()
        {          
            self::$connect = mysqli_connect(self::$host, self::$username, self::$password, self::$db_name);
            mysqli_set_charset(self::$connect, "utf8");
        }

        /* Выполнение запроса
        * Параметры:
        * query - запрос
        * response - true/false ожидание ответа
        */
        public static function execQuery($query, int $returnValue)
        {
            //TODO
            
            //if (self::$connect == null)
                self::getConnection(); // Подключение к базе данных
                
            // Выполнение запроса и получение данных
            $result = mysqli_query(self::$connect, $query);   
            
            //Если была получена ошибка, эта ошибка выбрасывается как исключение
            if (mysqli_errno(self::$connect))
                throw new Exception(mysqli_errno(self::$connect));

            mysqli_close(self::$connect);

            // Если ожидается ответ (SELECT запрос), формируется массив данных
            if ($returnValue == ReturnValue::GET_OBJECT)
                return json_encode(mysqli_fetch_assoc($result));
            else if ($returnValue == ReturnValue::GET_ARRAY)
            {
                if ($result != NULL)
                    $number_of_row = mysqli_num_rows($result);
                else
                    $number_of_row = 0;
                        
                $res_array = array();
                        
                if ($number_of_row > 0)
                    while ($row = mysqli_fetch_assoc($result))
                        $res_array[] = $row;
                                
                return json_encode($res_array);     
            }
        }
    }
?>