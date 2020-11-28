package feup.tvvs;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import org.h2.tools.DeleteDbFiles;

public class staticanalisys {

    public static void main(String[] args) {

        try {

            // delete the database named 'test' in the user home directory
            DeleteDbFiles.execute("~", "test", true);

            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:~/test");
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

            double num1=0.0, num2=0.0;
            double sum=0.0, sub=0.0, multiple=0.0;
            double divide=0.0, remainder=0.0, power=0.0;
            char operator='\0';
            boolean nextOperation = true;
            char ch='\0';

            ResultSet rst = stat.executeQuery("Select count(*) from ACTIVITY");
            Double total = 0.0;
            if (rst.next())
            {
                total = rst.getDouble(1);
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
                    System.out.println("% done: " + (count/total)*100);
                    System.out.println("--------------------------");

                    count++;
                }
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //close connection
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}