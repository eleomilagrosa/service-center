<?php  
	include("function.php");

	$first_name = addslashes($_POST['first_name']);
	$last_name = addslashes($_POST['last_name']);
	$address = addslashes($_POST['address']);
	$phone_no = addslashes($_POST['phone_no']);
	$email = addslashes($_POST['email']);
	$station_id = addslashes($_POST['station_id']);
	$account_id = addslashes($_POST['account_id']);
		
	$customer_id = create_customers($first_name,$last_name,$address,$phone_no,$email,$account_id,$station_id);

	if($customer_id){
		$customers = get_customer_by_id($customer_id);
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
		$response['message'] = "Fail Insert ID" . mysqli_error($GLOBALS['db']);
	}	

	$data['data'] = json_encode($response);
	echo json_encode($data);

?>