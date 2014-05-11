<?php

session_start();
require_once('connection.php');

function clean($str) {
    $str = @trim($str);
    if (get_magic_quotes_gpc ()) {
        $str = stripslashes($str);
    }
    return mysql_real_escape_string($str);
}

$course = clean(isset($_POST['course']) ? $_POST['course'] : false);
$group = clean(isset($_POST['group']) ? $_POST['group'] : false);
$fromDate = isset($_POST['FromDate']) ? $_POST['FromDate'] : false;
$toDate = clean(isset($_POST['ToDate']) ? $_POST['ToDate'] : false);

$errmsg_arr = array();

$errflag = false;


if ($course == '') {
    $errmsg_arr[] = 'Course not selected';
    $errflag = true;
}

if ($group == '') {
    $errmsg_arr[] = 'Group not selected';
    $errflag = true;
}

if ($fromDate == '') {
    $errmsg_arr[] = 'Enter start date';
    $errflag = true;
}

if ($toDate == '') {
    $errmsg_arr[] = "Enter end date";
    $errflag = true;
}

if ($fromDate > $toDate) {
    $errmsg_arr[] = "Start date must less than end date";
    $errflag = true;
}

if ($errflag) {
    $_SESSION['ERRMSG_ARR'] = $errmsg_arr;
    session_write_close();
    header("location: ../home.php?err=true");
    exit();
}

$students = array();
$date = array();

$query = "SELECT * FROM `group` WHERE group_name='$group'";
$result = mysql_query($query) or die(mysql_error());
$g = mysql_fetch_assoc($result);
$group_id = $g['group_id'];

$sql = "SELECT * FROM `course` WHERE course_name='$course'";
$res = mysql_query($sql) or die(mysql_error());
if ($res) {
    $val = mysql_fetch_assoc($res);
    $course_id = $val['COURSE_ID'];
    $query_date = "SELECT DISTINCT date FROM `attendance` where course_id='$course_id' and date>='$fromDate' and date<='$toDate' ORDER BY date asc";
    $res_date = mysql_query($query_date);
    $date = array();
    if ($res_date) {
        while ($val = mysql_fetch_array($res_date)) {
            $date[] = $val['date'];
        }
    }
    $_SESSION['DATES'] = $date;
    $query = "SELECT DISTINCT `student_id` FROM `attendance` WHERE `course_id`=$course_id and date>='$fromDate' and date<='$toDate' ORDER BY date asc";
    $result = mysql_query($query);
    if ($result) {
        while ($value = mysql_fetch_array($result)) {
            $student_id = $value['student_id'];
            $q = "SELECT * FROM `student` WHERE `student_id`=$student_id";
            $res_stud = mysql_query($q);
            if ($res_stud) {
                $val_stud = mysql_fetch_assoc($res_stud);
                $sgroup_id = $val_stud['GROUP_ID'];
                if ($sgroup_id == $group_id) {
                    $students[] = $val_stud['FIRST_NAME'] . " " . $val_stud['LAST_NAME'];
                }
            }
        }
    }
    sort($students);
    $_SESSION['STUDENTS'] = $students;
    $_SESSION['COURSE_ID'] = $course_id;
    $_SESSION['FROM_DATE'] = $fromDate;
    $_SESSION['TO_DATE'] = $toDate;
    session_write_close();
    header("location: ../home.php?show=true");
}
?>
