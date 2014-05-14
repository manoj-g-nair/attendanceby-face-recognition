/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facerecognitionattendance.additional;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ali
 */
public class DBConnection {

    private Connection conn;

    public DBConnection() {

        String database = "seproject";
        String username = "root";
        String password = ""; 
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/" + database + "?useUnicode=true&characterEncoding=utf8", username, password);

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Student getStudent(String ID) {
        Student student = new Student();
        try {
            Statement sta = conn.createStatement();
            String Sql = "select * from student where student_id = '" + ID + "'";
            ResultSet rs = sta.executeQuery(Sql);
            while (rs.next()) {
                student.setID(ID);
                student.setFacultyID(rs.getInt("faculty_id") + "");
                student.setGroupID(rs.getInt("group_id") + "");
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
            }
            return student;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean check(String login, String password) {
        try {
            byte[] bytesOfMessage = password.getBytes("UTF-8");

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(bytesOfMessage);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < thedigest.length; ++i) {
                sb.append(Integer.toHexString((thedigest[i] & 0xFF) | 0x100).substring(1, 3));
            }
            Statement sta = conn.createStatement();
            String Sql = "select * from instructor where privilege='admin' and username='" + login.trim() + "' and password='" + sb.toString() + "'";
            ResultSet rs = sta.executeQuery(Sql);
            while (rs.next()) {
                return true;
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void checkAtt(Student student, long currentTimeMillis) {
        try {
            Calendar c = Calendar.getInstance();
            int day_of_week = c.get(Calendar.DAY_OF_WEEK);
            SimpleDateFormat sdf = new SimpleDateFormat("HH");
            String courseIDColumn;
            switch (Integer.parseInt(sdf.format(c.getTime()))) {
                case 9:
                    courseIDColumn = "1";
                    break;
                case 10:
                    courseIDColumn = "2";
                    break;
                case 11:
                    courseIDColumn = "3";
                    break;
                case 12:
                    courseIDColumn = "4";
                    break;
                case 13:
                    courseIDColumn = "5";
                    break;
                case 14:
                    courseIDColumn = "6";
                    break;
                case 15:
                    courseIDColumn = "7";
                    break;
                case 16:
                    courseIDColumn = "8";
                    break;
                case 17:
                    courseIDColumn = "9";
                    break;
                default:
                    courseIDColumn = null;

            }
            if (courseIDColumn != null) {
                Statement sta = conn.createStatement();
                String Sql = "select course_id" + courseIDColumn + " from schedule where group_id = " + student.getGroupID() + " and day=" + day_of_week;
                ResultSet rs = sta.executeQuery(Sql);
                String courseID = null;
                while (rs.next()) {
                    courseID = rs.getInt(1) + "";
                }
                if (courseID != null) {
                    String query = "INSERT INTO attendance (student_id, course_id, status,date) VALUES ('" + student.getID() + "', '" + courseID + "', " + 1 + ",  NOW())";
                    Statement stmt = conn.prepareStatement(query);
                    stmt.execute(query);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
