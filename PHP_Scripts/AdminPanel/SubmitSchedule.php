<?php
    $disc = $_POST['disc'];
    $rooms = $_POST['room'];
    $subjectTypes = $_POST['subjectType'];
    $halfs = $_POST['half'];

    $length = (count($disc));

    $days = $length / 10;
    $params = array();

    // Формирование массива параметров в POST запрос (строки в таблице расписаний)
    for ($i = 0; $i < $days; ++$i)
    {
        for ($j = 0; $j < 10; ++$j)
        {
            $day = $i + 1;
            $pair = (int)($j / 2 + 1);
            $half = $halfs[$i * 10 +$j];
            $codeDiscp = $disc[$i * 10 +$j];
            $room = $rooms[$i * 10 +$j];
            $subjectType = $subjectTypes[$i * 10 +$j];

            array_push($params, json_encode(array(
                'day' => $day,
                'pair' => $pair, 
                'half' => $half,
                'codeDiscp' => $codeDiscp, 
                'room' => $room,
                'subjectType' => $subjectType, 
            )));
        }
    }
    $schedule = array('schedule' => $params);
    
    // Формирование самого POST запроса
    $idGroup = $_POST['group'];
    $url = 'http://<ВСТАВЬ IP АДРЕС>/api/groups/' . $idGroup . '/schedule';

    $result = file_get_contents($url, false, stream_context_create(array(
            'http' => array(
            'method'  => 'POST',
            'header'  => 'Content-type: application/x-www-form-urlencoded',
            'content' => http_build_query($schedule)
        )
    )));

    echo "Сохранено";
?>