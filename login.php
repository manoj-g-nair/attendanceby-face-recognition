<?php
session_start();

unset($_SESSION['uid']);
unset($_SESSION['username']);
?>

<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

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
                        <li><a href="index.php">Home</a></li>
                        <li class="selected"><a href="login.php">Instructor's Login</a></li>
                        <li><a href="register.php">Register</a></li>
                    </ul>
                </div>
            </div>
            <div id="content_header"></div>
            <div id="site_content">
                <div id="content">
                    <form name="loginform" action="src/login_exec.php" method="post">
                        <div class="form_settings">
                            <h1>Instructor's Login</h1>
                            <p>
                                <span>Username: </span>
                                <input type="text" name="username" id="username" value="" />
                            </p>
                            <p>
                                <span>Password: </span>
                                <input type="password" id="password" name="password" value="" />
                            </p>
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
                                <input class="submit" type="submit" name="login_submit" value="submit" />
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






