<?php
    class DataBase
    {
        private $host = "localhost";
        private $username = "root";
        private $password = "";
        private $db_name = "csn";
        public $conn;
        
        public function getConnection()
        {           
            $conn = mysqli_connect($this->host, $this->username, $this->password, $this->db_name);
            mysqli_set_charset($conn, "utf8");
            return $conn;
        }
    }
?>