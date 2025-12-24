package com.bpa4j.data.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.bpa4j.core.Root;

public class DataBaseStorageManager{
	private static final class FunctionalRegistry{
		
	}
	private static class DataBaseBridge{
		private String name;
		private String user,password; //Credentials
		private Connection connection;
		public DataBaseBridge(String name,String user,String password){
			this.name=name;
			this.user=user;
			this.password=password;
		}
		public void exec(String sql)throws SQLException{
			getConnection().createStatement().execute(sql);
		}
		public ResultSet execQuery(String sql)throws SQLException{
			return getConnection()
				.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY)
				.executeQuery(sql);
		}
		private Connection getConnection(){
			if(connection==null||!isValid(connection)){
				try{
					connection=DriverManager.getConnection(name,user,password);
				}catch(SQLException ex){
					throw new IllegalStateException(ex); //FIXME: Think about exception if the database is not found
				}
			}
			return connection;
		}
		private boolean isValid(Connection connection){
			try{
				return connection.isValid(10);
			}catch(SQLException ex){
				throw new AssertionError(ex);
			}
		}
	}
	private final DataBaseBridge bridge;
	/**
	 * Is not protected from SQL injection.
	 */
	public ResultSet execQuery(String sql)throws SQLException{
		return bridge.execQuery(sql);
	}
	public DataBaseStorageManager(String dbName){
		bridge=new DataBaseBridge("jdbc:h2:file:"+Root.folder+"","","");
	}
}
