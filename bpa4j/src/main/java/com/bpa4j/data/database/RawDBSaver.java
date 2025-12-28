package com.bpa4j.data.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import com.bpa4j.core.Root;

/**
 * Loads/saves whole objects in separate tables.
 * These tables have single `BLOB` entry.
 */
public final class RawDBSaver{
	private RawDBSaver(){}
	private static final String CREATE_GLOBAL_SQL_FILE="resources/sql/global/createglobal.sql";
	private static final String REMOVE_GLOBAL_SQL_FILE="resources/sql/global/removeglobal.sql";
	private static final String SAVE_GLOBAL_SQL_FILE="resources/sql/global/saveglobal.sql";
	private static final String SELECT_GLOBAL_SQL_FILE="resources/sql/global/selectglobal.sql";
	public static void save(Connection conn,Object obj,String tableName) throws SQLException{
		try{
			//Create if does not exist
			String createSqlUnf=new String(Objects.requireNonNull(Root.getResourceAsStream(CREATE_GLOBAL_SQL_FILE)).readAllBytes());
			String createSql=String.format(createSqlUnf,tableName);
			conn.createStatement().executeQuery(createSql);

			//Clear
			String removeSqlUnf=new String(Objects.requireNonNull(Root.getResourceAsStream(REMOVE_GLOBAL_SQL_FILE)).readAllBytes());
			String removeSql=String.format(removeSqlUnf,tableName);
			conn.createStatement().executeQuery(removeSql);

			//Save object
			String saveSqlUnf=new String(Objects.requireNonNull(Root.getResourceAsStream(SAVE_GLOBAL_SQL_FILE)).readAllBytes());
			String saveSql=String.format(saveSqlUnf,tableName);
			PreparedStatement p=conn.prepareStatement(saveSql);
			ByteArrayOutputStream b=new ByteArrayOutputStream();
			ObjectOutputStream oos=new ObjectOutputStream(b);
			oos.writeObject(obj);
			p.setBytes(1,b.toByteArray());
		}catch(IOException ex){
			throw new UncheckedIOException(ex);
		}
	}
	public static Object load(Connection conn,String tableName) throws SQLException{
		try{
			//Select object
			String selectSqlUnf=new String(Objects.requireNonNull(Root.getResourceAsStream(SELECT_GLOBAL_SQL_FILE)).readAllBytes());
			String selectSql=String.format(selectSqlUnf,tableName);
			try(ResultSet res=conn.createStatement().executeQuery(selectSql)){
				//Retrieve bytes
				byte[] b;
				if(res.next()) b=res.getBytes(1);
				else throw new SQLException("Object not found.");
				if(res.next()) throw new SQLException("There are multiple objects within this table.");

				ObjectInputStream ois=new ObjectInputStream(new ByteArrayInputStream(b));
				Object obj=ois.readObject();
				return obj;
			}
		}catch(IOException ex){
			throw new UncheckedIOException(ex);
		}catch(ClassNotFoundException ex){
			throw new RuntimeException(ex);
		}
	}
}
