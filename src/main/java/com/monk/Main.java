package com.monk;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.info("monk:namaste");

        File configFile = new File(System.getProperty("user.dir") + "/config_quick.json");
        Config conf = ConfigFactory.parseFile(configFile);

        String databaseURL = conf.getString("configuration.database.connection_string");
        String dbUsername = conf.getString("configuration.database.username");
        String dbPassword = conf.getString("configuration.database.password");

        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(conf.getString("configuration.database.driver"));
            conn = DriverManager.getConnection(databaseURL, dbUsername, dbPassword);
            conn.setReadOnly(true);
            if (conn != null){
                logger.info("Connection to db established");
            }
        } catch (ClassNotFoundException ex) {
            logger.error("No driver found");
            logger.error(ex.getMessage());
        } catch (SQLException ex) {
            logger.error("Error in connection to db.");
            logger.error(ex.getMessage());
        }

        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS MONK_VALUE, ((sysdate - to_date('01-JAN-1970','DD-MON-YYYY')) * (86400)) AS MONK_TIMESTAMP FROM SYSTEM.MON_USERS USERS JOIN SYSTEM.MON_DEPARTMENTS DEPT ON USERS.DEPARTMENT = DEPT.ID WHERE DEPT.name LIKE 'IT'");
            ResultSetMetaData rsmd = rs.getMetaData();
            String firstColumnName = rsmd.getColumnName(1);

            int colCount = rsmd.getColumnCount();
            logger.info("Number of columns found: " + colCount);

            List<String> columns = new ArrayList<String>();

            for (int i = 1; i <= colCount; i++){
                columns.add(rsmd.getColumnName(i));
            }

            logger.info("Names of columns found: " + columns.toString());

            int count = 0;
            while (rs.next()){
                count++;
                logger.info(firstColumnName + ": " + rs.getString(firstColumnName));
                logger.info("MONK_TIMESTAMP: " + rs.getInt("MONK_TIMESTAMP"));
            }
            logger.info("ROW_COUNT: " + count);
            rs.close();
        } catch (SQLException ex) {
            logger.error("Error in executing the query");
            logger.error(ex.getMessage());
        } catch (NullPointerException ex){
            logger.error("No connection could be established.");
        } finally {
            try{
                if(stmt!=null)
                    conn.close();
            }catch(SQLException se){
            }// do nothing
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException ex){
                logger.error(ex.getMessage());
            }//end finally try
        }
        logger.info("monk:punardarzanAya");
    }
}
