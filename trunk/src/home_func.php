<?php

require_once('connection.php');

if (isset($_GET['func']) && $_GET['func'] == "groups") {
    get_groups($_GET['course_var']);
}

function get_groups($course_var) {
    $query = "select course_id from course where course_name='$course_var'";
    $result = mysql_query($query);
    if ($result) {
        $value = mysql_fetch_assoc($result);
        $course_id = $value['course_id'];
        $query = "SELECT DISTINCT `student`.`group_id` FROM `student` JOIN `student_course` ON `student`.`student_id` = `student_course`.`student_id`
            WHERE `student_course`.`course_id` = $course_id";
        $result = mysql_query($query);
        if ($result) {
            echo '<select name="group" id="group" size="0">';
            $count = 0;
            echo mysql_num_rows($result);
            while ($group_id = mysql_fetch_array($result)) {
                $v = $group_id['group_id'];
                $q = "select * from `group` where group_id=$v";
                $res = mysql_query($q);
                if ($res) {
                    $val = mysql_fetch_assoc($res);
                    $ss = $val['group_name'];
                    if ($count == 0) {
                        echo '<option selected value="' . $ss . '">' . $ss . '</option>';
                    } else {
                        echo '<option value="' . $ss . '">' . $ss . '</option>';
                    }
                }
                $count++;
            }
            echo '</select>';
        } else {
            die("the query is not correct");
        }
    } else {
        die("the query is not correct");
    }
}
?>
