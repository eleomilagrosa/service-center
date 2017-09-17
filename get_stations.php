<?php  
	include("function.php");	

	$stations = get_stations();	
	
	if($stations->num_rows == 0){
		$response['success'] = 0;
		$response['message'] = "Failed to retrieve";
	}else{
		$response['stations'] = array();
		while($r = $stations->fetch_assoc()){
			array_push($response['stations'], $r);
		}
		$response['success'] = 1;
	}

	$data['data'] = json_encode($response);
	echo json_encode($data);

?>