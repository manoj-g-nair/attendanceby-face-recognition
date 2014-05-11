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

$id = clean($_POST['stud_id']);
$fname = clean($_POST['fname']);
$lname = clean($_POST['lname']);
$faculty = clean(isset($_POST['drop_1']) ? $_POST['drop_1'] : false);
$group = clean(isset($_POST['group']) ? $_POST['group'] : false);
$courses = isset($_POST['courses']) ? $_POST['courses'] : false;
$course = clean(isset($_POST['course']) ? $_POST['course'] : false);

$errmsg_arr = array();

$errflag = false;

if ($id == '') {
    $errmsg_arr[] = 'ID missing';
    $errflag = true;
} else {
    if (strlen($id) != 5 || !preg_match("/^[0-9]+$/", $id)) {
        $errmsg_arr[] = 'ID must contain only numbers and have length of 5';
        $errflag = true;
    }
}
if ($fname == '') {
    $errmsg_arr[] = 'First name missing';
    $errflag = true;
}

if ($lname == '') {
    $errmsg_arr[] = 'Last name missing';
    $errflag = true;
}

if ($faculty == '') {
    $errmsg_arr[] = 'Faculty not selected';
    $errflag = true;
}

if ($course == '') {
    $errmsg_arr[] = 'Course not selected';
    $errflag = true;
}

if ($group == '') {
    $errmsg_arr[] = 'Group not selected';
    $errflag = true;
}

if (!$courses) {
    $errmsg_arr[] = "Take at least one course";
    $errflag = true;
}

if ($errflag) {
    $_SESSION['ERRMSG_ARR'] = $errmsg_arr;
    session_write_close();
    header("location: ../register.php/err=true");
    exit();
}


$query = "select faculty_id from faculty where faculty_name='$faculty'";
$result = mysql_query($query) or die(mysql_error());
$f = mysql_fetch_assoc($result);
$faculty_id = $f['faculty_id'];
$query = "SELECT * FROM `group` WHERE group_name='$group'";
$result = mysql_query($query) or die(mysql_error());
$g = mysql_fetch_assoc($result);
$group_id = $g['group_id'];

mysql_query("INSERT INTO student(student_id, first_name, last_name, faculty_id, group_id)
  VALUES($id, '$fname', '$lname', $faculty_id, $group_id)");
foreach ($courses as $c) {
    $query = "select course_id from course where course_name='$c'";
    $result = mysql_query($query);
    $ss = mysql_fetch_assoc($result);
    $sss = $ss['course_id'];
    mysql_query("insert into student_course(student_id, course_id) values($id, $sss)");
}
session_write_close();
header("location: ../register.php?registered=true");
?>
