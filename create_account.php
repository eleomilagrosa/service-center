<?php  
	include("function.php");

	$first_name = addslashes($_POST['first_name']);
	$last_name = addslashes($_POST['last_name']);
	$uname = addslashes($_POST['username']);
	$pword = addslashes($_POST['password']);
	$email = addslashes($_POST['email']);
	$phone_no = addslashes($_POST['phone_no']);
	$account_type_id = addslashes($_POST['account_type_id']);
	$station_id = addslashes($_POST['station_id']);		

	$is_exist = check_if_username_exist($uname);

	if($is_exist->num_rows == 0){
		$account_id = create_account($first_name,$last_name,$uname,$pword,$email,$phone_no,$account_type_id,$station_id);
		if($account_id){
			$result = get_account_data($account_id);
			$response['success'] = $result['success'];
			$response['message'] = $result['message'];
			if($result['success'] == 1){
				$response['accounts'] = $result['accounts'];
			}
		}else{
			$response['success'] = 0;
			$response['message'] = "Fail Insert ID" . mysqli_error($GLOBALS['db']);
		}
	}else{
		$response['success'] = 0;
		$response['message'] = "Username Already Exists";
	}


	$data['data'] = json_encode($response);
	echo json_encode($data);

?>