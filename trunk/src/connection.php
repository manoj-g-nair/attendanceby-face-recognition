<?php

    $mysql_hostname = "localhost";
    $mysql_user = "root";
    $mysql_password = "Ersayin94";
    $mysql_database = "seproject";
    $prefix = "";
    $db = mysql_connect($mysql_hostname, $mysql_user, $mysql_password)
            or die("Couldn't connect to database");
    mysql_select_db($mysql_database, $db) or die("Couldn't select database");
?>