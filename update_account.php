<?php  
	include("function.php");

	$id = addslashes($_POST['id']);
	$first_name = addslashes($_POST['first_name']);
	$last_name = addslashes($_POST['last_name']);
	$email = addslashes($_POST['email']);
	$phone_no = addslashes($_POST['phone_no']);
	$account_type_id = addslashes($_POST['account_type_id']);
	$station_id = addslashes($_POST['station_id']);
		
	update_account($id,$first_name,$last_name,$email,$phone_no,$account_type_id,$station_id);

	if(mysqli_affected_rows($GLOBALS['db']) > 0 ){
		$result = get_account_data($id);
		$response['success'] = $result['success'];
		$response['message'] = $result['message'];
		if($result['success'] == 1){
			$response['accounts'] = $result['accounts'];
		}
	}else{
		$response['success'] = 0;		
	}

	$data['data'] = json_encode($response);
	echo json_encode($data);

?>