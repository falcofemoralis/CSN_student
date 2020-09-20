<?php
    class DataBase
    {
        private static $host = "localhost";
        private static $username = "root";
        private static $password = "root";
        private static $db_name = "csn";
        private static $connect = null;
        
        private static function getConnection()
        {          
            $connect = mysqli_connect(self::$host, self::$username, self::$password, self::$db_name);
            mysqli_set_charset($connect, "utf8");
        }

        /* Выполнение запроса
        * Параметры:
        * query - запрос
        * response - true/false ожидание ответа
        */
        public static function execQuery($query, bool $getResponse)
        {
            if (self::$connect == null)
                self::getConnection(); // Подключение к базе данных

            // Выполнение запроса и получение данных
            $result = mysqli_query(self::$connect, $query);   
            
            //Если была получена ошибка, эта ошибка выбрасывается как исключение
            if (mysqli_errno(self::$connect))
                throw new Exception(mysqli_errno(self::$connect));

            mysqli_close(self::$connect);

            // Если ожидается ответ (SELECT запрос), формируется массив данных
            if ($getResponse)
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