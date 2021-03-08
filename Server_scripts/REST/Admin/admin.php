<?php

require_once 'DataBase.php';

$encoding = "UTF-8";
$days = ["пн", "вт", "ср", "чт", "пт"]; // Название дней в файле 
$pairs = ["I", "II", "III", "IV", "V", "VI"]; // Название пар в файле
$pair_info = ["предмет", "аудитория", "группа", "тип"]; // Название аттрибутов в итоговом файле
$pair_type = ["лк", "пр"]; // Тип пары
$no_auditorium = ["з-д", "ск", "каф.", "ДО"]; // Тип, который не есть аудиторией
$dataToReplace = ["П О Н Е Д I Л О К", "В I В Т О Р О К", "С Е Р Е Д А", "Ч Е Т В Е Р", "П' Я Т Н И Ц Я"]; // Данные которые нужно заменить
$week_types = ["chisl", "znam", "obe"]; // Тип недели
$schedule = null; // JSON расписание
$isError = false;
$subjectLinks = [
    "Арх.веб" => "1",
    "МОКС" => "2",
    "КМ" => "3",
    "ОТІС" => "4",
    "ТСЦС" => "5",
    "КЛ" => "6",
    "Арх." => "7",
    "КС" => "8",
    "ЗІ" => "9",
    "СІОЗОТ" => "10",
    "МСЕС" => "11",
    "КГ" => "12",
    "ПКМ" => "13",
    "БТ" => "14",
    "МС" => "15",
    "СМП" => "16",
    "АМО" => "17",
    "Прог" => "18",
    "СПЗ" => "19",
    "ОБД" => "20",
    "ВС" => "21",
    "СМтаМІС" => "22",
    "БЖД" => "23",
    "ЦЗіОПГ" => "24",
    "Фіз.вих." => "25",
    "Фізика" => "26",
    "Філософ." => "27",
    "ВК" => "28",
    "ВМ" => "29",
    "ДМ" => "30",
    "Ін.мова" => "31",
    "СА" => "32",
    "ХТ" => "33",
    "Арх.сл." => "34",
    "ЕВД" => "35"
];

$departmentLinks = [
    "каф.ОПіНС" => "30",
    "каф.ОПіНС(КоробкоО.В.)" => "41",
    "каф.ПТтаБД" => "42",
    "каф.ФКОНВС" => "29",
    "каф.Фізики(ЛоскутовС.В.)" => "39",
    "каф.Філософії" => "43",
    "каф.військовоїпідготовки" => "31",
    "каф.прикладноїматематики" => "44",
    "каф.прикладноїматематики(ПожуєваІ.С.)" => "38",
    "каф.іноземнихмов" => "35",
    "каф.Фізики(КурбацькийВ.П.)" => "45"
];

