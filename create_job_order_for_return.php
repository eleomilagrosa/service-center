<?php  
	include("function.php");

	$job_order_id = addslashes($_POST['job_order_id']);
	$shipping_no = addslashes($_POST['shipping_no']);
	$shipping_note = addslashes($_POST['shipping_note']);
	$account_id = addslashes($_POST['account_id']);
	$repair_status = addslashes($_POST['repair_status']);
		
	$job_order_for_return_id = create_job_order_for_return($job_order_id, $shipping_no, $shipping_note, $account_id);

	if($job_order_for_return_id){
		update_job_order_status($job_order_id, 5, $repair_status);
		
		$job_orders = get_job_order_data($job_order_id);
		$response['success'] = $job_orders['success'];
		$response['message'] = $job_orders['message'] . $job_order_id;
		if($response['success'] == 1){
			$response['job_orders'] = $job_orders['job_orders'];
		}
	}else{
		$response['success'] = 0;
		$response['message'] = "Fail Insert ID" . mysqli_error($GLOBALS['db']) . $job_order_id;
	}

	$data['data'] = json_encode($response);
	echo json_encode($data);

?>