/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facerecognitionattendance.GUI;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Ali
 */
public class NewClass {

    public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK);
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String courseID;
        switch (Integer.parseInt(sdf.format(c.getTime()))) {
            case 9:
                courseID = "1";
                break;
            case 10:
                courseID = "2";
                break;
            case 11:
                courseID = "3";
                break;
            case 12:
                courseID = "4";
                break;
            case 13:
                courseID = "5";
                break;
            case 14:
                courseID = "6";
                break;
            case 15:
                courseID = "7";
                break;
            case 16:
                courseID = "8";
                break;
            case 17:
                courseID = "9";
                break;
            default:
                courseID = "0";

        }
        System.out.println(courseID);
    }
}
