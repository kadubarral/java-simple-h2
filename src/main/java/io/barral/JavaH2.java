package io.barral;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.h2.tools.DeleteDbFiles;
import static java.sql.DriverManager.*;

public class JavaH2 {

    private static final Logger LOGGER = Logger.getLogger(JavaH2.class.getName());

    public static void main(String[] args) {

        JavaH2.insertAndPrint();

    }

    public static Integer insertAndPrint() {

        // delete the database named 'test' in the user home directory
        DeleteDbFiles.execute("~", "test", true);

        String user = "user";
        char[] password = {'t', 'i', 'a', 'E', 'T', 'r', 'p'};
        String servername = null;
        try {
            servername = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Properties prop = new Properties();
        prop.setProperty("user", user);
        prop.put("password", password);

        Double total = 0.0;
        Integer count = 0;

        try (Connection conn = getConnection("jdbc:h2:~/test", prop)) {
            try (Statement stat = conn.createStatement()) {

                //create table
                stat.execute("CREATE TABLE ACTIVITY (ID INTEGER, STARTTIME datetime, ENDTIME datetime, SERVERNAME VARCHAR(200), ACTIVITYNAME VARCHAR(200), PRIMARY KEY (ID))");

                //prepared statement
                try (PreparedStatement prep = conn.prepareStatement("INSERT INTO ACTIVITY (ID, STARTTIME, ENDTIME, SERVERNAME, ACTIVITYNAME) VALUES (?,?,?,?,?)")) {

                    //insert 10 row data
                    for (int i = 0; i < 10; i++) {
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                conn.setAutoCommit(true);

                ResultSet rst;
                rst = stat.executeQuery("Select count(*) from ACTIVITY");
                if (rst.next()) {
                    total = rst.getDouble(1);
                } else {
                    total = 0.0;
                }

                //query to database
                try {

                    ResultSet rs;
                    try (PreparedStatement prep2 = conn.prepareStatement("Select STARTTIME, ENDTIME, ACTIVITYNAME, SERVERNAME from ACTIVITY where SERVERNAME = ?")) {
                        prep2.setString(1, servername);
                        rs = prep2.executeQuery();

                        while (rs.next()) {

                            count++;

                            Date start = rs.getTimestamp(1);
                            Date end = rs.getTimestamp(2);
                            String activityName = rs.getString(3);
                            String serverName = rs.getString(4);

                            //print query result to console
                            LOGGER.log(Level.INFO,"activity: {0} ", activityName);
                            LOGGER.log(Level.INFO,"local: {0} ", serverName);
                            LOGGER.log(Level.INFO,"start: {0} ", start);
                            LOGGER.log(Level.INFO,"end: {0} ", end);
                            if (total == 0) {
                                throw new UnsupportedOperationException("Can't divide by zero!");
                            } else {
                                LOGGER.log(Level.INFO,"% done: {0} ", (count / total) * 100);
                            }

                        }
                    }
                } catch (SQLException | UnsupportedOperationException e) {
                    LOGGER.log(Level.SEVERE, "context", e);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count;
    }
}