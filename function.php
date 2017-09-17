<?php
	require_once 'connection.php';

	$GLOBALS['db'] = mysqli_connect($GLOBALS['host'],$GLOBALS['username'],$GLOBALS['password'],$GLOBALS['database'],$GLOBALS['port']);
	$GLOBALS['db']->query("SET NAMES 'UTF8'");

	$headers = apache_request_headers();
	// $id = "2";
	// $secret = "3X3dxxH7tLs5ZcP3dIb18U0NQaCAd9tbBc80XANz";

	$id = $headers['id'];
	$secret = $headers['secret'];

	$client = mysqli_query($GLOBALS['db'],"Select * from oauth_clients where id = $id and secret = '$secret'");
	if($client->num_rows == 0){ 
	    header("HTTP/1.1 401 Unauthorized");
		exit;
	}

	function check_if_username_exist($uname){
		return mysqli_query($GLOBALS['db'],"Select * from accounts where username = '$uname'");
	}

	function create_account($first_name,$last_name,$username,$password,$email,$phone_no,$account_type_id,$station_id){
		$result = mysqli_query($GLOBALS['db'],"insert into accounts (`first_name`,`last_name`,`username`,`password`,`email`,`phone_no`,`account_type_id`,`station_id`,`date_created`) values ('$first_name','$last_name','$username',md5('$password'),'$email','$phone_no','$account_type_id','$station_id',now())");
		if($result){
			return mysqli_insert_id($GLOBALS['db']);
		}else{
			return $result;
		}
	}

	function update_account($id,$first_name,$last_name,$email,$phone_no,$account_type_id,$station_id){		
		mysqli_query($GLOBALS['db'],"update accounts set first_name = '$first_name',last_name = '$last_name',email = '$email',phone_no = '$phone_no' ,account_type_id = '$account_type_id' ,station_id = '$station_id' where id = $id");
	}

	function update_account_status_id($id,$is_main_branch){
		mysqli_query($GLOBALS['db'],"update accounts set is_main_branch = '$is_main_branch' where id = $id");
	}
	function approved_account($id,$approved_by){
		mysqli_query($GLOBALS['db'],"update accounts set approved_by = $approved_by, date_approved = NOW() where id = $id");
	}
	function delete_account($id){
		mysqli_query($GLOBALS['db'],"update accounts set is_deleted = 1 where id = $id");
	}

	function get_account_by_id($id){
		return mysqli_query($GLOBALS['db'],"Select * from accounts where id = $id");
	}

	function get_accounts_to_be_approved(){
		return mysqli_query($GLOBALS['db'],"Select * from accounts where date_approved IS NULL");
	}

	function get_accounts_to_be_approved_by_station_id($station_id){
		return mysqli_query($GLOBALS['db'],"Select * from accounts where date_approved IS NULL and station_id = $station_id");
	}

	function get_accounts(){
		return mysqli_query($GLOBALS['db'],"Select * from accounts where date_approved IS NOT NULL and is_main_branch = 0");
	}

	function get_accounts_by_station_id($station_id){
		return mysqli_query($GLOBALS['db'],"Select * from accounts where date_approved IS NOT NULL and is_main_branch = 0 and and station_id = $station_id");
	}

	function get_admins(){
		return mysqli_query($GLOBALS['db'],"Select * from accounts where date_approved IS NOT NULL and is_main_branch = 1");
	}

	function get_stations(){
		return mysqli_query($GLOBALS['db'],"Select * from stations");
	}
	function create_station($station_name,$station_prefix,$station_address,$station_number,$station_description){
		$query = "insert into stations(`station_name`,`station_prefix`,`station_address`,`station_number`,`station_description`,`date_created`) values('$station_name','$station_prefix','$station_address','$station_number','$station_description',now())";
		$result = mysqli_query($GLOBALS['db'],$query);
		if($result){
			return mysqli_insert_id($GLOBALS['db']);
		}else{
			return $result;
		}
	}
	function update_station($id,$station_name,$station_prefix,$station_address,$station_number,$station_description){
		mysqli_query($GLOBALS['db'],"update stations set station_name = '$station_name',station_prefix = '$station_prefix',station_address = '$station_address',station_number = '$station_number',station_description = '$station_description' where id = $id");
	}
	function delete_station($id){
		 mysqli_query($GLOBALS['db'],"update stations set is_deleted = 1 where id = $id");
	}
	function get_station_by_id($id){
		return mysqli_query($GLOBALS['db'],"Select * from stations where id = $id");
	}

	function get_customers(){
		return mysqli_query($GLOBALS['db'],"Select * from customers");
	}
	function get_customers_by_station_id($station_id){
		return mysqli_query($GLOBALS['db'],"Select * from customers where station_id = $station_id");
	}

	function create_customers($first_name,$last_name,$address,$phone_no,$email,$account_id,$station_id){
		$result = mysqli_query($GLOBALS['db'],"insert into customers(`first_name`,`last_name`,`address`,`phone_no`,`email`,`account_id`,`station_id`,`date_created`) values('$first_name','$last_name','$address','$phone_no','$email','$account_id',now(),'$station_id')");
		if($result){
			return mysqli_insert_id($GLOBALS['db']);
		}else{
			return $result;
		}
	}
	function update_customers($id,$first_name,$last_name,$address,$phone_no,$email,$account_id){
		$result = mysqli_query($GLOBALS['db'],"update customers set first_name = '$first_name',last_name = '$last_name',address = '$address',phone_no = '$phone_no',email = '$email',account_id = '$account_id' where id = $id");
	}

	function delete_customer($id){
		$result = mysqli_query($GLOBALS['db'],"update customers set is_deleted = 1 where id = $id");
	}
	function get_customer_by_id($id){
		return mysqli_query($GLOBALS['db'],"Select * from customers where id = $id");
	}

	function change_password($id,$password){
 		mysqli_query($GLOBALS['db'],"update acco set `password` = md5('$password') where id = $id");
	}

	function login_credentials($username, $password){
		return mysqli_query($GLOBALS['db'],"Select * from accounts where username = '$username' and password = md5('$password') and is_deleted = 0");
	}

	function create_job_order($id,$unit,$model, $date_of_purchased, $dealer, $serial_number, $warranty_label, $complaint, $customer_id, $account_id,$status_id,$station_id){
		$result = mysqli_query($GLOBALS['db'],"insert into job_orders
			(`id`,
			`unit`,
			`model`,
			`date_of_purchased`,
			`dealer`,
			`serial_number`,
			`warranty_label`,
			`complaint`,
			`customer_id`,
			`account_id`,
			`status_id`,
			`station_id`,
			`date_created`)
			values 
			('$id',
			'$unit',
			'$model',
			'$date_of_purchased',
			'$dealer',
			'$serial_number',
			'$warranty_label',
			'$complaint',
			'$customer_id',
			'$account_id',
			'$status_id',
			'$station_id',
			now())");
		return $result;
	}

	function create_job_order_diagnosis($job_order_id, $diagnosis, $account_id){
		$result = mysqli_query($GLOBALS['db'],"insert into job_order_diagnosis
			(`job_order_id`,
			`diagnosis`,
			`account_id`,
			`date_created`)
			values 
			('$job_order_id',
			'$diagnosis',
			'$account_id',
			now())");
		return $result;
	}

	function update_job_order_status($job_order_id,$status_id,$repair_status){
			$result = mysqli_query($GLOBALS['db'],"Update job_orders
				set `status_id` = '$status_id' , repair_status = '$repair_status' where id = '$job_order_id'");
	}

	function create_job_order_shipping($job_order_id, $shipping_no, $shipping_note, $account_id){
			$result = mysqli_query($GLOBALS['db'],"insert into job_order_shipping
			(`job_order_id`,
			`shipping_no`,
			`shipping_note`,
			`account_id`,
			`date_created`)
			values 
			('$job_order_id',
			'$shipping_no',
			'$shipping_note',
			'$account_id',
			now())");
		return get_job_order_diagnosis_by_id($job_order_id);
	}


	function create_job_order_repair_status($job_order_id, $repair_note, $repair_status, $account_id){
			$result = mysqli_query($GLOBALS['db'],"insert into job_order_repair_status
			(`job_order_id`,
			`repair_note`,
			`repair_status`,
			`account_id`,
			`date_created`)
			values 
			('$job_order_id',
			'$repair_note',
			'$repair_status',
			'$account_id',
			now())");
		return $result;
	}

	function create_job_order_for_return($job_order_id, $shipping_no, $shipping_note, $account_id){
			$result = mysqli_query($GLOBALS['db'],"insert into job_order_for_return
			(`job_order_id`,
			`shipping_no`,
			`shipping_note`,
			`account_id`,
			`date_created`)
			values 
			('$job_order_id',
			'$shipping_no',
			'$shipping_note',
			'$account_id',
			now())");
		return get_job_order_diagnosis_by_id($job_order_id);
	}

	function get_account_data($account_id){
		$response['message'] = "";
		$accounts = get_account_by_id($account_id);
		if($accounts->num_rows == 0){
			$response['success'] = 0;
			$response['message'] = "Failed to retrieve id";
		}else{
			$response['accounts'] = array();
			while($r = $accounts->fetch_assoc()){
				$stations = get_station_by_id($r['station_id']);
				while($per_stations = $stations->fetch_assoc()){
					$r['station'] = $per_stations; 
				}
				array_push($response['accounts'], $r);
			}
			$response['success'] = 1;
		}
		return $response;
	}

	function get_job_order_data($id){
		$response['message'] = "";
		$job_orders = get_job_order_by_id($id);
		if($job_orders->num_rows == 0){
			$response['success'] = 0;
			$response['message'] = "Failed to retrieve id";
		}else{
			$response['job_orders'] = array();
			while($job_orders_rows = $job_orders->fetch_assoc()){
				
				$diagnosis = get_job_order_diagnosis_by_id($job_orders_rows['id']);
				if($diagnosis->num_rows > 0){
					while($diagnosis_rows = $diagnosis->fetch_assoc()){
						$job_orders_rows['jobOrderDiagnosis'] = $diagnosis_rows;
					}
				}

				$shipping = get_job_order_shipping_by_id($job_orders_rows['id']);
				if($shipping->num_rows > 0){
					while($shipping_rows = $shipping->fetch_assoc()){
						$job_orders_rows['jobOrderShipping'] = $shipping_rows;
					}
				}

				$repair_status = get_job_order_repair_status_by_id($job_orders_rows['id']);
				if($repair_status->num_rows > 0){
					while($repair_status_rows = $repair_status->fetch_assoc()){
						$job_orders_rows['jobOrderRepairStatus'] = $repair_status_rows;
					}
				}

				$for_return = get_job_order_for_return($job_orders_rows['id']);
				if($for_return->num_rows > 0){
					while($for_return_rows = $for_return->fetch_assoc()){
						$job_orders_rows['jobOrderForReturn'] = $for_return_rows;
					}
				}

				$customer = get_customer_by_id($job_orders_rows['customer_id']);
				if($customer->num_rows > 0){
					while($customer_rows = $customer->fetch_assoc()){
						$job_orders_rows['customer'] = $customer_rows;
					}
				}

				$job_orders_rows['jobOrderImages'] = array();
				$images = get_images_by_job_order($job_orders_rows['id']);
				if($images->num_rows > 0){
					while($images_rows = $images->fetch_assoc()){
						array_push($job_orders_rows['jobOrderImages'], $images_rows);
					}
				}

				array_push($response['job_orders'], $job_orders_rows);
			}
			$response['success'] = 1;
		}
		return $response;
	}

	function get_job_order_by_id($id){
		return mysqli_query($GLOBALS['db'],"Select * from job_orders where id = '$id'");
	}

	function get_job_order_by_show_open_orders(){
		return mysqli_query($GLOBALS['db'],"Select * from job_orders where status_id != '8'");
	}

	function get_job_order_by_show_open_orders_by_station_id($station_id){
		return mysqli_query($GLOBALS['db'],"Select * from job_orders where status_id != '8' and station_id = $station_id");
	}

	function get_job_order_by_history(){
		return mysqli_query($GLOBALS['db'],"Select * from job_orders where status_id = '8'");
	}

	function get_job_order_by_history_by_station_id($station_id){
		return mysqli_query($GLOBALS['db'],"Select * from job_orders where status_id = '8' and station_id = $station_id");
	}

	function get_job_order_by_customer_id($customer_id){
		return mysqli_query($GLOBALS['db'],"Select * from job_orders where customer_id = '$customer_id'");
	}

	function get_job_order_by_customer_id_by_station_id($customer_id, $station_id){
		return mysqli_query($GLOBALS['db'],"Select * from job_orders where customer_id = '$customer_id' and station_id = $station_id");
	}

	function get_job_order_diagnosis_by_id($id){
		return mysqli_query($GLOBALS['db'],"Select * from job_order_diagnosis where job_order_id = '$id'");
	}

	function get_job_order_shipping_by_id($id){
		return mysqli_query($GLOBALS['db'],"Select * from job_order_shipping where job_order_id = '$id'");
	}

	function get_job_order_repair_status_by_id($id){
		return mysqli_query($GLOBALS['db'],"Select * from job_order_repair_status where job_order_id = '$id'");
	}

	function get_job_order_for_return($id){
		return mysqli_query($GLOBALS['db'],"Select * from job_order_for_return where job_order_id = '$id'");
	}
	function get_images_by_job_order($id){
		return mysqli_query($GLOBALS['db'],"Select * from job_order_images where job_order_id = '$id'");
	}

	function add_job_order_image($image,$label,$job_order_status_id,$job_order_id){
		$result = mysqli_query($GLOBALS['db'],"insert into job_order_images(`image`,`label`,`job_order_status_id`,`job_order_id`,`date_created`) values ('$image','$label','$job_order_status_id','$job_order_id',now())");
		if($result){
			return mysqli_insert_id($GLOBALS['db']);
		}else{
			return $result;
		}
	}	
	function get_image_by_id($id){
		return mysqli_query($GLOBALS['db'],"Select * from job_order_images where id = '$id'");
	}
?>