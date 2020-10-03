<?php
    
    $htmlSelect = "<select name=\"disc[]\" class = \"small\">";
    $fd = fopen("disc.html", 'w+');

    // Получение списка дисциплин с базы
    $response = file_get_contents("http://<ВСТАВЬ URL>/api/subjects/shortAll");
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