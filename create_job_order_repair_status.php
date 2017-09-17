<?php  
	include("function.php");

	$job_order_id = addslashes($_POST['job_order_id']);
	$repair_note = addslashes($_POST['repair_note']);
	$repair_status = addslashes($_POST['repair_status']);
	$account_id = addslashes($_POST['account_id']);
		
	$job_order_repair_status_id = create_job_order_repair_status($job_order_id, $repair_note, $repair_status, $account_id);

	if($job_order_repair_status_id){
		update_job_order_status($job_order_id, 4, $repair_status);
		
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