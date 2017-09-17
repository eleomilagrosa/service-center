<?php  
	include("function.php");

	$job_order_id = addslashes($_POST['job_order_id']);
	$status_id = addslashes($_POST['status_id']);
	$repair_status = addslashes($_POST['repair_status']);
		
	$job_orders = update_job_order_status($job_order_id, $status_id, $repair_status);

	if(mysqli_affected_rows($GLOBALS['db']) > 0 ){
		$job_orders = get_job_order_data($job_order_id);
		$response['success'] = $job_orders['success'];
		$response['message'] = $job_orders['message'] . $job_order_id;
		if($response['success'] == 1){
			$response['job_orders'] = $job_orders['job_orders'];
		}
	}else{		
		$response['success'] = 0;
		$response['message'] = "Fail Insert ID" . mysqli_error($GLOBALS['db']);	
	}

	$data['data'] = json_encode($response);
	echo json_encode($data);
?>