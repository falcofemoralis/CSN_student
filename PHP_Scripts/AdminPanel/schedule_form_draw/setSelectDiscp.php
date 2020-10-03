<?php
    
    $choosen = $_GET['group']; // Текущая группа

    $htmlSelect = "<select name=\"disc[]\" class = \"small\">";
    $fd = fopen("disc.html", 'w+');


    // Проверка на то выбрана ли группа
    if ($choosen == null)
    {
        fwrite($fd, $htmlSelect . "<option></option> </select>");
        fclose($fd);
        echo "here";
        return;
    }

    // Получение списка дисциплин с базы
    $response = file_get_contents("http://192.168.1.3/api/subjects/shortAll");
    $dataArray = json_decode($response);

    $htmlSelect .= "<option value = \"-1\"></option>";
    foreach ($dataArray as $item)
    {
        $nameDiscp =  (json_decode($item->{'NameDiscipline'}))->{'uk'};
        $arrWord = explode(' ', $nameDiscp);
        $abb = "";

        foreach ($arrWord as $word)
            $abb .= mb_substr($word, 0, 1);

        $htmlSelect .= "<option value = " . $item->{'id'} . ">" . mb_strtoupper($abb) . "</option>";
    }

    fwrite($fd, $htmlSelect . "</select>");
    fclose($fd);
?>