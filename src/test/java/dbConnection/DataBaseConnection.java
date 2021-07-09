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
	private static DataBaseConnection dbIsntance;
	private static Connection con;
	private static String tmuserName;
	private static String tmpassWord;
	private static String uusduserName;
	private static String uusdpassWord;
	private static String comicuserName;
	private static String comicpassWord;
	private static String dbIp;
	private static String serviceName;

	private DataBaseConnection() {
		tmuserName=getDBUserName("TM");
		tmpassWord=getDBPassword("TM");
		uusduserName=getDBUserName("UUSD");
		uusdpassWord=getDBPassword("UUSD");
		comicuserName=getDBUserName("COMIC");
		comicpassWord=getDBPassword("COMIC");
		dbIp=getTMDBIP();
		serviceName=getDBServiceName();
   }

	public static DataBaseConnection getInstance() throws ClassNotFoundException, SQLException {
		if (dbIsntance == null) {
			dbIsntance = new DataBaseConnection();
		} else if (dbIsntance!=null) {
			con.close();
			dbIsntance = new DataBaseConnection();
        }
		return dbIsntance;
		}

	public Connection getDBConnection(String databaseName) throws SQLException,ClassNotFoundException {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		if (con == null) {
			if (databaseName.equalsIgnoreCase("TM")) {
				con = DriverManager.getConnection("jdbc:oracle:thin:@" + dbIp+ serviceName, tmuserName, tmpassWord);
				con.setAutoCommit(true);
				System.out.println("jdbc:oracle:thin:@" + dbIp + serviceName+ "," + tmuserName + "," + tmpassWord);
				System.out.println("Connection Established with TM Database.");
			} else if (databaseName.equalsIgnoreCase("UUSD")) {
				con = DriverManager.getConnection("jdbc:oracle:thin:@" + dbIp+ serviceName, uusduserName, uusdpassWord);
				con.setAutoCommit(true);
				System.out.println("jdbc:oracle:thin:@" + dbIp + serviceName+ "," + uusduserName + "," + uusdpassWord);
				System.out.println("Connection Established with UUSD Database.");
			} else if (databaseName.equalsIgnoreCase("COMIC")) {
				con = DriverManager.getConnection("jdbc:oracle:thin:@" + dbIp+ serviceName, comicuserName, comicpassWord);
				System.out.println("jdbc:oracle:thin:@" + dbIp + serviceName+ "," + comicuserName + "," + comicpassWord);
				con.setAutoCommit(true);
				System.out.println("Connection Established with COMIC Database.");
			}else {
				System.out.println("Incorrect database name");
		} 
		}
		else  {
			System.out.println("Connections are not closed so closing all connections and reconnecting.");
			con.close();
			if (databaseName.equalsIgnoreCase("TM")) {
				con = DriverManager.getConnection("jdbc:oracle:thin:@" + dbIp+ serviceName, tmuserName, tmpassWord);
				con.setAutoCommit(true);
				System.out.println("jdbc:oracle:thin:@" + dbIp + serviceName+ "," + tmuserName + "," + tmpassWord);
				System.out.println("Connection Established with TM Database.");
			} else if (databaseName.equalsIgnoreCase("UUSD")) {
				con = DriverManager.getConnection("jdbc:oracle:thin:@" + dbIp+ serviceName, uusduserName, uusdpassWord);
				con.setAutoCommit(true);
				System.out.println("jdbc:oracle:thin:@" + dbIp + serviceName+ "," + uusduserName + "," + uusdpassWord);
				System.out.println("Connection Established with UUSD Database.");
			} else if (databaseName.equalsIgnoreCase("COMIC")) {
				con = DriverManager.getConnection("jdbc:oracle:thin:@" + dbIp+ serviceName, comicuserName, comicpassWord);
				con.setAutoCommit(true);
				System.out.println("jdbc:oracle:thin:@" + dbIp + serviceName+ "," + comicuserName + "," + comicpassWord);
			}
			else {
				System.out.println("Incorrect database name");
		} 
		}
		return con;
	}
	public static void closeConnection() throws SQLException {
		try {
			con.close();
		} finally {
			con.close();
			System.out.println("Closing connecions");
		}
	}
}
