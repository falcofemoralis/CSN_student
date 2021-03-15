<?php

class Subject
{
    public $name;
    public $img;
    public $teachers;

    function __construct($name, $img)
    {
        $this->name = $name;
        $this->img = $img;
        $this->teachers = array();
    }
}

//GET запрос на предметы по group
function getSubjectsByGroup($url)
{
    $id = explode('/', $url)[4];
    $imgPath = "http://192.168.0.104:81/images/subjects/";

    $query = "SELECT DISTINCT subjects.SubjectName, teachers.Code_Teacher, subjects.Image
    FROM schedule_list 
    INNER JOIN subjects on subjects.Code_Subject = schedule_list.Code_Subject 
    INNER JOIN schedule on schedule.Code_Schedule = schedule_list.Code_Schedule
    INNER JOIN teachers on teachers.Code_Teacher = schedule.Code_Teacher
    WHERE schedule_list.Code_Group = $id 
    ORDER BY subjects.SubjectName DESC";

    $data = json_decode(DataBase::execQuery($query, ReturnValue::GET_ARRAY));

    // Группировка учителей по дисциплинам
    $subjects = array();

    $subject = new Subject($data[0]->SubjectName, $data[0]->Image);
    array_push($subject->teachers, $data[0]->Code_Teacher);
    for ($i = 1; $i < count($data); ++$i) {
        if ($data[$i]->SubjectName !== $subject->name) {
            array_push($subjects, $subject);
            $subject = new Subject($data[$i]->SubjectName, $data[$i]->Image);
        }
        array_push($subject->teachers, $data[$i]->Code_Teacher);
    }
    array_push($subjects, $subject);


    for ($i = 0; $i < count($subjects); ++$i) {
        if ($subjects[$i]->img)
            $subjects[$i]->imgPath = $imgPath . $subjects[$i]->img;
    }
    echo json_encode($subjects);
}

//GET запрос на предметы по group URI: .../subjects?image=...
function getImageSubject()
{
    $image = $_GET["image"];

    // open the file in a binary mode
    $name = 'images/subjects/' . $image;
    if (file_exists($name))
        $fp = fopen($name, 'rb');
    else {
        echo "invalig method";
        return;
    }

    // send the right headers
    header("Content-Type: image/png");
    header("Content-Length: " . filesize($name));

    // dump the picture and stop the script
    echo fpassthru($fp);
}

//GET запрос на все предметы
function getShortAllSubjects()
{
    $query = " SELECT subjects.Code_Subject as id, subjects.SubjectName
    FROM subjects";

    $data = DataBase::execQuery($query, ReturnValue::GET_ARRAY);
    echo $data;
}