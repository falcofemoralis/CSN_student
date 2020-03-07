<?php

if ($_SERVER["REQUEST_METHOD"]=="POST")
{
	require 'connection.php';
	createStudent();
}

function createStudent()
{
	global $connect;

	$NickName = $_POST["NickName"];
	$Password = $_POST["Password"];

	$query = "INSERT INTO registration(NickName, Password) values ('$NickName', '$Password');";
	
	mysqli_query($connect, $query) or die (mysqli_error($connect));
	mysqli_close($connect);
}