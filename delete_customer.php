<?php  
	include("function.php");
	$id = addslashes($_POST['id']);
		
	delete_customer($id);

	if(mysqli_affected_rows($GLOBALS['db']) > 0 ){
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