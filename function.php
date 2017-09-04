<?php
	require_once 'connection.php';

	function getDatabase(){

		$db = mysqli_connect($GLOBALS['host'],$GLOBALS['username'],$GLOBALS['password'],$GLOBALS['database'],$GLOBALS['port']);
		$db->query("SET NAMES 'UTF8'");

		return $db;
	}

	function get_revenue_settings($name){
		return mysqli_query(getDatabase(),"CALL kmobile_get_revenues_by_name('$name')");
	}

?>