function inserInDatabase()
{
    global $pair_info;
    global $subjectLinks;
    global $departmentLinks;
    global $encoding;
    global $isError;
    global $week_types;
    $connection = DataBase::getConnection();

    // Получение id препода по фамилии
    //  $result = mysqli_query($connect, "SELECT Code_Teacher FROM teachers WHERE teachers.FIO LIKE '%Ільяшенко%'");
    //  echo mysqli_insert_id($connect);
    /*     for ($i = 0; $i < mysqli_num_rows($result); ++$i) {
        $res =  mysqli_fetch_assoc($result);
        echo $res['Code_Teacher'];
    } */


    $data = json_decode(file_get_contents("./res.json"));
    foreach ($data as $FIO => $obj) {
        $FIOArr = explode(" ", $FIO);
        $surname = normilizeName($FIOArr[0]);
        $name = $FIOArr[1];
        $otchstvo = $FIOArr[2];

        if ($surname == "Каф.") {
            // Получение id кафедры
            $codeTeacher = $departmentLinks[str_replace(" ", "", $FIO)];
        } else {
            // Получение id препода по фамилии
            $codeTeacher =  mysqli_fetch_assoc(mysqli_query($connection, "SELECT Code_Teacher FROM teachers WHERE teachers.FIO LIKE '%$name $otchstvo $surname%'"))['Code_Teacher'];
        }

        if ($codeTeacher == null) {
            showError("Teacher\departement $FIO doesn't exist");
            continue;
        }

        mysqli_query($connection, "INSERT INTO schedule(Code_Teacher) VALUES ($codeTeacher)");
        $codeSchedule = mysqli_insert_id($connection);

        foreach ($obj as $day => $obj2) {
            foreach ($obj2 as $pair => $obj3) {
                foreach ($obj3 as $half => $attr) {
                    $codeSubject = null;
                    $codesGroup = null;
                    $codeSubjectType = null;
                    $room = null;
                    $half = array_search($half, $week_types);

                    foreach ($attr as $key => $value) {
                        switch ($key) {
                            case $pair_info[0]:
                                // Получение id предмета
                                $codeSubject = $subjectLinks[$value];
                                if ($codeSubject == null) {
                                    showError("Subject $value doesn't exist");
                                }
                                break;
                            case $pair_info[1]:
                                // Получение номера аудитории
                                $room = $value;
                                break;
                            case $pair_info[2]:
                                // Получение id группы
                                $groups = explode(';', $value);

                                for ($i = 0; $i < count($groups); $i++) {
                                    $group = $groups[$i];
                                    $codeGroup =  mysqli_fetch_assoc(mysqli_query($connection, "SELECT groups.Code_Group FROM groups WHERE groups.GroupName like 'КНТ-%$group%'"))['Code_Group'];
                                    if ($codeGroup) {
                                        $codesGroup[$i] = $codeGroup;
                                    } else {
                                        showError("Group $group doesn't exist");
                                    }
                                }
                                break;
                            case $pair_info[3]:
                                // Получение id типа предмета
                                $value = mb_strtoupper($value, $encoding);
                                $codeSubjectType =  mysqli_fetch_assoc(mysqli_query($connection, "SELECT Code_SubjectType FROM subjecttypes WHERE subjecttypes.SubjectType LIKE '%$value%'"))['Code_SubjectType'];
                                break;
                            default:
                                echo "invalid attribute <br>";
                        }
                    }

                    // когда узнали все аттрибуты, делаем инсерт
                    if (!$isError) {
                        for ($i = 0; $i < count($codesGroup); $i++) {
                            $codeGroup = $codesGroup[$i];
                            $query =  "INSERT INTO `schedule_list`(`Code_Schedule`, `Day`, `Pair`, `Half`, `Code_Subject`, `Code_Group`, `Room`, `Code_SubjectType`) 
                            VALUES ($codeSchedule, $day,$pair,$half,$codeSubject,$codeGroup,'$room',$codeSubjectType)";
                            mysqli_query($connection, $query) or showError("Ошибка " . mysqli_error($connection));
                        }
                    }
                }
            }
        }

        /*  */
    }

    if ($isError) {
        mysqli_query($connection, "DELETE FROM schedule");
        mysqli_query($connection, "DELETE FROM schedule_list");
        mysqli_query($connection, "ALTER TABLE schedule AUTO_INCREMENT = 1");
    }
}

function showError($msg)
{
    global $isError;
    if (!$isError) echo "Error occurred. All insert operations has been stopped. All inserted data will be truncated at the end. <br>";
    echo $msg;
    echo "<br>";
    $isError = true;
}


function normilizeName($name)
{
    global $encoding;
    $name = mb_strtolower($name);
    $firstChar = mb_substr($name, 0, 1, $encoding);
    $then = mb_substr($name, 1, null, $encoding);
    $name = mb_strtoupper($firstChar, $encoding) . $then;
    $name = str_replace("i", "і", $name);
    return $name;
}


/**
 * Конвертер текста в JSON формат
 */
