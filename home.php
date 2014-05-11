<?php
session_start();
if (!isset($_SESSION['uid'])) {
    header("location: login.php");
    exit;
}
include('src/connection.php');
include('src/home_func.php');
?>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <title>Instructor's home page</title>
        <link rel="stylesheet" href="css/style.css">
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3/jquery.min.js"></script>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js "> </script>
        <script type="text/javascript" src="js/jquery-1.3.2.js"></script>
        <script type="text/javascript">

            window.onload = function () {
                if (typeof history.pushState === "function") {
                    history.pushState("jibberish", null, null);
                    window.onpopstate = function () {
                        history.pushState('newjibberish', null, null);
                    };
                }
                else {
                    var ignoreHashChange = true;
                    window.onhashchange = function () {
                        if (!ignoreHashChange) {
                            ignoreHashChange = true;
                            window.location.hash = Math.random();
                        }
                        else {
                            ignoreHashChange = false;
                        }
                    };
                }
            }
            $(document).ready(function() {
                $('#wait_1').hide();
                $('#course').change(function(){
                    $('#wait_1').show();
                    $('#result_1').hide();
                    $.get("src/home_func.php", {
                        func: "groups",
                        course_var: $('#course').val()
                    }, function(response){
                        $('#result_1').fadeOut();
                        setTimeout("finishAjax('result_1', '"+escape(response)+"')", 400);
                    });
                    return false;

                });
            });

            function finishAjax(id, response) {
                $('#wait_1').hide();
                $('#wait_2').hide();
                $('#'+id).html(unescape(response));
                $('#'+id).fadeIn();
            }

            $( function() {
                $('td').click( function() {
                    $(this).toggleClass("red-cell");
                } );
            } );

        </script>
    </head>
    <body>
        <div id="main">
            <div id="header">
                <div id="logo">
                    <div id="logo_text">
                        <h1><a href="index.php">Attendance by <span class="logo_colour">Face Recognition</span></a></h1>
                    </div>
                </div>
                <div id="menubar">
                    <ul id="menu">
                        <?php

                        function clean($str) {
                            $str = @trim($str);
                            if (get_magic_quotes_gpc ()) {
                                $str = stripslashes($str);
                            }
                            return mysql_real_escape_string($str);
                        }

                        echo '<li id="submenu"><a href="login.php">Logout</a>
                                    </li>';
                        ?>
                    </ul>
                </div>
            </div>
            <div id="content_header"></div>
            <div id="site_content">
                <div id="content">
                    <form name="loginform" action="src/home_exec.php" method="post">
                        <div class="form_settings">
                            <h1>Instructor's page</h1>
                            <p>
                                <span>Course:</span>
                                <select size="0" name="course" id="course">
                                    <option value="" selected="selected" disabled="disabled">Select a Course</option>
                                    <?php
                                    $username = $_SESSION['username'];
                                    $query = "select * from instructor where username='$username'";
                                    $result = mysql_query($query);
                                    if ($result) {
                                        while ($value = mysql_fetch_array($result)) {
                                            $ins_id = $value['INSTRUCTOR_ID'];
                                            $query = "SELECT `course`.* FROM `course` JOIN `instructor_course` ON `course`.`course_id` = `instructor_course`.`course_id`
                                              WHERE `instructor_course`.`instructor_id` = $ins_id";
                                            $res = mysql_query($query);
                                            while ($course = mysql_fetch_array($res)) {
                                                $val = $course[1];
                                                echo '<option value="' . $val . '">' . $val . '</option>';
                                            }
                                        }
                                    }
                                    ?>
                                </select>
                            </p>
                            <div style="height: 70px;">
                                <p>
                                    <span style="margin-top: 13px;">Group:</span>
                                    <span id="wait_1" style="display: none; height: 100px;">
                                        <img alt="Please Wait" src="images/ajax-loader.gif"/>
                                    </span>
                                    <span id="result_1" style="display: none;"></span>
                                </p>
                            </div>
                            <div class="right">
                                <p>
                                    <span>From:</span>
                                    <input type="date" id="FromDate" name="FromDate"
                                           value="" class="date" />

                                </p>
                                <p>
                                    <span>To:</span>
                                    <input type="date" id="ToDate" name="ToDate"
                                           value="" class="date"/>
                                </p>
                                <div>
                                    <?php
                                    if (isset($_SESSION['ERRMSG_ARR']) && is_array($_SESSION['ERRMSG_ARR'])
                                            && count($_SESSION['ERRMSG_ARR']) > 0) {
                                        echo '<ul class="err">';
                                        foreach ($_SESSION['ERRMSG_ARR'] as $msg) {
                                            echo '<li>', $msg, '</li>';
                                        }
                                        echo '</ul>';
                                        unset($_SESSION['ERRMSG_ARR']);
                                    }
                                    ?>
                                </div>
                                <p style="margin-top: 25px;">
                                    <span>&nbsp;</span>
                                    <input class="submit" type="submit" name="home_submit" value="show" id="show"/>
                                </p>
                                <div id="sheet">
                                    <?php
                                    if (isset($_SESSION['ERRMSG_ARR']) && is_array($_SESSION['ERRMSG_ARR'])
                                            && count($_SESSION['ERRMSG_ARR']) > 0) {
                                        echo '<ul class="err">';
                                        foreach ($_SESSION['ERRMSG_ARR'] as $msg) {
                                            echo '<li>', $msg, '</li>';
                                        }
                                        echo '</ul>';
                                        unset($_SESSION['ERRMSG_ARR']);
                                    }
                                    ?>

                                </div>
                            </div>
                        </div>
                    </form>
                    <div style="margin-top: 20px;">
                        <h2> Attendance Sheet </h2>
                    </div>
                    <div id="view">
                        <?php
                                    if (@$_GET['show'] == 'true') {
                                        echo "<table align=\"letf\" style=\"margin-left: 0px; border: 1px solid black; border-spacing: 0px; ;\" width=\"50\">";
                                        echo "<th style=\"background: #B7B9A7; border: 1px solid black; text-align: center; font-size: 13px\">Student Name/Date</th>";
                                        if (isset($_SESSION['DATES']) && is_array($_SESSION['DATES'])
                                                && count($_SESSION['DATES']) > 0) {
                                            foreach ($_SESSION['DATES'] as $date) {
                                                echo "<th style=\"background: #B7B9A7; border: 1px solid black; text-align: center; font-size: 11px;\">";
                                                echo $date;
                                                echo "</th>";
                                            }
                                        }
                                        if (isset($_SESSION['STUDENTS']) && is_array($_SESSION['STUDENTS'])
                                                && count($_SESSION['STUDENTS']) > 0) {
                                            $course_id = $_SESSION['COURSE_ID'];
                                            $fromDate = $_SESSION['FROM_DATE'];
                                            $toDate = $_SESSION['TO_DATE'];
                                            $count = 0;
                                            foreach ($_SESSION['STUDENTS'] as $student) {
                                                if ($count % 2 == 0) {
                                                    echo "<tr style=\"background: lavender; color: #000; border: 1px solid black; text-align: center; font-size: 13px;\">";
                                                    echo "<td contenteditable=\"true\" style=\"background: lavender; color: #000; border: 1px solid black; text-align: center; font-size: 13px;\">$student</td>";
                                                } else {
                                                    echo "<tr style=\"background: #B7B9A7; border: 1px solid black; text-align: center; font-size: 13px;\">";
                                                    echo "<td contenteditable=\"true\" style=\"background: #B7B9A7; border: 1px solid black; text-align: center; font-size: 13px;\">$student</td>";
                                                }
                                                $info = explode(" ", $student);
                                                $query = "SELECT * FROM `student` WHERE `first_name`='$info[0]' and `last_name`='$info[1]'";
                                                $result = mysql_query($query);
                                                if ($result) {
                                                    $val = mysql_fetch_assoc($result);
                                                    $student_id = $val['STUDENT_ID'];
                                                    foreach ($_SESSION['DATES'] as $date) {
                                                        $q = "SELECT * FROM `attendance` WHERE `student_id`=$student_id and `course_id`=$course_id and date='$date'";
                                                        $res = mysql_query($q);
                                                        if (mysql_num_rows($res) > 0) {
                                                            $stat = mysql_fetch_array($res);
                                                            $status = $stat['status'];
                                                            if ($count % 2 == 0) {
                                                                echo "<td contenteditable=\"true\" style=\"background: lavender; color: #000; border: 1px solid black; text-align: center; font-size: 10px;\">$status</td>";
                                                            } else {
                                                                echo "<td contenteditable=\"true\" style=\"background: #B7B9A7; border: 1px solid black; text-align: center; font-size: 10px;\">$status</td>";
                                                            }
                                                        } else {
                                                            if ($count % 2 == 0) {
                                                                echo "<td style=\"background: lavender; color: #000; border: 1px solid black; text-align: center; font-size: 10px;\">X</td>";
                                                            } else {
                                                                echo "<td style=\"background: #B7B9A7; border: 1px solid black; text-align: center; font-size: 10px;\">X</td>";
                                                            }
                                                        }
                                                    }
                                                }
                                                $count++;
                                                echo "</tr>";
                                            }
                                            unset($_SESSION['STUDENTS']);
                                            unset($_SESSION['DATES']);
                                        }
                                        echo "</table>";
                                    }
                                    echo "<div>";
                                    
                                    echo "<p style=\"margin-top: 25px;\">";
                                    echo "<span>&nbsp;</span>";
                                    if (@$_GET['show'] == 'true') {
                                        echo "<input class=\"submit\" style=\"align: center; font: 100% arial; border: 0; width: 99px; margin: 0 0 0 120px; height: 33px; padding: 0 0 0 0; cursor: pointer; background: #3B3B3B; color: #FFF;\" type=\"submit\" name=\"home_modify\" value=\"modify\" id=\"modify\"/>";
                                    }
                                    echo "</div>";
                        ?>
                    </div>
                </div>


            </div>
        </div>

        <div id="content_footer"></div>
        <div id="footer">
        </div>
        <p>&nbsp;</p>
    </body>
</html>