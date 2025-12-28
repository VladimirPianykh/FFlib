package com.bpa4j.data.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import com.bpa4j.core.Editable;
import com.bpa4j.core.EditableGroup;

/**
 * Manages SQL tables with editables as utility class.
 */
public final class EditableORM{
	public static final String GROUP_TABLE_NAME="groups";
	private static enum SQLType{
		VARCHAR("VARCHAR(255)"),INT("INT"),BIGINT("BIGINT"),REAL("REAL"),DOUBLE("DOUBLE PRECISION"),BOOLEAN("BOOLEAN"),DATE("DATE"),TIMESTAMP("TIMESTAMP"),BLOB("BLOB");
		String sql;
		SQLType(String sql){
			this.sql=sql;
		}
		public String toString(){
			return sql;
		}
	}
	/**
	 * Any table also has ID column.
	 */
	private static record Table(String name,List<Column> columns){
		public static record Column(Field f,String name,SQLType type){
			public String toString(){
				return name+" "+type;
			}
		}
	}
	public static String getTableName(Connection conn,Class<? extends Editable>c)throws SQLException{
		TreeSet<String>names=new TreeSet<>();
		try(ResultSet res=conn.getMetaData().getTables(null,null,null,null)){
			while(res.next()){
				String s=res.getString("TABLE_NAME");
				names.add(s);
			}
		}
		String name=c.getSimpleName();
		while(names.contains(name))name+="_";
		return name;
	}

	/**
	 * Creates table.
	 * Does not commit changes.
	 * Warning! Fields, that are called identically in the parent and in the child will cause {@code SQLException}!
	 * @throws SQLException if error occurs during interaction with database or there are duplicated fields.
	 */
	public static void createTableFrom(Connection conn,Class<? extends Editable> e) throws SQLException{
		Table table=buildTable(conn,e);
		create(conn,table);
	}
	/**
	 * Writes object to table.
	 * Does not commit changes.
	 * @throws NotSerializableException if {@code editable} or it's contents are not serializable.
	 */
	public static void writeToTable(Connection conn,Editable editable) throws SQLException{
		Table table=buildTable(conn,editable.getClass());
		write(conn,table,editable);
	}
	
	@SuppressWarnings("rawtypes")
	public static void createGroupTable(Connection conn,Class<EditableGroup>c)throws SQLException{
		Table t=buildGroupTable(conn,c);
		create(conn,t);
	}
	public static void writeToGroupTable(Connection conn,EditableGroup<?>g)throws SQLException{
		Table t=buildGroupTable(conn,EditableGroup.class);
		write(conn,t,g);
	}
	
	private static void create(Connection conn,Table table)throws SQLException{
		StringBuilder s=new StringBuilder();
		s.append("CREATE TABLE "+table.name+"(");
		s.append("id BIGINT AUTO_INCREMENT PRIMARY KEY,");
		String fieldColumns=String.join(",",table.columns.stream().map(Table.Column::toString).toList());
		s.append(fieldColumns);
		s.append(")");
		conn.createStatement().execute(s.toString());
	}
	private static void write(Connection conn,Table table,Object o)throws SQLException{
		String fieldNames=String.join(",",table.columns.stream().map(e->e.name).toList());
		StringBuilder s=new StringBuilder();
		s.append("INSERT INTO ");
		s.append(table.name);
		s.append("(");
		s.append(fieldNames);
		s.append(")VALUES(");
		s.append("?,".repeat(table.columns.size()-1));
		s.append("?)");
		PreparedStatement statement=conn.prepareStatement(s.toString());
		for(int i=0;i<table.columns.size();i++)
			try{
				Object value=table.columns.get(i).f.get(o);
				sendDBValue(statement,i+1,value);
			}catch(ReflectiveOperationException ex){
				throw new IllegalStateException(ex);
			}
	}

	@SuppressWarnings("rawtypes")    
	private static Table buildGroupTable(Connection conn,Class<EditableGroup>c)throws SQLException{
		String tableName=GROUP_TABLE_NAME;
		ArrayList<Table.Column>l=new ArrayList<>();
		for(Field f:c.getDeclaredFields()){
			Table.Column col=new Table.Column(f,f.getName(),convertToSQLType(f.getType()));
			l.add(col);
		}
		return new Table(tableName,l);
	}
	private static Table buildTable(Connection conn,Class<? extends Editable> c)throws SQLException{
		ArrayList<Table.Column> l=new ArrayList<>();
		for(Field f:c.getFields())
			if(!Modifier.isTransient(f.getModifiers()))l.add(new Table.Column(f,f.getName(),convertToSQLType(f.getType())));
		return new Table(getTableName(conn,c),l);
	}
	
	private static void sendDBValue(PreparedStatement s,int index,Object o) throws SQLException{
		// Set parameter on PreparedStatement in batches, handle SQLExceptions
		if(o==null) s.setObject(index,null);
		else if(o instanceof String) s.setString(index,(String)o);
		else if(o instanceof Number) s.setObject(index,o);
		else if(o instanceof Boolean) s.setBoolean(index,(Boolean)o);
		else if(o instanceof Enum<?>) s.setString(index,((Enum<?>)o).name());
		else if(o instanceof java.time.LocalDate) s.setObject(index,o);
		else if(o instanceof java.time.LocalDateTime) s.setObject(index,o);
		else{
			ByteArrayOutputStream b=new ByteArrayOutputStream();
			try(ObjectOutputStream oos=new ObjectOutputStream(b)){
				oos.writeObject(o);
				s.setBytes(index,b.toByteArray());
			}catch(IOException ex){
				throw new IllegalStateException(ex);
			}
		}
	}
	private static SQLType convertToSQLType(Class<?> type){
		if(type.isEnum()) return SQLType.VARCHAR;
		if(type==int.class||type==Integer.class) return SQLType.INT;
		if(type==long.class||type==Long.class) return SQLType.BIGINT;
		if(type==double.class||type==Double.class) return SQLType.DOUBLE;
		if(type==float.class||type==Float.class) return SQLType.REAL;
		if(type==boolean.class||type==Boolean.class) return SQLType.BOOLEAN;
		if(type==String.class) return SQLType.VARCHAR;
		if(type==LocalDate.class) return SQLType.DATE;
		if(type==LocalDateTime.class) return SQLType.TIMESTAMP;
		return SQLType.BLOB;
	}
}
