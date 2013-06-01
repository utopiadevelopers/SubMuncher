package data.operations;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class Database {

	Connection connection;
	Statement sqlStatement;
	ResultSet resultSet;
	String connectionString = "jdbc:sqlite:src" + File.separator + "db"
			+ File.separator + "subMuncher.sqlite";

	public Database() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		// create a database connection
		connection = DriverManager.getConnection(connectionString);
		sqlStatement = connection.createStatement();
	}

	public void executeQuery(String query) throws SQLException {
		resultSet = sqlStatement.executeQuery(query);
	}
	
	public void executeInsertQuery(String query) throws SQLException {
		sqlStatement.executeUpdate(query);
	}
	
	public void insert(String tableName,Map<String,String> data)
	{
		//TODO
		StringBuffer query = new StringBuffer();
		query.append("insert into ").append(tableName).append(" values(");
		int count =0,size = data.size();
		for(String key : data.keySet())
		{
			if(count == size)
			{
				
			}
		}
	}

	public ResultSet getLastResultSet()
	{
		return resultSet;
	}
}
