package com.globecapital.db;
import java.sql.Connection;
import java.sql.SQLException;

public interface DBPool {
	
	public Connection getConnection() throws SQLException;
	
	public void releasePool() ;

}