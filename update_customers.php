<?php  
	include("function.php");

	$id = addslashes($_POST['id']);
	$first_name = addslashes($_POST['first_name']);
	$last_name = addslashes($_POST['last_name']);
	$address = addslashes($_POST['address']);
	$phone_no = addslashes($_POST['phone_no']);
	$email = addslashes($_POST['email']);
	$account_id = addslashes($_POST['account_id']);
		
	update_customers($id,$first_name,$last_name,$address,$phone_no,$email,$account_id);

	if(mysqli_affected_rows($GLOBALS['db']) > 0){
		$customers = get_customer_by_id($id);
		if($customers->num_rows == 0){
			$response['success'] = 0;
			$response['message'] = "Failed to retrieve id";
		}else{
			$response['customers'] = array();
			while($r = $customers->fetch_assoc()){
				array_push($response['customers'], $r);
			}
			$response['success'] = 1;
		}
	}else{
		$response['success'] = 0;		
	}

	$data['data'] = json_encode($response);
	echo json_encode($data);

?>