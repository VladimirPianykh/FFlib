package com.bpa4j.data.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import com.bpa4j.core.Data;
import com.bpa4j.core.Root;
import com.bpa4j.core.StorageManager;
import com.bpa4j.core.UserSaver;
import com.bpa4j.feature.FeatureModel;
import com.bpa4j.feature.FeatureSaver;
import com.bpa4j.feature.FeatureTransmissionContract;

/**
 * Storage manager with H2 database.
 * WARNING: Does not save users' authorization history.
 */
public class DataBaseStorageManager implements StorageManager{
	private final class FunctionalRegistry{
		private static HashMap<Class<? extends FeatureTransmissionContract>,Function<? extends FeatureTransmissionContract,? extends FeatureModel<?>>> models=new HashMap<>();
		private static HashMap<Class<? extends FeatureTransmissionContract>,Function<? extends FeatureTransmissionContract,? extends FeatureSaver<?>>> savers=new HashMap<>();

		@SuppressWarnings("unchecked")
		static <F extends FeatureTransmissionContract> FeatureModel<F> getFeatureModel(F f){
			Function<F,FeatureModel<F>> function=(Function<F,FeatureModel<F>>)models.get(f.getClass());
			FeatureModel<?> m=function.apply(f);
			Objects.requireNonNull(m);
			if(m.getTransmissionContract()!=f) throw new IllegalStateException(function+" is not valid.");
			return (FeatureModel<F>)m;
		}
		@SuppressWarnings("unchecked")
		static <F extends FeatureTransmissionContract> FeatureSaver<F> getFeatureSaver(F f){
			Function<F,FeatureSaver<F>> function=(Function<F,FeatureSaver<F>>)savers.get(f.getClass());
			FeatureSaver<?> s=function.apply(f);
			Objects.requireNonNull(s);
			return (FeatureSaver<F>)s;
		}
		static <F extends FeatureTransmissionContract> void putModel(Class<F> f,Function<F,FeatureModel<F>> model){
			models.put(f,model);
		}
		static <F extends FeatureTransmissionContract> void putSaver(Class<F> f,Function<F,FeatureSaver<F>> saver){
			savers.put(f,saver);
		}
	}
	/**
	 * Supports connection with the database.
	 * Provides {@link DataBaseBridge#getConnection()} operation.
	 * This connection is managed automatically and will be closed with the DataBaseBridge itself.
	 * Thus it is unsafe to use this in a multi-threaded environment.
	 */
	static class DataBaseBridge{
		private final String dbName;
		private final String login,password;
		private Connection connection;
		private final boolean dbWasCreated;
		public DataBaseBridge(String dbName,String user,String password){
			this.dbName=dbName;
			this.login=user;
			this.password=password;
			try{
				dbWasCreated=!new File(dbName.substring(dbName.indexOf("file:")+5)).exists(); //Note: possibly constrained to H2.
				getConnection(); //Ensures this is fine.
			}catch(SQLException ex){
				throw new IllegalStateException("Could not establish connection with "+dbName+".",ex);
			}
		}
		/**
		 * Checks whether the database was created with this bridge.
		 */
		public boolean dbWasCreated(){
			return dbWasCreated;
		}
		public void close(){
			if(connection!=null)try{
				//No commit required because of auto-commit mode.
				connection.close();
			}catch(SQLException ex){
				throw new IllegalStateException(ex);
			}
		}
		public Connection getConnection() throws SQLException{
			if(connection==null||!isValid(connection)){
				connection=DriverManager.getConnection(dbName,login,password);
				connection.setAutoCommit(true);
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
	private final DataBaseData storage=new DataBaseData();
	/**
	 * Executes query.
	 * Is not protected from SQL injection (altering commands are possible).
	 * @param sql - sql query
	 * @param resultHandler - consumer, which will get the result of this query
	 */
	public void execQuery(String sql,Consumer<ResultSet>resultHandler) throws SQLException{
		try(ResultSet res=bridge.getConnection().createStatement().executeQuery(sql)){
			resultHandler.accept(res);
		}
	}
	public DataBaseStorageManager(String dbName){
		bridge=new DataBaseBridge("jdbc:h2:file:"+Root.folder+dbName,"","");
		try{
			storage.load(bridge.getConnection());
		}catch(SQLException ex){
			throw new RuntimeException(ex);
		}
	}
	public <F extends FeatureTransmissionContract> FeatureModel<F> getFeatureModel(F f){
		return FunctionalRegistry.getFeatureModel(f);
	}
	public <F extends FeatureTransmissionContract> FeatureSaver<F> getFeatureSaver(F f){
		return FunctionalRegistry.getFeatureSaver(f);
	}
	public <F extends FeatureTransmissionContract> void putModel(Class<F> f,Function<F,FeatureModel<F>> model){
		FunctionalRegistry.putModel(f,model);
	}
	public <F extends FeatureTransmissionContract> void putSaver(Class<F> f,Function<F,FeatureSaver<F>> saver){
		FunctionalRegistry.putSaver(f,saver);
	}
	public void close(){
		try{
			storage.save(bridge.getConnection());
		}catch(SQLException ex){
			throw new RuntimeException(ex);
		}
		bridge.close();
	}
	public boolean isFirstLaunch(){
		return !bridge.dbWasCreated();
	}
	public Data getStorage(){
		return storage;
	}
	public UserSaver getUserSaver(){
		return new DataBaseUserSaver(bridge); //No need to keep it.
	}
}
