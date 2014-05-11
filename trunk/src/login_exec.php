<?php
	session_start();

	require_once('connection.php');

	$errmsg_arr = array();

	$errflag = false;

	function clean($str) {
		$str = @trim($str);
		if(get_magic_quotes_gpc()) {
			$str = stripslashes($str);
		}
		return mysql_real_escape_string($str);
	}

	$username = clean($_POST['username']);
	$password = clean($_POST['password']);

	if($username == '') {
		$errmsg_arr[] = 'Username missing';
		$errflag = true;
	}
	if($password == '') {
		$errmsg_arr[] = 'Password missing';
		$errflag = true;
	}

	if($errflag) {
		$_SESSION['ERRMSG_ARR'] = $errmsg_arr;
		session_write_close();
		header("location: ../login.php?err=true");
		exit();
	}
        $password = md5($password); 
        $query="SELECT * FROM instructor WHERE username='$username' AND password='$password'";
	$result=mysql_query($query);
	if($result) {
		if(mysql_num_rows($result) > 0) {
			session_regenerate_id();
			$member = mysql_fetch_assoc($result);
			$_SESSION['uid'] = $member['INSTRUCTOR_ID'];
			$_SESSION['username'] = $member['USERNAME'];
                 	session_write_close();
			header("location: ../home.php");
			exit();
		}else {
			$errmsg_arr[] = 'user name or password is incorrect';
			$errflag = true;
			if($errflag) {
				$_SESSION['ERRMSG_ARR'] = $errmsg_arr;
		  		session_write_close();
				header("location: ../login.php?err=true");
				exit();
			}
		}
	}else {
		die("Query failed");
	}
?>