function convertData()
{
    global $days;
    global $pairs;
    global $pair_type;
    global $no_auditorium;
    global $dataToReplace;
    global $week_types;
    global $schedule;

    /////////////////////////////////////////////////////////////////
    /////////////////////// ONLY PHP 7.1 ////////////////////////////
    /////////////////////////////////////////////////////////////////
    //1) преобразовать пдф файл
    //https://products.aspose.app/pdf/ru/parser
    //2) перевести кодировку в UTF-8

    // Переменные дня
    $cur_day_ind = 0; // Текущий день
    $cur_day = null;
    $day_search_mode = false; // Режим поиска дня

    // Переменные пары
    $cur_pair_ind = 0; // Текущая пара
    $pair_info_ind = 0; // Текущий аттрибут пары
    $pair_search_mode = false; // Режим поиска аттрибутов пары
    $subject_add_mode = true;
    $group_add_mode = false;
    $week_type = $week_types[2]; // тип недели 0 - числ, 1 - знам
    $week_type_mode = false;

    // Переменные препода
    $cur_teacher = null; // ФИО текущего препода
    $teacher_search_mode = true; // Режим поиска ФИО препода 
    $teacher = null; // Препод который собирается

    // Прочине переменные
    $wordRegEx = ["/(?<![\w\d])", "(?![\w\d])/"];
    $space_count = 0;

    // Получение данных из файла
    $data = file_get_contents("./data.txt");

    // Замена лишних переменных
    for ($i = 0; $i < count($dataToReplace); ++$i) {
        $data = str_replace($dataToReplace[$i], " " . $days[$i], $data);
    }
    $data = str_replace("День", " День", $data);

    $dataArr = explode(" ", $data); // Разбитые данные на массив

    //  echo var_dump($dataArr);

    foreach ($dataArr as $data) {
        if ($data != "" && $data != "﻿") {
            // Если дата это фамилия препода, то след 2 слова это его имя и фамилия
            if ($data && $teacher_search_mode) {
                // Если препода собрали, сохраняем его и сбрасываем все переменные
                if (preg_match("/День/", $data)) {
                    $teacher = substr($teacher, 0, -1);
                    $cur_teacher = str_replace("\n", "", $teacher);
                    $teacher_search_mode = false;
                    $teacher = null;
                    continue;
                }

                $teacher_search_mode = true; // Включаем режим поиска слова
                $teacher .= "$data "; // Добавляем часть ФИО в переменную
                continue;
            }

            // если дата это название дня, то делаем поиск пары
            if ($days[$cur_day_ind] == $data || $day_search_mode) {
                $day_search_mode = true; // Включаем режим поиска дня
                if ($days[$cur_day_ind] == $data) $cur_day = $data;

                //6 пара, заканчиваем поиск  и срабсываем все переменные
                if (preg_match("$wordRegEx[0]$pairs[5]$wordRegEx[1]", $data)) {
                    if ($days[4] == $cur_day) {
                        $teacher_search_mode = true;
                        $cur_day_ind = 0;
                    } else {
                        $cur_day_ind++;
                    }

                    $day_search_mode = false;
                    $pair_search_mode = false;
                    $group_add_mode = false;
                    $cur_pair_ind = 0;
                    $pair_info_ind = 0;
                    $space_count = 0;
                    continue;
                }

                // Смотрит является ли данные номером пары
                if (preg_match("$wordRegEx[0]$pairs[$cur_pair_ind]$wordRegEx[1]", $data)) {
                    $pair_search_mode = true; // Включаем режим поиска инфы про предмет
                    $space_count = 0;
                } else if (preg_match("$wordRegEx[0]" . $pairs[$cur_pair_ind + 1] . "$wordRegEx[1]", $data)) {
                    // Определение числителя, если после предмета было кучу пробелов
                    if ($week_type_mode) {
                        // Переносим из общей недели в числитель
                        replaceData($cur_teacher, $cur_day_ind, $cur_pair_ind, 2, 0);
                        $week_type_mode = false;
                    }

                    // Если предмета нету, переходим к следуюющей паре
                    $cur_pair_ind++;
                    $pair_info_ind = 0;
                    $group_add_mode = false;
                    $subject_add_mode = true;
                    $space_count = 0;
                    $week_type = $week_types[2];
                } else if ($pair_search_mode) {
                    // Определние знаменателя (если перед предметом было 20 пробелов)
                    if ($space_count > 20) {

                        if ($week_type == $week_types[2] &&  getData($cur_teacher, $cur_day_ind, $cur_pair_ind, $week_type, null, "week") != null) {
                            // Переносим из общей недели в числитель
                            replaceData($cur_teacher, $cur_day_ind, $cur_pair_ind, 2, 0);
                        }

                        $week_type = $week_types[1];
                        $pair_info_ind = 0;
                        $group_add_mode = false;
                        $subject_add_mode = true;
                        $space_count = 0;
                    }

                    // Определение типа предмета, изначально будет выставленно как практика
                    if (preg_match("/$pair_type[0]/", mb_strtolower($data))) {
                        addData($cur_teacher, $cur_day_ind, $cur_pair_ind, $week_type, 3, $data, false);
                        continue;
                    } else if (getData($cur_teacher, $cur_day_ind, $cur_pair_ind, $week_type, 3, null) == null) {
                        addData($cur_teacher, $cur_day_ind, $cur_pair_ind, $week_type, 3, $pair_type[1], false);
                    }

                    // Отслеживаем является ли числителем строка
                    if (preg_match("/\n\n/", $data)) {
                        $week_type_mode = true;
                    }

                    // Если включен режим, то добавление будет происходить в группы
                    if ($group_add_mode) {
                        addData($cur_teacher, $cur_day_ind, $cur_pair_ind, $week_type, 2, $data, true);
                        continue;
                    }

                    // Определение режима добавления групп
                    if (preg_match("/;/", $data)) {
                        $group_add_mode = true;
                    }

                    // Определение режима прекращена составление название предмета
                    if (preg_match("/\d/", $data)) {
                        if ($subject_add_mode) {
                            $subject_add_mode = false;
                            $pair_info_ind++;
                        }
                    } else {
                        // Проверяем ялвяется ли аудитория частным случаем
                        for ($i = 0; $i < count($no_auditorium); ++$i) {
                            if (preg_match("$wordRegEx[0]$no_auditorium[$i]$wordRegEx[1]", $data)) {
                                addData($cur_teacher, $cur_day_ind, $cur_pair_ind, $week_type, 1, $data, false);

                                if ($subject_add_mode) {
                                    $subject_add_mode = false;
                                    $pair_info_ind++;
                                }
                                break;
                            }
                        }
                    }

                    // Если режим составлении имени предмета включен
                    if ($subject_add_mode) {
                        addData($cur_teacher, $cur_day_ind, $cur_pair_ind, $week_type, 0, $data, true);
                        continue;
                    }

                    // Добавляем данные про предмет
                    addData($cur_teacher, $cur_day_ind, $cur_pair_ind, $week_type, $pair_info_ind, $data, false);
                    $pair_info_ind++;
                }
            }
        } else {
            $space_count++;
        }
    }

    file_put_contents("res.json", json_encode($schedule, JSON_UNESCAPED_UNICODE));
    // echo json_encode($schedule, JSON_UNESCAPED_UNICODE);
}

