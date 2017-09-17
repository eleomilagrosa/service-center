<?php

	$host = '192.168.0.106';
	$username = 'admin';
	$password = '21posincbibiza4012006';
	$database = 'servicecenter';
	$port = 2121;
	$check_connection = 'false';

	/*$method = $_SERVER['REQUEST_METHOD'];

	switch($method){
		case 'GET':
		$host = $_GET['host'];
		$username = $_GET['username'];
		$password = $_GET['password'];
		$database = $_GET['db_name'];
		$port = $_GET['port'];
		$check_connection = 'false';
		break;
		case 'POST':
		$host = $_POST['host'];
		$username = $_POST['username'];
		$password = $_POST['password'];
		$database = $_POST['db_name'];
		$port = $_POST['port'];
		$check_connection = 'false';
		break;
	}*/

	// if(!$_SERVER['REQUEST_METHOD'] === 'GET'){
	// 	echo "GET NOT EMPTY";
	// 	$host = $_GET['host'];
	// 	$username = $_GET['username'];
	// 	$password = $_GET['password'];
	// 	$database = $_GET['db_name'];
	// 	$port = $_GET['port'];
	// 	$check_connection = 'false';
	// }else{
	// 	// echo "GET EMPTY";
	// }

	// if(!$_SERVER['REQUEST_METHOD'] === 'POST'){
		// echo "POST NOT EMPTY";
		// $host = $_POST['host'];
		// $username = $_POST['username'];
		// $password = $_POST['password'];
		// $database = $_POST['db_name'];
		// $port = $_POST['port'];
		// $check_connection = 'false';

	// }else{
	// 	// echo "POST EMPTY";
	// }

	if($check_connection == "true")
		if(mysqli_connect_error()){
				echo json_encode(array(false));
			die();
		}else{
				echo json_encode(array(true));
			die();
		}
 ?>
