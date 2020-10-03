<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <link rel="stylesheet" type="text/css" href="table.css"/>
    </head>
    <body>
        <form action="SubmitSchedule.php" method="POST">
            <?php include('schedule_form_draw/setSelectDiscp.php')?>

            <h1 align="center">Рассписание групп</h1>

            <p style="font-size: 20pt;">Группа
            <?php include ('schedule_form_draw/setSelectGroups.php') ?>

            <h2>Понедельник</h2>
            <?php include ('schedule_form_draw/table_draw/table.php') ?>

            <h2>Вторник</h2>
            <?php include ('schedule_form_draw/table_draw/table.php') ?>

            <h2>Среда</h2>
            <?php include ('schedule_form_draw/table_draw/table.php') ?>

            <h2>Четверг</h2>
            <?php include ('schedule_form_draw/table_draw/table.php') ?>   
            
            <h2>Пятница</h2>
            <?php include ('schedule_form_draw/table_draw/table.php') ?>    

            <input type="submit" value="Сохранить">
        </form>
    </body>
</html>
