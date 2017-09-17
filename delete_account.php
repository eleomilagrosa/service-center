<?php  
	include("function.php");
	$id = addslashes($_POST['id']);
		
	delete_account($id);

	if(mysqli_affected_rows($GLOBALS['db']) > 0 ){
		$result = get_account_data($id);
		$response['success'] = $result['success'];
		$response['message'] = $result['message'];
		if($result['success'] == 1){
			$response['accounts'] = $result['accounts'];
		}
	}else{
		$response['success'] = 0;		
	}

	$data['data'] = json_encode($response);
	echo json_encode($data);

?>