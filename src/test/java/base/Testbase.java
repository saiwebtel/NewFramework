package base;
import io.restassured.RestAssured;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import model.Consent;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import dbConnection.DataBaseConnection;
public class Testbase 
{
	public static String RequestServerIp=getRequestServerIP();
	public static String RequestServerPort=getRequestServerPORT();
	public HashMap<String,String> requestType;
	public HashMap<String,String> headerValues;
	public static HashMap<String,String> queryParams;
	protected Consent consent;
	public static Map<String, String> resultData;

	
	@BeforeSuite(enabled=true)
	public void beforeSuite() throws ClassNotFoundException, SQLException
	{
		RestAssured.baseURI = "http://"+getRequestServerIP()+":"+getRequestServerPORT()+"";
	}
	public HashMap<String,String> requestType()
	{
		requestType=new HashMap<>();
		requestType.put("ADDREMINDER", "addReminder");
		requestType.put("DELETEREMINDER", "deleteReminder");
		requestType.put("UPDATEREMINDER", "updateReminder");
		requestType.put("CUS", "cus");
		requestType.put("SETCONSENT", "setConsent");
		requestType.put("ADDWISHLIST", "addWishlist");
		requestType.put("DELETEWISHLIST", "deleteWishlist");
		requestType.put("CREATESWIMLANE", "deleteWishlist");
		
		return requestType;
	}
	public HashMap<String,String> headerValues(String requestType)
	{
		headerValues=new HashMap<>();
		if(requestType.equalsIgnoreCase("REST"))
		headerValues.put("Content-Type", "application/json");
		headerValues.put("Authorization", "Basic dmFpYmhhdjpjMWE4ZTA1OWJmZDFlOTExY2YxMGI2MjYzNDBjOWE1NA==");
		if(requestType.equalsIgnoreCase("BTA"))
		headerValues.put("Content-Type", "application/xml");
		
		return headerValues;
	}
	public HashMap<String,String> queryParams(String query,String DBName)
	{
		queryParams=new HashMap<>();		
		try {
				queryParams.put("MAC", executeSelectQuery(query,DBName).get("MACADDRESS"));
				if(queryParams.containsValue(null))
				{
					System.out.println("MAC is empty as data not available in table");
					System.exit(0);
				}				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		queryParams.put("InterfaceVersion", "5.2.0");
		return queryParams;
	}
	public void removeRecordFromTable(String query,String DBName) throws SQLException 
	{
		Statement statement = null;
		try{
			if(DBName.equalsIgnoreCase("TM")){
				statement = DataBaseConnection.getInstance().getDBConnection("TM").createStatement();}
			else if(DBName.equalsIgnoreCase("COMIC")){
				DataBaseConnection.getInstance();
				statement = DataBaseConnection.getInstance().getDBConnection("COMIC").createStatement();}
			statement.executeQuery(query);
		    }
	     
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		finally{
			statement.close();
			DataBaseConnection.closeConnection();
		}
	}	
	public static Properties getUpdatedProptiesFile() {
		Properties property = new Properties();
		FileInputStream FIS;
		try {
			FIS = new FileInputStream(System.getProperty("user.dir") +"/src/test/java/config/configuration.properties");
			property.load(FIS);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return property;
	}
	
	public static String getTMDBIP()
	{
		return getUpdatedProptiesFile().getProperty("Db_Ip");
	}
	public static String getDBServiceName()
	{
		return getUpdatedProptiesFile().getProperty("Db_Service_Name");
	}
	public static String getDBUserName(String dbUserName)
	{
		String dbUsername="";
		if (dbUserName == "TM") {
			dbUsername = getUpdatedProptiesFile().getProperty("Db_Username");
		} else if (dbUserName == "UUSD") {
			dbUsername = getUpdatedProptiesFile().getProperty("UUSDDB_Username");
		} else if (dbUserName == "COMIC") {
			dbUsername = getUpdatedProptiesFile().getProperty("COMICDB_Username");
		} else {
			System.out.println("INCORRECT USERNAME");
		}		
		return dbUsername;
	}
	public static String getDBPassword(String dbPassWord)
	{
		String dbPassword="";
		if (dbPassWord.equalsIgnoreCase("TM")) {
			dbPassword = getUpdatedProptiesFile().getProperty("Db_Password");
		} else if (dbPassWord.equalsIgnoreCase("UUSD")) {
			dbPassword = getUpdatedProptiesFile().getProperty("UUSDDB_Password");
		} else if (dbPassWord.equalsIgnoreCase("COMIC")) {
			dbPassword = getUpdatedProptiesFile().getProperty("COMICDB_Password");
		} else {
			System.out.println("INCORRECT PASSWORD");
		}
		return dbPassword;
	}
	public static String getRequestServerIP()
	{
		return getUpdatedProptiesFile().getProperty("Request_Server_Ip");
	}
	public static String getRequestServerPORT()
	{
		return getUpdatedProptiesFile().getProperty("Request_Server_Port");
	}
	@AfterSuite(enabled=true)
	public void afterSuite() throws SQLException{
		DataBaseConnection.closeConnection();
	
	}
	public static Map<String, String> executeSelectQuery(String sqlQuery, String DBName) throws SQLException {
		Map<String, String> resultData = new HashMap<String, String>();
		Statement statement = null;
		ResultSet resultset = null;
		ResultSetMetaData resultColumn = null;
		try {
			if(DBName.equalsIgnoreCase("TM")){
			statement = DataBaseConnection.getInstance().getDBConnection("TM").createStatement();}
			else if(DBName.equalsIgnoreCase("COMIC")){
			statement = DataBaseConnection.getInstance().getDBConnection("COMIC").createStatement();}
			else if(DBName.equalsIgnoreCase("UUSD")){
			statement = DataBaseConnection.getInstance().getDBConnection("UUSD").createStatement();}
			else{System.out.println("Wrong DB Name");
			}
			System.out.println("QUERY=>"+sqlQuery);
			resultset = statement.executeQuery(sqlQuery);
			resultColumn = resultset.getMetaData();
			resultset.next();
			for (int x = 1; x <= resultColumn.getColumnCount(); x++) {
				resultData.put(resultColumn.getColumnName(x),resultset.getString(x));
				System.out.println(resultData.put(resultColumn.getColumnName(x),resultset.getString(x)));	
			}        
			} catch (Exception ex) {
			System.out.println(ex.getMessage());
			} finally 
			{
			statement.close();
			resultset.close();
			DataBaseConnection.closeConnection();
			}
			if (resultData.size()== 0) {
				System.out.println("Record not exist in table");
				System.exit(0);}
			return resultData;
	}	
}


