package com.bpa4j.data.database;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import com.bpa4j.core.Root;
import com.bpa4j.core.User;
import com.bpa4j.core.UserSaver;
import com.bpa4j.core.User.SerialUserFactory;
import com.bpa4j.data.database.DataBaseStorageManager.DataBaseBridge;

/**
 * Service, which saves the users in a data base.
 */
public class DataBaseUserSaver implements UserSaver{
	private final DataBaseBridge bridge;
	//Files
	private static final String CLEAR_USERS_SQL_RESOURCE="resources/sql/clearusers.sql";
	private static final String SAVE_USER_SQL_RESOURCE="resources/sql/saveuser.sql";
	private static final String SELECT_USER_SQL_RESOURCE="resources/sql/selectuser.sql";
	//Users table column indexes
	private static final int COL_LOGIN=1;
	private static final int COL_PASSWORD=2;
	private static final int COL_ROLE=3;
	private static final int COL_FAIL2BAN_TRIES=4;
	private static final int COL_TRIES=5;
	private static final int COL_LOCK_TIME=6;

	public DataBaseUserSaver(DataBaseBridge bridge){
		this.bridge=bridge;
	}
	/**
	 * Saves users, but first clears the data base.
	 */
	public void saveUsers(HashMap<String,User> userMap) throws RuntimeException,UncheckedIOException{
		try{
			// Clear
			String clearSql=new String(Root.getResourceAsStream(CLEAR_USERS_SQL_RESOURCE).readAllBytes());
			bridge.getConnection().createStatement().execute(clearSql);

			// Add
			String sql=new String(Root.getResourceAsStream(SAVE_USER_SQL_RESOURCE).readAllBytes());
			PreparedStatement s=bridge.getConnection().prepareStatement(sql);
			for(User u:userMap.values()){
				s.setString(COL_LOGIN,u.login);
				s.setString(COL_PASSWORD,u.password);
				s.setString(COL_ROLE,u.role.getClass().getName());
				s.setInt(COL_FAIL2BAN_TRIES,u.getFail2banTries());
				s.setInt(COL_TRIES,u.getTries());
				s.setTimestamp(COL_LOCK_TIME,u.lockTime==null?null:Timestamp.valueOf(u.lockTime));
				s.execute();
			}
			s.close();
		}catch(IOException ex){
			throw new UncheckedIOException(ex);
		}catch(SQLException ex){
			throw new RuntimeException(ex);
		}
	}
	public HashMap<String,User> loadUsers() throws UncheckedIOException{
		HashMap<String,User> userMap=new HashMap<>();
		try{
			String sql=new String(Root.getResourceAsStream(SELECT_USER_SQL_RESOURCE).readAllBytes());
			PreparedStatement s=bridge.getConnection().prepareStatement(sql);
			ResultSet res=s.executeQuery();
			while(res.next()){
				String login=res.getString(COL_LOGIN);
				String password=res.getString(COL_PASSWORD);
				User.Role role=(User.Role)Class.forName(res.getString(COL_ROLE)).getDeclaredConstructor().newInstance(); // Fragile
				int fail2banTries=res.getInt(COL_FAIL2BAN_TRIES);
				int tries=res.getInt(COL_TRIES);
				LocalDateTime lockTime=res.getTimestamp(COL_LOCK_TIME).toLocalDateTime();
				User u=SerialUserFactory.createUser(login,password,role,fail2banTries,tries,lockTime);
				userMap.put(login,u);
			}
			s.close();
			res.close();
		}catch(IOException ex){
			throw new UncheckedIOException(ex);
		}catch(SQLException ex){
			throw new RuntimeException(ex);
		}catch(ReflectiveOperationException ex){
			throw new IllegalStateException("Roles should have the default constructor to be loaded from data base.",ex);
		}
		return userMap;
	}
}
