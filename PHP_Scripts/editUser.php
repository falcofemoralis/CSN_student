<?php

if ($_SERVER["REQUEST_METHOD"]=="POST")
{
	require 'connection.php';
	editStudent();
}

function editStudent()
{
	global $connect;

	$NickName = $_POST["NickName"];
	$OldNickName = $_POST["OldNickName"];

	$query = "UPDATE `registration` SET `Nickname`='$NickName' WHERE `NickName`='$OldNickName'";
	mysqli_query($connect, $query) or die (mysqli_error($connect));
	mysqli_close($connect);
}