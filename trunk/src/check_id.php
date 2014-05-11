<?php

include('connection.php');
if ($_REQUEST) {
    $id = $_REQUEST['stud_id'];
    $query = "select * from student where student_id = $id";
    $results = mysql_query($query) or die('ok');

    if (mysql_num_rows(@$results) > 0) { // not available
        echo '<div id="Error"></div>';
    } else {
        echo '<div id="Success"></div>';
    }
}
?>