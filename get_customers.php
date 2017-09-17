<?php  
	include("function.php");	

	if($_GET['station_id'] == 0){
		$customers = get_customers();	
	}else{
		$customers = get_customers_by_station_id($_GET['station_id']);	
	}
	
	if($customers->num_rows == 0){
		$response['success'] = 0;
		$response['message'] = "Failed to retrieve";
	}else{
		$response['customers'] = array();
		while($r = $customers->fetch_assoc()){
			array_push($response['customers'], $r);
		}
		$response['success'] = 1;
	}

	$data['data'] = json_encode($response);
	echo json_encode($data);

?>