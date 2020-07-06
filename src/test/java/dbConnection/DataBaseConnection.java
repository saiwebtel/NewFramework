package dbConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import base.Testbase;

public class DataBaseConnection extends Testbase {

	public static Connection con;
	public static Connection getDBConnection(String dbIP,String dbUserName, String dbPassword,String serviceName) throws SQLException, ClassNotFoundException {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		con = DriverManager.getConnection("jdbc:oracle:thin:@" 
				+ dbIP 
				+serviceName, dbUserName, dbPassword);
		con.setAutoCommit(true);
		System.out.println("jdbc:oracle:thin:@"+dbIP+serviceName+","+dbUserName+","+dbPassword);
		return con;
	}

	public static void closeConnection() throws SQLException {
		try {
			con.close();
		} finally {
			con.close();
		}

	}

}
