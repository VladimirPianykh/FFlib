package com.bpa4j.data.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;
import com.bpa4j.core.Root;
import com.bpa4j.feature.FeatureModel;
import com.bpa4j.feature.FeatureSaver;
import com.bpa4j.feature.FeatureTransmissionContract;

public class DataBaseStorageManager{
	private static final class FunctionalRegistry{
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
	private static class DataBaseBridge{
		private final String name;
		private final String user,password; //Credentials
		private Connection connection;
		private final boolean dbWasCreated;
		public DataBaseBridge(String name,String user,String password){
			this.name=name;
			this.user=user;
			this.password=password;
			try{
				dbWasCreated=new File(name.substring(name.indexOf("file:")+5)).exists(); //Note: possibly constrained to H2.
				getConnection(); //Ensures this is fine.
			}catch(SQLException ex){
				throw new IllegalStateException("Could not establish connection with "+name+".",ex);
			}
		}
		public void exec(String sql) throws SQLException{
			getConnection().createStatement().execute(sql);
		}
		public ResultSet execQuery(String sql) throws SQLException{
			return getConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY).executeQuery(sql);
		}
		/**
		 * Checks whether the database was created with this bridge.
		 */
		public boolean dbWasCreated(){
			return dbWasCreated;
		}
		private Connection getConnection() throws SQLException{
			if(connection==null||!isValid(connection)){
				connection=DriverManager.getConnection(name,user,password);
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
	public ResultSet execQuery(String sql) throws SQLException{
		return bridge.execQuery(sql);
	}
	public DataBaseStorageManager(String dbName){
		bridge=new DataBaseBridge("jdbc:h2:file:"+Root.folder+"","","");
	}
	<F extends FeatureTransmissionContract> FeatureModel<F> getFeatureModel(F f){
		return FunctionalRegistry.getFeatureModel(f);
	}
	<F extends FeatureTransmissionContract> FeatureSaver<F> getFeatureSaver(F f){
		return FunctionalRegistry.getFeatureSaver(f);
	}
	<F extends FeatureTransmissionContract> void putModel(Class<F> f,Function<F,FeatureModel<F>> model){
		FunctionalRegistry.putModel(f,model);
	}
	<F extends FeatureTransmissionContract> void putSaver(Class<F> f,Function<F,FeatureSaver<F>> saver){
		FunctionalRegistry.putSaver(f,saver);
	}
}
