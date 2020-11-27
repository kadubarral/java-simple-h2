package io.barral;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import org.h2.tools.DeleteDbFiles;

public class JavaH2 {

    public static void main(String[] args) {

        try {

            // delete the database named 'test' in the user home directory
            DeleteDbFiles.execute("~", "test", true);

            String user = "user";
            char[] password = {'t', 'i', 'a', 'E', 'T', 'r', 'p'};

            Properties prop = new Properties();
            prop.setProperty("user", user);
            prop.put("password", password);
            Connection conn = DriverManager.getConnection("jdbc:h2:~/test", prop);
            Statement stat = conn.createStatement();

            String servername = InetAddress.getLocalHost().getHostName();

            //create table
            stat.execute("CREATE TABLE ACTIVITY (ID INTEGER, STARTTIME datetime, ENDTIME datetime, SERVERNAME VARCHAR(200), ACTIVITYNAME VARCHAR(200), PRIMARY KEY (ID))");

            //prepared statement
            PreparedStatement prep = conn.prepareStatement("INSERT INTO ACTIVITY (ID, STARTTIME, ENDTIME, SERVERNAME, ACTIVITYNAME) VALUES (?,?,?,?,?)");

            //insert 10 row data
            for (int i = 0; i<10; i++){
                prep.setLong(1, i);
                prep.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                Thread.sleep(500);
                prep.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                prep.setString(4, servername);
                prep.setString(5, "Activity-" + i);

                //batch insert
                prep.addBatch();
            }
            conn.setAutoCommit(false);
            prep.executeBatch();
            conn.setAutoCommit(true);

            ResultSet rst = stat.executeQuery("Select count(*) from ACTIVITY");

            double total;
            if (rst.next())
            {
               total = rst.getDouble(1);
            } else {
                total = 0.0;
            }

            Integer count = 1;

            //query to database
            try {
                ResultSet rs = stat.executeQuery("Select STARTTIME, ENDTIME, ACTIVITYNAME, SERVERNAME from ACTIVITY where SERVERNAME = '" + servername + "'");
                while (rs.next()) {

                    Date start = rs.getTimestamp(1);
                    Date end = rs.getTimestamp(2);
                    String activityName = rs.getString(3);
                    String serverName = rs.getString(4);

                    //print query result to console
                    System.out.println("activity: " + activityName);
                    System.out.println("local: " + serverName);
                    System.out.println("start: " + start);
                    System.out.println("end: " + end);
                    if (total == 0) {
                        throw new UnsupportedOperationException("Can't divide by zero!");
                    } else {
                        System.out.println("% done: " + (count/total)*100);
                    }
                    System.out.println("--------------------------");

                    count++;
                }
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                //close connection
                conn.close();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}