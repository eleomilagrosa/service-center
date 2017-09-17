<?php  
	include("function.php");

	$id = addslashes($_POST['id']);
	$unit = addslashes($_POST['unit']);
	$model = addslashes($_POST['model']);
	$date_of_purchased = addslashes($_POST['date_of_purchased']);
	$dealer = addslashes($_POST['dealer']);
	$serial_number = addslashes($_POST['serial_number']);
	$warranty_label = addslashes($_POST['warranty_label']);
	$complaint = addslashes($_POST['complaint']);
	$customer_id = addslashes($_POST['customer_id']);
	$account_id = addslashes($_POST['account_id']);
	$status_id = addslashes($_POST['status_id']);
	$station_id = addslashes($_POST['station_id']);
		

	$job_orders_id = create_job_order($id,$unit,$model, $date_of_purchased, $dealer, $serial_number, $warranty_label, $complaint, $customer_id, $account_id,$status_id,$station_id);

	if($job_orders_id){
		$job_orders = get_job_order_data($id);
		$response['success'] = $job_orders['success'];
		$response['message'] = $job_orders['message'];
		if($response['success'] == 1){
			$response['job_orders'] = $job_orders['job_orders'];
		}
	}else{
		$response['success'] = 0;
		$response['message'] = "Failed Insert" . mysqli_error($GLOBALS['db']);
	}	

	$data['data'] = json_encode($response);
	echo json_encode($data);

?>