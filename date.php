<?php  
	$response['original_date'] = date("Y-m-d H:i:s");
	$data["data"] = json_encode($response);
	echo json_encode($data);
?>