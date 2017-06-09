package com.monk;

import javax.xml.transform.Result;
import java.sql.*;

public class Main {

    public static void main(String[] args) {

        String databaseURL = "jdbc:oracle:thin:@localhost:49161:XE";
        String dbUsername = "system";
        String dbPassword = "oracle";

        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection(databaseURL, dbUsername, dbPassword);
            conn.setReadOnly(true);
            if (conn != null){
                System.out.println("Connetion to db established");
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("No driver found");
            ex.printStackTrace();
        } catch (SQLException ex) {
            System.out.println("Error in connection to db.");
            System.out.println(ex.getMessage());
        }

        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS NUM_USERS_IT, ((sysdate - to_date('01-JAN-1970','DD-MON-YYYY')) * (86400)) AS TS FROM SYSTEM.MON_USERS USERS JOIN SYSTEM.MON_DEPARTMENTS DEPT ON USERS.DEPARTMENT = DEPT.ID WHERE DEPT.name LIKE 'IT'");
            ResultSetMetaData rsmd = rs.getMetaData();
            String firstColumnName = rsmd.getColumnName(1);

            int colCount = rsmd.getColumnCount();
            System.out.println("COL_COUNT: " + colCount);

            for (int i = 1; i <= colCount; i++){
                System.out.println("COL" + i + ": " + rsmd.getColumnName(i));
            }

            int count = 0;
            while (rs.next()){
                count++;
                System.out.println(firstColumnName + ": " + rs.getString(firstColumnName));
                System.out.println("TS: " + rs.getInt("TS"));
            }
            System.out.println("ROW_COUNT: " + count);
            rs.close();
        } catch (SQLException ex) {
            System.out.println("Error in executing the query");
            System.out.println(ex.getMessage());
        } catch (NullPointerException ex){
            System.out.println("No connection could be established.");
        } finally {
            try{
                if(stmt!=null)
                    conn.close();
            }catch(SQLException se){
            }// do nothing
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }
    }
}
