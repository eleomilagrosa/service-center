<?php  
	include("function.php");

	$station_name = addslashes($_POST['station_name']);
	$station_prefix = addslashes($_POST['station_prefix']);
	$station_address = addslashes($_POST['station_address']);
	$station_number = addslashes($_POST['station_number']);
	$station_description = addslashes($_POST['station_description']);

	$station_id = create_station($station_name,$station_prefix,$station_address,$station_number,$station_description);

	if($station_id){
		$stations = get_station_by_id($station_id);
		if($stations->num_rows == 0){
			$response['success'] = 0;
			$response['message'] = "Failed to retrieve id";
		}else{
			$response['stations'] = array();
			while($r = $stations->fetch_assoc()){
				array_push($response['stations'], $r);
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