/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package wild.api.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import wild.core.WildCommonsPlugin;

public class MySQL {
	
	private static final int TIMEOUT = 8;
	
	@Getter	private Connection connection;

	@Setter @Getter private String host;
	@Setter @Getter private int port;
	@Setter @Getter private String database;
	@Setter @Getter private String user;
	@Setter @Getter private String password;
	
	
	/**
	 * Instantiate the object, doesn't connect yet.
	 */
	public MySQL(@NonNull String host, int port, @NonNull String database, @NonNull String user, String password) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
	}

	
	/**
	 * Connects to the database.
	 */
	public void connect() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new SQLException("Could not load driver class com.mysql.jdbc.Driver", e);
		}

        connection = DriverManager.getConnection(
        		"jdbc:mysql://" + host + ":" + port + "/" + database +							// URI
        		"?useSSL=false",																// Disable warning
//        		"&connectTimeout=" + (TIMEOUT * 1000) + "&socketTimeout=" + (TIMEOUT * 1000), 	// Query
        		user, password); 																// Authentication
	}
    
    
	/**
	 * Closes the active connection.
	 */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            }
            catch (SQLException ex) { }
        }
    }
    
    /**
     *  Checks if the connection is still valid. Useful for refreshing.
     */
    public boolean isConnectionValid() {
        if (connection == null) {
        	return false;
        }
        
        try {
			return connection.isValid(TIMEOUT);
		} catch (SQLException e) {
			return false;
		}
    }
    
    
    
    /**
     * Prepares a query.
     */
    public SQLResult preparedQuery(@NonNull String sql, Object... parameters) throws SQLException {
    	PreparedStatement statement = null;
    	
    	try {
    		checkNullConnection();
    		statement = prepareWithParameters(connection, sql, false, parameters);
    		ResultSet resultSet = statement.executeQuery();
    		return new SQLResult(statement, resultSet);
    		
    	} catch (SQLException e) {
    		// statement va chiuso solo in caso di errore, altrimenti deve chiuderlo l'utente
	    	handleSQLException(statement, sql, e);
			throw e;
		}
    }
    
    
    /**
     * Prepares an update.
     */
    public int preparedUpdate(@NonNull String sql, Object... parameters) throws SQLException {
    	try {
    		checkNullConnection();
    		PreparedStatement statement = prepareWithParameters(connection, sql, false, parameters);
    		try {
    			int rowCount = statement.executeUpdate();
    			return rowCount;
    		} finally {
    			statement.close(); // Qui va sempre chiuso
    		}

    	} catch (SQLException e) {
	    	handleSQLException(null, sql, e);
			throw e;
		}
    }
    
    
    /**
     * Prepares an update and returns auto generated keys.
     */
    public SQLResult preparedUpdateAndKeys(@NonNull String sql, Object... parameters) throws SQLException {
    	PreparedStatement statement = null;
    	
    	try {
    		checkNullConnection();
    		statement = prepareWithParameters(connection, sql, true, parameters);
    		statement.executeUpdate();
    		ResultSet resultSet = statement.getGeneratedKeys();
    		return new SQLResult(statement, resultSet);
    		
    	} catch (SQLException e) {
	    	handleSQLException(statement, sql, e);
			throw e;
		}
    }
    
	
	/**
	 * Executes an update.<br>
	 * <b>Warning!</b> Use a prepared statement if there are user inputs, to avoid SQL injection.
	 */
	public int update(@NonNull String sql) throws SQLException {
		try {
			checkNullConnection();
			Statement statement = connection.createStatement();
			try {
				int rowCount = statement.executeUpdate(sql);
				return rowCount;
			} finally {
				statement.close(); // Qui va sempre chiuso
			}
			
		} catch (SQLException e) {
			handleSQLException(null, sql, e);
			throw e;
		}
	}
	
	
	/**
	 * Executes an update and auto generated keys.<br>
	 * <b>Warning!</b> Use a prepared statement if there are user inputs, to avoid SQL injection.
	 */
	public SQLResult updateAndKeys(@NonNull String sql) throws SQLException {
		Statement statement = null;
		
		try {
			checkNullConnection();
			statement = connection.createStatement();
			statement.executeUpdate(sql);
			ResultSet resultSet = statement.getGeneratedKeys();
			return new SQLResult(statement, resultSet);
			
		} catch (SQLException e) {
			handleSQLException(statement, sql, e);
			throw e;
		}
	}
	
	
	/**
	 * Queries the database.<br>
	 * <b>Warning!</b> Use a prepared statement if there are user inputs, to avoid SQL injection.
	 */
	public SQLResult query(@NonNull final String sql) throws SQLException {
		Statement statement = null;
		
		try {
			checkNullConnection();
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			return new SQLResult(statement, resultSet);
			
		} catch (SQLException e) {
			handleSQLException(statement, sql, e);
			throw e;
		}
    }
	
	
	public static String escapeLikeParameter(@NonNull String s) {
		StringBuilder result = new StringBuilder();
		
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\\' || c == '%' || c == '_') {
				result.append('\\');
			}
			result.append(c);
		}
	
		return result.toString();
	}


	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	/**
	 * Prepare a statement with parameters already set, returning auto generated keys.
	 * Internal use, the MOST COMPLETE method.
	 */
	private PreparedStatement prepareWithParameters(Connection connection, String sql, boolean returnGeneratedKeys, Object... parameters) throws SQLException {
		int parametersAmount = parameters != null ? parameters.length : 0;
		
		if (countOccurrences(sql, '?') != parametersAmount) {
			throw new SQLException("Amount of parameters doesn't match amount of values (question marks)");
		}
		
		PreparedStatement statement;
		if (returnGeneratedKeys) {
			statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		} else {
			statement = connection.prepareStatement(sql);
		}

		if (parametersAmount > 0) {
			int index = 1;
			for (Object o : parameters) {
				setParameter(statement, index, o);
				index++;
			}
		}
		
		return statement;
	}
	
	private void setParameter(PreparedStatement statement, int index, Object param) throws SQLException {
		if (param == null) {
			throw new SQLException("Parameter " + index + " for prepared statement cannot be null");
		}
		
		Class<?> clazz;
		Object value;
		
		if (param instanceof Nullable) {
			Nullable nullableParam = (Nullable) param;
			clazz = nullableParam.getNullType();
			value = nullableParam.getValue();
			
		} else {
			clazz = param.getClass();
			value = param;
		}
		
		if 		(clazz == int.class) 		statement.setInt(index, (int) value);
		else if (clazz == Integer.class)	statement.setInt(index, (Integer) value);
		else if (clazz == long.class) 		statement.setLong(index, (long) value);
		else if (clazz == Long.class) 		statement.setLong(index, (Long) value);
		else if (clazz == String.class) 	statement.setString(index, (String) value);
		else if (clazz == boolean.class) 	statement.setBoolean(index, (boolean) value);
		else if (clazz == Boolean.class) 	statement.setBoolean(index, (Boolean) value);
		else if (clazz == double.class) 	statement.setDouble(index, (double) value);
		else if (clazz == Double.class) 	statement.setDouble(index, (Double) value);
		else if (clazz == float.class) 		statement.setFloat(index, (float) value);
		else if (clazz == Float.class) 		statement.setFloat(index, (Float) value);
		else if (clazz == short.class) 		statement.setShort(index, (short) value);
		else if (clazz == Short.class) 		statement.setShort(index, (Short) value);
		// Add here Date, Timestamp, etc
		else throw new SQLException("Unknown or unsupported parameter type: " + clazz.getSimpleName());
	}
	
	
	private void handleSQLException(AutoCloseable closeable, String sql, SQLException sqlException) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception ex) { }
		}
		WildCommonsPlugin.mysqlErrorLogger.log("MySQL error (query: " + sql + "): " + sqlException.toString().replaceAll("\n{2,}", "\n"));
		if (!isConnectionValid()) {
			// Se è un errore di connessione (e non per esempio di query) proviamo a riconnettere
			try {
				connect();
				WildCommonsPlugin.mysqlErrorLogger.log("Reconnection attempt succeeded!");
			} catch (SQLException e) {
				WildCommonsPlugin.mysqlErrorLogger.log("Reconnection attempt failed.");
			}
		}
	}
	
	
    private void checkNullConnection() throws SQLException {
		if (connection == null) {
			throw new SQLException("Connection was null (either connect() was not called or failed)");
		}
	}
    
    
    private int countOccurrences(String haystack, char needle) {
        int count = 0;
        
        for (int i = 0; i < haystack.length(); i++) {
            if (haystack.charAt(i) == needle) {
                 count++;
            }
        }
        
        return count;
    }
	
}
