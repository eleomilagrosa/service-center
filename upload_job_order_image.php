<?php  
	include("function.php");	
		
	$label = addslashes($_POST['label']);
	$job_order_status_id = addslashes($_POST['job_order_status_id']);
	$job_order_id = addslashes($_POST['job_order_id']);	

	$basepath = 'images/jo'. $job_order_id . '/';
	$file_path =  $basepath . basename($_FILES['uploaded_file']['name']);

	if (!file_exists($basepath)) {
    	mkdir($basepath, 0777, true);
	}

    if(move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $file_path)) {
    	$image_id = add_job_order_image($file_path,$label,$job_order_status_id,$job_order_id);
		if($image_id){
			$job_order_images = get_image_by_id($image_id);
			if($job_order_images->num_rows == 0){
				$response['success'] = 0;
				$response['message'] = "Failed to retrieve id";
			}else{
				$response['job_order_images'] = array();
				while($r = $job_order_images->fetch_assoc()){
					array_push($response['job_order_images'], $r);
				}
				$response['success'] = 1;
			}
		}else{
			$response['success'] = 0;
			$response['message'] = "Fail Insert ID" . mysqli_error($GLOBALS['db']);
		}
    } else{
		$response['success'] = 0;
		$response['message'] = "Failed To Upload" . mysqli_error($GLOBALS['db']);
    }




	$data['data'] = json_encode($response);
	echo json_encode($data);

?>