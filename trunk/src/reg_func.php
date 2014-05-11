<?php

function getFaculties() {
    $query = "select faculty_name from faculty";
    $result = mysql_query($query);
    while ($faculty = mysql_fetch_array($result)) {
        $value = $faculty[0];
        echo '<option value="' . $value . '">
                Faculty of ' . $value . '</option>';
    }
}

if (isset($_GET['func']) && $_GET['func'] == "drop_1") {
    drop_1($_GET['drop_var'], $_GET['course_var']);
}

if (isset($_GET['load_courses']) && $_GET['load_courses'] == "load_courses") {
    load_courses($_GET['drop_var'], $_GET['course_var']);
}

function load_courses($drop_var, $course_var) {
    include_once('connection.php');
    $query = "select faculty_id from faculty where faculty_name='$drop_var'";
    $result = mysql_query($query);
    if ($result) {
        $value = mysql_fetch_assoc($result);
        $faculty_id = $value['faculty_id'];
        $query = "SELECT `course`.* FROM `course` JOIN `course_faculty` ON `course`.`course_id` = `course_faculty`.`course_id`
            WHERE `course_faculty`.`faculty_id` = $faculty_id and `course`.`course` like  '%$course_var%'";
        $result = mysql_query($query);
        $row_count = mysql_num_rows($result);
        $num = $row_count % 3 == 0 ? $row_count / 3 : (int) ($row_count / 3) + 1;
        $count = 0;
        $c = 1;
        echo '<table>';
        while ($drop_2 = mysql_fetch_array($result)) {
            $val = $drop_2[1];
            if ($count == 0) {
                echo '<tr>';
                echo '<td>';
                echo '<input type="checkbox" name="courses[]" value="' . $val . '"/>';
                echo '<label style="align: center;" for="courses">' . $val . '</label>';
                echo '</td>';
            } else {
                echo '<td>';
                echo '<input type="checkbox" name="courses[]" value="' . $val . '"/>';
                echo '<label style="align: center;" for="courses">' . $val . '</label>';
                echo '</td>';
            }
            $count++;
            if ($count % 3 == 0) {
                $count = 0;
                echo '</tr>';
            }
            $c++;
        }

        echo '</table>';
    }
}

function drop_1($drop_var, $course_var) {
    include_once('connection.php');
    $query = "select faculty_id from faculty where faculty_name='$drop_var'";
    $result = mysql_query($query);
    if ($result) {
        $value = mysql_fetch_assoc($result);
        $faculty_id = $value['faculty_id'];
        $query = "SELECT `group`.* FROM `group` JOIN `group_faculty` ON `group`.`group_id` = `group_faculty`.`group_id`
            WHERE `group_faculty`.`faculty_id` = $faculty_id and `group`.`group_name` like  '$course_var%'";
        $result = mysql_query($query);
        $count = 0;
        echo '<select name="group" id="group" size="0">';
        while ($drop_2 = mysql_fetch_array($result)) {
            $val = $drop_2['group_name'];
            if ($count == 0) {
                echo '<option selected value="' . $val . '">' . $val . '</option>';
            } else {
                echo '<option value="' . $val . '">' . $val . '</option>';
            }
            $count++;
        }
        echo '</select> ';
    }
}
?>