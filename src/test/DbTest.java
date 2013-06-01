package test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

	    Connection connection = null;
	    try
	    {
	     Class.forName("org.sqlite.JDBC");
	      // create a database connection
	      connection = DriverManager.getConnection("jdbc:sqlite:src"+File.separator+"db"+File.separator+"subMuncher.sqlite");
	      Statement statement = connection.createStatement();
	      statement.setQueryTimeout(30);  // set timeout to 30 sec.
	      
	      
	      ResultSet rs = statement.executeQuery("select * from ud_sm_folders");
	      while(rs.next())
	      {
	        // read the result set
	        System.out.println("name = " + rs.getString("folderName"));
	        
	      }
	    }
	    catch(SQLException e)
	    {
	      // if the error message is "out of memory", 
	      // it probably means no database file is found
	      System.out.println(e.getMessage());
	    } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally
	    {
	      try
	      {
	        if(connection != null)
	          connection.close();
	      }
	      catch(SQLException e)
	      {
	        // connection close failed.
	        System.err.println(e);
	      }
	    }
	  }
	

}
