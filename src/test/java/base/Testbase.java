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
	public static String DbIp=getTMDBIP().split(",")[0];
	public static String TmDbUserName=getDBUserName().split(",")[0];
	public static String TmDbPassword=getDBPassword().split(",")[0];
	public static String ComicDbUserName=getDBUserName().split(",")[1];
	public static String ComicDbPassword=getDBPassword().split(",")[1];
	public static String UusdDbUserName=getDBUserName().split(",")[2];
	public static String UusdDbPassword=getDBPassword().split(",")[2];	
	public static String ServiceName=getDBServiceName().split(",")[0];
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
		
		return requestType;
	}
	public HashMap<String,String> headerValues()
	{
		headerValues=new HashMap<>();
		headerValues.put("Content-Type", "application/xml");
		return headerValues;
	}
	public HashMap<String,String> queryParams(String query,String DBName)
	{
		queryParams=new HashMap<>();		
		try {
				queryParams.put("MAC", getRecordFromTable(query,DBName).get("MACADDRESS"));
				
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		queryParams.put("InterfaceVersion", "5.2.0");
		return queryParams;
	}
	public Map<String, String> getRecordFromTable(String query,String DBName) throws SQLException
	{
		Map<String, String> testData=new HashMap<>();
		testData= Testbase.executeSelectQuery(query,DBName);	
		//removeRecordFromTable(ReminderTests.deleteQuery);		
		return testData;
	}

	public void removeRecordFromTable(String query,String DBName) throws SQLException 
	{
		Statement statement = null;
		try{
			if(DBName.equalsIgnoreCase("TM")){
				statement = DataBaseConnection.getDBConnection(DbIp,TmDbUserName,TmDbPassword,ServiceName).createStatement();}
			else if(DBName.equalsIgnoreCase("COMIC")){
				statement = DataBaseConnection.getDBConnection(DbIp,ComicDbUserName,ComicDbPassword,ServiceName).createStatement();}
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

	public boolean checkIfRecordExist(String Query,String DBName) throws SQLException
	{
		boolean record=true;
		Map<String, String> resultSetMap=new HashMap<>();
		resultSetMap=executeSelectQuery(Query, DBName);
		if(resultSetMap.size()!=0)
		{
			record=false;
		}
		
		return record;
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
	public static String getDBUserName()
	{
		return getUpdatedProptiesFile().getProperty("Db_Username");
	}
	public static String getDBPassword()
	{
		return getUpdatedProptiesFile().getProperty("Db_Password");
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
			if(DBName.equalsIgnoreCase("TM"))
			{
			statement = DataBaseConnection.getDBConnection(DbIp,TmDbUserName,TmDbPassword,ServiceName).createStatement();
			}
			else if(DBName.equalsIgnoreCase("COMIC")){
			statement = DataBaseConnection.getDBConnection(DbIp, ComicDbUserName, ComicDbPassword, ServiceName).createStatement();
			}
			else if(DBName.equalsIgnoreCase("UUSD")){
				statement = DataBaseConnection.getDBConnection(DbIp, UusdDbUserName, UusdDbPassword, ServiceName).createStatement();
				}
			else
			{
				System.out.println("Wrong DB Name");
			}
			resultset = statement.executeQuery(sqlQuery);
			resultColumn = resultset.getMetaData();
			int noOfColumns = resultColumn.getColumnCount();
			System.out.println("NUMBER OF COLUmNS ARE="+noOfColumns);
			resultset.next();
			for (int x = 1; x <= noOfColumns; x++) {
				System.out.println(resultColumn.getColumnName(x) + " ------> "+ resultset.getString(x));
				resultData.put(resultColumn.getColumnName(x),resultset.getString(x));
				//resultset.next();
				
			}
			/*File file=new File("data");
	        FileWriter fw=new FileWriter(file.getAbsoluteFile());
	        BufferedWriter bw=new BufferedWriter(fw);
	        bw.write(resultData.toString());
	        bw.close();*/
	        
			} catch (Exception ex) {
			System.out.println(ex.getMessage());
			} finally 
			{
			statement.close();
			resultset.close();
			DataBaseConnection.closeConnection();
			}
			return resultData;

	}
	public void storeDataInPojo(String Query ,String DBName) throws ClassNotFoundException, SQLException
	{
		Statement statement = null;
		ResultSet resultset = null;
		try{
		
		if(DBName.equalsIgnoreCase("TM"))
		{
		statement = DataBaseConnection.getDBConnection(DbIp,TmDbUserName,TmDbPassword,ServiceName).createStatement();
		}
		else if(DBName.equalsIgnoreCase("COMIC")){
		statement = DataBaseConnection.getDBConnection(DbIp, ComicDbUserName, ComicDbPassword, ServiceName).createStatement();
		}
		else
		{
			System.out.println("Query or DB Name is not correct");
		}
		resultset = statement.executeQuery(Query);
		while(resultset.next())
		{
			consent=new Consent();
			consent.setID(resultset.getInt(1));
			consent.setTVID(resultset.getString(2));
			consent.setConsentType(resultset.getString(3));
			consent.setConsentValue(resultset.getString(4));
			consent.setConsentStatus(resultset.getString(5));
			consent.setLastUpdatedOn(resultset.getLong(6));
			consent.setLastUpdatedBy(resultset.getString(7));
			consent.setConsentMessage(resultset.getString(8));
			consent.setLastDerivedOn(resultset.getInt(9));
			consent.setRDSRetryCounter(resultset.getInt(10));
			consent.setConsentFeedback(resultset.getInt(11));
			System.out.println("CONSENT TYPE IS="+consent.getConsentType());
		}}
		catch(Exception e)
		{
			
		}
		finally
		{
			statement.close();
			resultset.close();
			DataBaseConnection.closeConnection();
		}
		
	}
	public static void main(String[] args) throws SQLException, ClassNotFoundException 
	{
			Testbase ts=new Testbase();
			//database=new DataBaseConnection();
			//System.out.println(ts.checkIfRecordExist("select * from settopboxreminders where MACADDRESS='24374CFFD3FA'"));
			//System.out.println(ts.getReminderDataFromSETTOPBOXREMINDERSTable().get("MACADDRESS"));
			//ts.storeDataInPojo("select * fom consents where TVID=014372354", "COMIC");
	}
	
}


