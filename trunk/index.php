<?php
session_start();

unset($_SESSION['uid']);
unset($_SESSION['username']);

?>

<html>
    <head>

        <title>Home</title>
        <link rel="stylesheet" href="css/style.css">
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
                        <!-- put class="selected" in the li tag for the selected page - to highlight which page you're on -->
                        <li class="selected"><a href="index.php">Home</a></li>
                        <li><a href="login.php">Instructor's Login</a></li>
                        <li><a href="register.php">Register</a></li>
                    </ul>
                </div>
            </div>
            <div id="content_header"></div>
            <div id="site_content">
                <div id="content">
                    <h1>Welcome to our service</h1>
                    <p>This service was created for Instructors of our university. Here Instructors can view the attendance list of the students and modify it...</p>
                    <img src="images/gallery3.jpg" style="width: 600px; height: 400px;"/>
                </div>
            </div>
            <div id="content_footer"></div>
            <div id="footer">
            </div>
            <p>&nbsp;</p>
        </div>
    </body>

</html>






