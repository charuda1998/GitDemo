package com.globecapital.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LocalDatabaseConnection {
	private Connection con;

	public Connection getCon() {
		return con;
	}

	public LocalDatabaseConnection(String url,String username,String password) {
           try {
        	   Class.forName("com.mysql.jdbc.Driver");
                 this.con = DriverManager.getConnection(url,username,password);
           } catch (ClassNotFoundException e) {
                 e.printStackTrace();
           } catch (SQLException e) {
                 e.printStackTrace();
           }    
     }
}
