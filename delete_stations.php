<?php  
	include("function.php");
	$id = addslashes($_POST['id']);
		
	delete_station($id);

	if(mysqli_affected_rows($GLOBALS['db']) > 0 ){
		$stations = get_station_by_id($id);
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
	}

	$data['data'] = json_encode($response);
	echo json_encode($data);

?>