/**
 * Добавление данных в расписание
 */
function addData($cur_teacher, $cur_day_ind, $cur_pair_ind, $week_type, $pair_info_ind, $data, $isAppend)
{
    global $pair_info;
    global $schedule;

    // Перевод дня и пары в HRS
    $day = $cur_day_ind + 1;
    $pair = $cur_pair_ind + 1;

    $data = str_replace("\n", "", $data);

    if ($isAppend) {
        $schedule[$cur_teacher]["$day"]["$pair"][$week_type][$pair_info[$pair_info_ind]] .= $data;
    } else {
        $schedule[$cur_teacher]["$day"]["$pair"][$week_type][$pair_info[$pair_info_ind]] = $data;
    }
}

/**
 * Получение данных
 */
function getData($cur_teacher, $cur_day_ind, $cur_pair_ind, $week_type, $pair_info_ind, $type_data)
{
    global $pair_info;
    global $schedule;

    // Перевод дня и пары в HRS
    $day = $cur_day_ind + 1;
    $pair = $cur_pair_ind + 1;

    if ($type_data == "week") {
        return  $schedule[$cur_teacher]["$day"]["$pair"][$week_type];
    } else {
        return $schedule[$cur_teacher]["$day"]["$pair"][$week_type][$pair_info[$pair_info_ind]];
    }
}

/**
 * Замена данных
 */
function replaceData($cur_teacher, $cur_day_ind, $cur_pair_ind, $week_type_ind_from, $week_type_ind_to)
{
    global $schedule;
    global $week_types;

    // Перевод дня и пары в HRS
    $day = $cur_day_ind + 1;
    $pair = $cur_pair_ind + 1;

    $schedule[$cur_teacher]["$day"]["$pair"][$week_types[$week_type_ind_to]] = $schedule[$cur_teacher]["$day"]["$pair"][$week_types[$week_type_ind_from]];
    unset($schedule[$cur_teacher]["$day"]["$pair"][$week_types[$week_type_ind_from]]);
}