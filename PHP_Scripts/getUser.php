<?php

if($_SERVER["REQUEST_METHOD"]=="POST"){
	include 'connection.php';
	getUser();
}

function getUser()
{
	global $connect;
	
	
	//$NickName = $_POST["NickName"];
	//$NickName = json_decode( $_POST['NickName'] );
	//$jsonString = '{"NickName":"Arti"}';
	
    $post = json_decode(file_get_contents("php://input"), true);
    $my_value = $post['NickName'];
	$query = "Select NickName FROM registration WHERE NickName = '$my_value'";
	$result = mysqli_query($connect, $query);
	$number_of_rows = mysqli_num_rows($result);
	
	$temp_array  = array();
	
	if($number_of_rows > 0) {
		while ($row = mysqli_fetch_assoc($result)) {
			$temp_array[] = $row;
		}
	}
	
	header('Content-Type: application/json');
	echo json_encode(array("students"=>$temp_array));
	mysqli_close($connect);
	
}


?>