<?php
    
    $choosen = $_GET['group']; // Текущая группа

    $htmlSelect = "<select>";

    // Проверка на то выбрана ли группа
    if ($choosen == null)
    {
        echo $htmlSelect . "</select>";
        return;
    }


?>