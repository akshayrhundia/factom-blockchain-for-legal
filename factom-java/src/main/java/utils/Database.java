package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
    private static Connection conn;
    private static String database="bc";
    private static String hostname="slb-ingested-storage:us-central1:blockchain";
    private static String username="root";
    private static String password="admin";

    private Database(){}
    public static Connection getInstance(){
        if(conn==null){
            try {
                String url = String.format(
                        "jdbc:mysql://google/%s?cloudSqlInstance=%s&"
                                + "socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=true",
                        database,
                        hostname);
                conn = DriverManager.getConnection(url,username,password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }
    public static void insertData(String query,Connection conn){
        try {
            System.out.println(query);
            PreparedStatement statement=conn.prepareStatement(query);
            System.out.println("Executed statement:" + statement.executeUpdate());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
