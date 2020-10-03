<?php
    // Получение списка групп с базы
    $response = file_get_contents("http://192.168.1.3/api/groups/all");
    $dataArray = json_decode($response);

    $choosen = $_GET['group']; // Текущая группа

    // Формирование списка групп
    $htmlSelect = "<select style=\"font-size: 15pt;\" name=\"group\">";

    if ($choosen == null)
        $htmlSelect .= "<option>Не выбрана</option>";

    foreach ($dataArray as $item)
        if ($choosen != $item->{'id'})
            $htmlSelect .= "<option value = " . $item->{'id'} . ">" . $item->{'GroupName'} . "</option>";
        else
            $htmlSelect .= "<option value = " . $item->{'id'} . " selected >" . $item->{'GroupName'} . "</option>";
    $htmlSelect .= "</select>";
    echo $htmlSelect;
?>