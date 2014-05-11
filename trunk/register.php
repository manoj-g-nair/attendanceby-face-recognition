
<?php
session_start();

unset($_SESSION['fname']);
unset($_SESSION['lname']);
unset($_SESSION['stud_id']);
unset($_SESSION['drop_1']);
unset($_SESSION['group']);
unset($_SESSION['courses']);
include('src/connection.php');
include('src/reg_func.php');
?>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Student Registration Form</title>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3/jquery.min.js"></script>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js "> </script>
        <script type="text/javascript" src="js/jquery-1.3.2.js"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                $('#Loading').hide();
            });

            function check_username(){

                var id = $("#stud_id").val();
                if(id.length == 5){
                    $('#Loading').show();
                    $.post("src/check_id.php", {
                        stud_id: $('#stud_id').val()
                    }, function(response){
                        $('#Info').fadeOut();
                        $('#Loading').hide();
                        setTimeout("finishAjax('Info', '"+escape(response)+"')", 400);
                    });
                    return false;
                }
            }

            $(document).ready(function() {
                $('#wait_1').hide();
                $('#drop_1').change(function(){
                    $('#course').change(function() {
                        $('#wait_1').show();
                        $('#result_1').hide();
                        $.get("src/reg_func.php", {
                            func: "drop_1",
                            drop_var: $('#drop_1').val(),
                            course_var: $('#course').val()
                        }, function(response){
                            $('#result_1').fadeOut();
                            setTimeout("finishAjax('result_1', '"+escape(response)+"')", 400);
                        });
                        return false;
  
                    });
                });
            });

            $(document).ready(function() {
                $('#wait_1').hide();
                $('#course').change(function(){
                    $('#drop_1').change(function() {
                        $('#wait_1').show();
                        $('#result_1').hide();
                        $.get("src/reg_func.php", {
                            func: "drop_1",
                            drop_var: $('#drop_1').val(),
                            course_var: $('#course').val()
                        }, function(response){
                            $('#result_1').fadeOut();
                            setTimeout("finishAjax('result_1', '"+escape(response)+"')", 400);
                        });
                        return false;

                    });
                });
            });
            $(document).ready(function() {
                $('#wait_2').hide();
                $('#drop_1').change(function(){
                    $('#course').change(function() {
                        $('#wait_2').show();
                        $('#result_2').hide();
                        $.get("src/reg_func.php", {
                            load_courses: "load_courses",
                            drop_var: $('#drop_1').val(),
                            course_var: $('#course').val()
                        }, function(response){
                            $('#result_2').fadeOut();
                            setTimeout("finishAjax('result_2', '"+escape(response)+"')", 400);
                        });
                        return false;

                    });
                });
            });

            $(document).ready(function() {
                $('#wait_2').hide();
                $('#course').change(function(){
                    $('#drop_1').change(function() {
                        $('#wait_2').show();
                        $('#result_2').hide();
                        $.get("src/reg_func.php", {
                            load_courses: "load_courses",
                            drop_var: $('#drop_1').val(),
                            course_var: $('#course').val()
                        }, function(response){
                            $('#result_2').fadeOut();
                            setTimeout("finishAjax('result_2', '"+escape(response)+"')", 400);
                        });
                        return false;

                    });
                });
            });

            function finishAjax(id, response) {
                $('#wait_1').hide();
                $('#wait_2').hide();
                $('#'+id).html(unescape(response));
                $('#'+id).fadeIn();
            }
        </script>
        <link rel="stylesheet" href="css/style.css">
    </head>
    <body style="">
        <div id="main">
            <div id="header">
                <div id="logo">
                    <div id="logo_text">
                        <h1><a href="index.php">Attendance by <span class="logo_colour">Face Recognition</span></a></h1>
                    </div>
                </div>
                <div id="menubar">
                    <ul id="menu">
                        <li><a href="index.php">Home</a></li>
                        <li><a href="login.php">Instructor's Login</a></li>
                        <li  class="selected"><a href="register.php">Register</a></li>
                    </ul>
                </div>
            </div>
            <div id="content_header"></div>
            <div id="site_content">
                <div id="content">
                    <?php
                    if (@$_GET['registered'] == 'true') {
                        echo '<h1 style="color:green;">You have registered successfully.<h1/>';
                    }
                    ?>
                    <form name="reg" method="post" action="src/reg_exec.php" >
                        <div class="form_settings">
                            <h1>Student Registration</h1>
                            <p>
                                <span>ID:</span>
                                <input type="text" name="stud_id" id="stud_id" onblur="return check_username();"/>
                                <span id="Loading"><img src="images/ajax-loader.gif" alt="" /></span>
                                <div id="Info"></div>
                            </p>
                            <p>
                                <span>First Name:</span>
                                <input type="text" name="fname" id="fname" />
                            </p>
                            <p>
                                <span>Last Name:</span>
                                <input type="text" name="lname" id="fname" />
                            </p>
                            <p>
                                <span>Faculty:</span>
                                <select size="0" name="drop_1" id="drop_1">
                                    <option value="" selected="selected" disabled="disabled">Select a Faculty</option>
                                    <?php getFaculties(); ?>
                                </select>
                            </p>
                            <p>
                                <span>Course:</span>
                                <select size="0" name="course" id="course">
                                    <option value="" selected="selected" disabled="disabled">Select a Course</option>
                                    <option value="1">1</option>
                                    <option value="2">2</option>
                                    <option value="3">3</option>
                                    <option value="4">4</option>
                                </select>
                            </p>
                            <div id="groups" style="height: 60px">
                                <p>
                                    <span style="margin-top: 5px;">Group:</span>
                                    <span id="wait_1" style="display: none; height: 100px;">
                                        <img alt="Please Wait" src="images/ajax-loader.gif"/>
                                    </span>
                                    <span id="result_1" style="display: none;"></span>
                                </p>
                            </div>
                            <div id="loaded_courses" style="height: 300px;">
                                <p>
                                    <span> Courses: </span><br/>
                                    <span id="wait_2" style="display: none;">
                                        <img alt="Please Wait" src="images/ajax-loader.gif"/>
                                    </span>
                                    <span id="result_2" style="display: none;"></span>
                                </p>

                            </div>
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
                            <p style="padding-top: 5px;">
                                <span>&nbsp;</span>
                                <input class="submit" type="submit" name="register_submit" value="submit" />
                            </p>

                        </div>
                    </form>
                </div>
            </div>
            <div id="content_footer"></div>
            <div id="footer">
            </div>
            <p>&nbsp;</p>
        </div>

    </body>
</html>
