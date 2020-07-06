package Exposure.RestAssured;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import testData.TestDataCreation;
import RestAPIHelper.RestUtil;
import base.Testbase;
import dbConnection.DataBaseConnection;

public class ReminderTests extends Testbase {
	TestDataCreation testdatacreation=new TestDataCreation();
	public static String query;
	public static String deleteQuery;
	public static String selectQuery;
	public static String updateQuery;
	public static String reminderData;
	public static Map<String, String> queryData;
	ReminderTests() throws SQLException
	{
		query="SELECT STB.MACADDRESS, DTV.EXTERNALID, SFS.SCHEDULETRAILID FROM subscribers s,customer_na@TO_UUSD cna,"
				+ "SETTOPBOXES STB,subscriberpackages sp, packageitems pi,dtvchannels dtv,schedulefilespec sfs "
				+ "WHERE S.ID = CNA.SUBSCRIBER_ID "
				+ "AND S.ID = STB.ASSIGNEDTOSUBSCRIBERID "
				+ "AND SP.SUBSCRIBERID = S.ID "
				+ "AND SP.PACKAGEID = PI.PACKAGEID "
				+ "AND PI.ITEMID = DTV.ID "
				+ "AND DTV.CHANNELREFERENCENO = SFS.CHANNELREFERENCENUMBER "
				+ "AND S.STATUS = 'Active' "
				+ "AND S.STATUSCODE = 'A' "
				+ "AND STB.STATUS='Assigned'  "
				+ "AND STB.RECORDSTATUSCODE='A' "
				+ "AND STARTDATETIME>sysdate "
				+ "AND rownum < 100 order by SFS.SCHEDULETRAILID desc";
		updateQuery="SELECT STB.MACADDRESS, DTV.ID,DTV.EXTERNALID,SFS.PROGRAMREFERENCENUMBER,SFS.CHANNELREFERENCENUMBER, SFS.SCHEDULETRAILID "
				+ "FROM subscribers s,customer_na@TO_UUSD cna,"
				+ "SETTOPBOXES STB,subscriberpackages sp, packageitems pi,dtvchannels dtv,schedulefilespec sfs "
				+ "WHERE S.ID = CNA.SUBSCRIBER_ID "
				+ "AND S.ID = STB.ASSIGNEDTOSUBSCRIBERID "
				+ "AND SP.SUBSCRIBERID = S.ID "
				+ "AND SP.PACKAGEID = PI.PACKAGEID "
				+ "AND PI.ITEMID = DTV.ID "
				+ "AND DTV.CHANNELREFERENCENO = SFS.CHANNELREFERENCENUMBER "
				+ "AND S.STATUS = 'Active' "
				+ "AND S.STATUSCODE = 'A' "
				+ "AND STB.STATUS='Assigned'  "
				+ "AND STB.RECORDSTATUSCODE='A' "
				+ "AND STARTDATETIME>sysdate "
				+ "AND rownum < 500 order by SFS.SCHEDULETRAILID desc";
		queryData=getRecordFromTable(query,"TM");
		deleteQuery="delete from SETTOPBOXREMINDERS WHERE MACADDRESS="+"'"+queryData.get("MACADDRESS")+"'";
		//deleteQuery="delete from SETTOPBOXREMINDERS WHERE MACADDRESS="+"'"+getRecordFromTable(query,"TM").get("MACADDRESS")+"'";
		reminderData="SELECT * FROM SETTOPBOXREMINDERS WHERE SETTOPBOXREMINDERS.REMINDERCREATIONDATETIME IS NOT NULL "
				+ "and SETTOPBOXREMINDERS.REMINDERCREATIONDATETIME>sysdate";
		
	}
	
	
	@BeforeMethod
	public void beforeTest() throws SQLException
	{
		RestAssured.basePath = "/broker/bta/addReminder";
		//RestAssured.baseURI = "http://"+getRequestServerIP()+":"+getRequestServerPORT()+"";

	}
	@Test(priority=1)

	public void addReminderWithInterfaceVersion520_BEP14470001() throws JAXBException, ClassNotFoundException, SQLException, IOException 
	{	
		//Removing already existing record	
			removeRecordFromTable(deleteQuery,"TM");
		//Sending Request
			Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForReminder("addReminder"), headerValues(), queryParams(query,"TM"));
			System.out.println(response.prettyPrint());
		//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ID"),getRecordFromTable(reminderData,"TM").get("REMINDERID"));
			Assert.assertEquals("Reminder",getRecordFromTable(reminderData,"TM").get("REMINDERTYPE"));
			
	}
	 @Test(priority=2)
	public void addReminderWithInterfaceVersion420_BEP14470004() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
	{
		 	removeRecordFromTable(deleteQuery,"TM");
		 	queryParams(query,"TM").put("InterfaceVersion", "4.2.0");
		 	Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForReminder("addReminder"), headerValues(), queryParams(query,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ID"),getRecordFromTable(reminderData,"TM").get("REMINDERID"));
			Assert.assertEquals("Reminder",getRecordFromTable(reminderData,"TM").get("REMINDERTYPE"));
	}
	 @Test(priority=3)
		public void addReminderWithoutInterfaceVersion_BEP14470002() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	queryParams(query,"TM").remove("InterfaceVersion");
		 	Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForReminder("addReminder"), headerValues(), queryParams);
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Mandatory Parameter Interface Version missing");
		}
	 @Test(priority=4)
		public void addReminderWithInterfaceVersionLessThan420_BEP14470003() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	queryParams(query,"TM").put("InterfaceVersion", "4.1.0");
		 	Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForReminder("addReminder"), headerValues(), queryParams);
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Invalid Interface version");
		}
	 @Test(priority=5)
		public void addReminderWithoutMac_BEP14470005() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	queryParams(query,"TM").remove("MAC");
		 	Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForReminder("addReminder"), headerValues(), queryParams);
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Request header for remote ipaddress does not exist or it may be empty");
		}
	 @Test(priority=6)
		public void addReminderWithInvalidMac_BEP14470006() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	//Sending Request
		 	queryParams(query,"TM").put("MAC","24374CFFD3FATEST");
			Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForReminder("addReminder"), headerValues(), queryParams);
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Invalid MAC.");
		}
	 @Test(priority=7)
		public void addReminderWithBlankValueForReminderType_BEP14470007() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{

			String postBody = testdatacreation.createPOSTBodyForReminder("addReminder").replaceAll("<ReminderType>Reminder</ReminderType>", "<ReminderType></ReminderType>");
			System.out.println("FINAL BODY IS ====="+postBody);
			//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(query,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Reminder Type tag is either missing or empty");
		}
	 @Test(priority=8)
		public void addReminderWithoutReminderTag_BEP14470008() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	String postBody = testdatacreation.createPOSTBodyForReminder("addReminder").replaceAll("<ReminderType>Reminder</ReminderType>", "");
			System.out.println("FINAL BODY IS ====="+postBody);
			//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(query,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Reminder Type tag is either missing or empty");
		}
	 @Test(priority=9)
		public void addReminderWithBlankValueforMinsBeforeStart_BEP14470009() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
			String postBody = testdatacreation.createPOSTBodyForReminder("addReminder").replaceAll("<MinsBeforeStart>8</MinsBeforeStart>", "<MinsBeforeStart></MinsBeforeStart>");
			//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(query,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Mins Before Start tag is either missing/empty/incorrect");
		}
	 @Test(priority=10)
		public void addReminderWithoutMinsBeforeStartTag_BEP144700010() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	String postBody = testdatacreation.createPOSTBodyForReminder("addReminder").replaceAll("<MinsBeforeStart>8</MinsBeforeStart>", "");
			//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(query,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Mins Before Start tag is either missing/empty/incorrect");
		}
	 @Test(priority=11)
		public void addReminderWithBlankValueForSchTrailID_BEP144700011() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	String postBody=testdatacreation.createPOSTBodyForReminder("addReminder").replaceAll("<SchTrailID>"+getRecordFromTable(query,"TM").get("SCHEDULETRAILID")+"</SchTrailID>", "<SchTrailID></SchTrailID>");
		 //Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(query,"TM"));
			System.out.println(response.prettyPrint());
		//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Schedule Trail Id tag is either missing/empty/incorrect");
		}
	 @Test(priority=12)
		public void addReminderWithoutSchTrailIDTag_BEP144700012() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	String postBody=testdatacreation.createPOSTBodyForReminder("addReminder").replaceAll("<SchTrailID>"+getRecordFromTable(query,"TM").get("SCHEDULETRAILID")+"</SchTrailID>", "");
		 //Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(query,"TM"));
			System.out.println(response.prettyPrint());
		//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Schedule Trail Id tag is either missing/empty/incorrect");
		}
	 @Test(priority=13)
		public void addReminderWithBlankValueForChannelExtID_BEP144700013() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	String postBody=testdatacreation.createPOSTBodyForReminder("addReminder").replaceAll("<ChannelExtID>"+getRecordFromTable(query,"TM").get("EXTERNALID")+"</ChannelExtID>", "<ChannelExtID></ChannelExtID>");
			//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(query,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Channel External Id tag is either missing/empty");
		}
	 @Test(priority=14)
		public void addReminderWithoutChannelExtID_BEP144700014() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	String postBody=testdatacreation.createPOSTBodyForReminder("addReminder").replaceAll("<ChannelExtID>"+getRecordFromTable(query,"TM").get("EXTERNALID")+"</ChannelExtID>", "");
			//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(query,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);

			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Channel External Id tag is either missing/empty");
		}
	 @Test(priority=15)
		public void addReminderWhenIcorrectSchTrailID_BEP144700016() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	String postBody=testdatacreation.createPOSTBodyForReminder("addReminder").replaceAll("<SchTrailID>"+getRecordFromTable(query,"TM").get("SCHEDULETRAILID")+"</SchTrailID>", "<SchTrailID>"+getRecordFromTable(query,"TM").get("SCHEDULETRAILID")+"99"+"</SchTrailID>");
		 	//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(query,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Schedule Trail Id does not exists");
		}
	 @Test(priority=16)
		public void addReminderWhenIcorrectChannelExtID_BEP144700017() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{			
			String postBody=testdatacreation.createPOSTBodyForReminder("addReminder").replaceAll("<ChannelExtID>"+getRecordFromTable(query,"TM").get("EXTERNALID")+"</ChannelExtID>", "<ChannelExtID>"+getRecordFromTable(query,"TM").get("EXTERNALID")+"99"+"</ChannelExtID>");
			//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(query,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Channel External Id does not exists");
		}
	 @Test(priority=17)
		public void addReminderWhenMinsBeforeStartIsnotCorrect_BEP144700018() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
			String postBody=testdatacreation.createPOSTBodyForReminder("addReminder").replaceAll("<MinsBeforeStart>8</MinsBeforeStart>", "<MinsBeforeStart>101</MinsBeforeStart>");
			//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(query,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response

			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Mins Before Start value should be between 0 and 99");
		}
	  @Test(priority=18)
	 public void getReminderBTACall_BEP14477001() throws SQLException, ClassNotFoundException
	 {
		 	RestAssured.basePath = "/broker/bta/getReminders";		 	
		 	//QueryParams
		 	Map<String, String> queryParams = new HashMap<String, String>();
			queryParams.put("MAC", getRecordFromTable(reminderData,"TM").get("MACADDRESS"));
			queryParams.put("InterfaceVersion", "4.2.0");		
			//Sending Request
			Response response =  RestUtil.sendGetAPI("application/xml", queryParams);
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			System.out.println(response.asString());		
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.Reminders.Reminder.ID"),getRecordFromTable(reminderData,"TM").get("REMINDERID"));			
	 }
	  @Test(priority=19)
	  public void getReminderBTACall_BEP14477002() throws SQLException, ClassNotFoundException
		 {
			 	RestAssured.basePath = "/broker/bta/getReminders";		 	
			 	//QueryParams
			 	Map<String, String> queryParams = new HashMap<String, String>();
				queryParams.put("MAC", getRecordFromTable(reminderData,"TM").get("MACADDRESS"));	
				//Sending Request
				Response response =  RestUtil.sendGetAPI("application/xml", queryParams);
				//Validating Response
				Assert.assertEquals(response.getStatusCode(), 200);
				System.out.println(response.asString());		
				Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Mandatory Parameter Interface Version missing");			
		 }
	  @Test(priority=20)
	  public void getReminderBTACall_BEP14477003() throws SQLException, ClassNotFoundException
		 {
			 	RestAssured.basePath = "/broker/bta/getReminders";		 	
			 	//QueryParams
			 	Map<String, String> queryParams = new HashMap<String, String>();
				queryParams.put("MAC", getRecordFromTable(reminderData,"TM").get("MACADDRESS"));	
				queryParams.put("InterfaceVersion", "4.1.0");
				//Sending Request
				Response response =  RestUtil.sendGetAPI("application/xml", queryParams);
				//Validating Response
				Assert.assertEquals(response.getStatusCode(), 200);
				System.out.println(response.asString());		
				Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Invalid Interface version");			
		 }
	  @Test(priority=21)
	  public void getReminderBTACall_BEP14477004() throws SQLException, ClassNotFoundException
		 {
			 	RestAssured.basePath = "/broker/bta/getReminders";		 	
			 	//QueryParams
			 	Map<String, String> queryParams = new HashMap<String, String>();
				queryParams.put("MAC", getRecordFromTable(reminderData,"TM").get("MACADDRESS")+"TEST");	
				queryParams.put("InterfaceVersion", "4.2.0");
				//Sending Request
				Response response =  RestUtil.sendGetAPI("application/xml", queryParams);
				//Validating Response
				Assert.assertEquals(response.getStatusCode(), 200);
				System.out.println(response.asString());		
				Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Invalid MAC.");			
		 }
	  @Test(priority=22)
	  public void getReminderBTACall_BEP14477005() throws SQLException, ClassNotFoundException
		 {
			 	RestAssured.basePath = "/broker/bta/getReminders";		 	
			 	//QueryParams
			 	Map<String, String> queryParams = new HashMap<String, String>();
				queryParams.put("MAC", "");	
				queryParams.put("InterfaceVersion", "4.2.0");
				//Sending Request
				Response response =  RestUtil.sendGetAPI("application/xml", queryParams);
				//Validating Response
				Assert.assertEquals(response.getStatusCode(), 200);
				System.out.println(response.asString());		
				Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Mandatory parameter is missing. Supported types are MAC, DeviceID, AccountNumber as request parameter or GID as request header.");			
		 }
	  @Test(priority=23)
		public void updateReminder_BEP14471001() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		  	RestAssured.basePath = "/broker/bta/updateReminder";
			//Sending Request
			Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForReminder("updateReminder"), headerValues(), queryParams(updateQuery,"TM"));
			/*System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.Status"),"Success");
			Assert.assertEquals("Autotune",getRecordFromTable(reminderData,"TM").get("REMINDERTYPE"));
			Assert.assertEquals("10",getRecordFromTable(reminderData,"TM").get("REMINDERMINUTESBEFORESTART"),"Paased");
			Assert.assertEquals(getRecordFromTable(updateQuery,"TM").get("PROGRAMREFERENCENUMBER"),getRecordFromTable(reminderData,"TM").get("PROGRAMREFERENCENUMBER"));
			Assert.assertEquals(getRecordFromTable(updateQuery,"TM").get("CHANNELREFERENCENUMBER"),getRecordFromTable(reminderData,"TM").get("CHANNELREFERENCENUMBER"));
			Assert.assertEquals(getRecordFromTable(updateQuery,"TM").get("ID"),getRecordFromTable(reminderData,"TM").get("REALCHANNELIDS"));
*/			
		}
	  @Test(priority=24)
		public void updateReminderWithoutInterfaceVersion_BEP14471002() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		  	queryParams(query,"TM").remove("InterfaceVersion");	
		  	RestAssured.basePath = "/broker/bta/updateReminder";
			//Sending Request
			Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForReminder("updateReminder"), headerValues(), queryParams);
			System.out.println(response.prettyPrint());
			//Validating Response

			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Mandatory Parameter Interface Version missing");
		}
	  @Test(priority=25)
		public void updateReminderWhenInterfaceVersionLessThen420_BEP14471003() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		  	queryParams(query,"TM").put("InterfaceVersion", "4.1.0");
		  	RestAssured.basePath = "/broker/bta/updateReminder";
			//Sending Request
			Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForReminder("updateReminder"), headerValues(), queryParams);
			System.out.println(response.prettyPrint());
			//Validating Response

			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Invalid Interface version");
		}
	  @Test(priority=26)
		public void updateReminderWithoutMac_BEP14471004() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		  	queryParams(query,"TM").remove("MAC");
		  	RestAssured.basePath = "/broker/bta/updateReminder";
			//Sending Request
			Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForReminder("updateReminder"), headerValues(), queryParams);
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Request header for remote ipaddress does not exist or it may be empty");
		}
	  @Test(priority=27)
		public void updateReminderWithInvalidMac_BEP14471005() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		  	queryParams(query,"TM").put("MAC","24374CFFD3FATEST");
		  	RestAssured.basePath = "/broker/bta/updateReminder";
			//Sending Request
			Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForReminder("updateReminder"), headerValues(), queryParams);
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Invalid MAC.");
		}
	  @Test(priority=28)
		public void updateReminderWithBlankValueForID_BEP14471006() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		  	RestAssured.basePath = "/broker/bta/updateReminder";
			//Sending Request
		  	String postBody=testdatacreation.createPOSTBodyForReminder("updateReminder").replaceAll("<ID>"+getRecordFromTable(reminderData,"TM").get("REMINDERID")+"</ID>", "<ID></ID>");;
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(updateQuery,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Reminder ID tag is either missing or empty");
		}
	  @Test(priority=29)
		public void updateReminderWithoutIDTag_BEP14471007() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		  	RestAssured.basePath = "/broker/bta/updateReminder";
			//Sending Request
		  	String postBody=testdatacreation.createPOSTBodyForReminder("updateReminder").replaceAll("<ID>"+getRecordFromTable(reminderData,"TM").get("REMINDERID")+"</ID>", "");;
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(updateQuery,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Reminder ID tag is either missing or empty");
		}
	  @Test(priority=30)
		public void updateReminderWithBlankValueForReminderType_BEP14471008() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		  	RestAssured.basePath = "/broker/bta/updateReminder";
			String postBody = testdatacreation.createPOSTBodyForReminder("updateReminder").replaceAll("<ReminderType>Autotune</ReminderType>", "<ReminderType></ReminderType>");
			//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(updateQuery,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Reminder Type tag is either missing or empty");
		}
	 @Test(priority=31)
		public void updateReminderWithoutReminderTag_BEP14471009() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 RestAssured.basePath = "/broker/bta/updateReminder";	
		 String postBody = testdatacreation.createPOSTBodyForReminder("updateReminder").replaceAll("<ReminderType>Autotune</ReminderType>", "");
			//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(updateQuery,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Reminder Type tag is either missing or empty");
		}
	 @Test(priority=32)
		public void updateReminderWithBlankValueforMinsBeforeStart_BEP144710010() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	RestAssured.basePath = "/broker/bta/updateReminder";
		 	String postBody = testdatacreation.createPOSTBodyForReminder("updateReminder").replaceAll("<MinsBeforeStart>10</MinsBeforeStart>", "<MinsBeforeStart></MinsBeforeStart>");
			//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(updateQuery,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Mins Before Start tag is either missing/empty/incorrect");
		}
	 @Test(priority=33)
		public void updateReminderWithoutMinsBeforeStartTag_BEP144710011() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	RestAssured.basePath = "/broker/bta/updateReminder";
		 	String postBody = testdatacreation.createPOSTBodyForReminder("updateReminder").replaceAll("<MinsBeforeStart>10</MinsBeforeStart>", "");
			//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(updateQuery,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Mins Before Start tag is either missing/empty/incorrect");
		}
	 @Test(priority=34)
		public void updateReminderWithBlankValueForSchTrailID_BEP144710012() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	RestAssured.basePath = "/broker/bta/updateReminder";
		 	String postBody=testdatacreation.createPOSTBodyForReminder("updateReminder").replaceAll("<SchTrailID>"+getRecordFromTable(updateQuery,"TM").get("SCHEDULETRAILID")+"</SchTrailID>", "<SchTrailID></SchTrailID>");
		 //Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(updateQuery,"TM"));
			System.out.println(response.prettyPrint());
		//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Schedule Trail Id tag is either missing/empty/incorrect");
		}
	 @Test(priority=35)
		public void updateReminderWithoutSchTrailIDTag_BEP144710013() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	RestAssured.basePath = "/broker/bta/updateReminder";
		 	String postBody=testdatacreation.createPOSTBodyForReminder("updateReminder").replaceAll("<SchTrailID>"+getRecordFromTable(updateQuery,"TM").get("SCHEDULETRAILID")+"</SchTrailID>", "");
		 //Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(updateQuery,"TM"));
			System.out.println(response.prettyPrint());
		//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Schedule Trail Id tag is either missing/empty/incorrect");
		}
	 @Test(priority=36)
		public void updateReminderWithBlankValueForChannelExtID_BEP144710014() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	RestAssured.basePath = "/broker/bta/updateReminder";	
		 	String postBody=testdatacreation.createPOSTBodyForReminder("updateReminder").replaceAll("<ChannelExtID>"+getRecordFromTable(updateQuery,"TM").get("EXTERNALID")+"</ChannelExtID>", "<ChannelExtID></ChannelExtID>");
			//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(updateQuery,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Channel External Id tag is either missing/empty");
		}
	 @Test(priority=37)
		public void updateReminderWithoutChannelExtID_BEP144710015() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	RestAssured.basePath = "/broker/bta/updateReminder";
		 	String postBody=testdatacreation.createPOSTBodyForReminder("updateReminder").replaceAll("<ChannelExtID>"+getRecordFromTable(updateQuery,"TM").get("EXTERNALID")+"</ChannelExtID>", "");
			//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(updateQuery,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);

			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Channel External Id tag is either missing/empty");
		}
	 @Test(priority=38)
		public void updateReminderWhenIcorrectSchTrailID_BEP144710018() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	RestAssured.basePath = "/broker/bta/updateReminder";	
		 	String postBody=testdatacreation.createPOSTBodyForReminder("updateReminder").replaceAll("<SchTrailID>"+getRecordFromTable(updateQuery,"TM").get("SCHEDULETRAILID")+"</SchTrailID>", "<SchTrailID>"+getRecordFromTable(updateQuery,"TM").get("SCHEDULETRAILID")+"99"+"</SchTrailID>");
		 	//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(updateQuery,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Schedule Trail Id does not exists");
		}
	 @Test(priority=39)
		public void updateReminderWhenIcorrectChannelExtID_BEP144710019() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{			
		 	RestAssured.basePath = "/broker/bta/updateReminder";
		 	String postBody=testdatacreation.createPOSTBodyForReminder("updateReminder").replaceAll("<ChannelExtID>"+getRecordFromTable(updateQuery,"TM").get("EXTERNALID")+"</ChannelExtID>", "<ChannelExtID>"+getRecordFromTable(updateQuery,"TM").get("EXTERNALID")+"99"+"</ChannelExtID>");
			//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(updateQuery,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Channel External Id does not exists");
		}
	 @Test(priority=40)
		public void updateReminderWhenMinsBeforeStartIsnotCorrect_BEP144710020() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	RestAssured.basePath = "/broker/bta/updateReminder";	
		 	String postBody=testdatacreation.createPOSTBodyForReminder("updateReminder").replaceAll("<MinsBeforeStart>10</MinsBeforeStart>", "<MinsBeforeStart>101</MinsBeforeStart>");
			//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(updateQuery,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Mins Before Start value should be between 0 and 99");
		}
	 @Test(priority=41)
		public void updateReminderWhenIcorrectSchTrailID_BEP144710021() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
		{
		 	RestAssured.basePath = "/broker/bta/updateReminder";	
		 	String postBody=testdatacreation.createPOSTBodyForReminder("updateReminder").replaceAll("<ID>"+getRecordFromTable(reminderData,"TM").get("REMINDERID")+"</ID>", "<ID>"+getRecordFromTable(reminderData,"TM").get("REMINDERID")+"99"+"</ID>");
		 	//Sending Request
			Response response =  RestUtil.sendPostAPI(postBody, headerValues(), queryParams(updateQuery,"TM"));
			System.out.println(response.prettyPrint());
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"No Reminder present for the requested MAC with this ID");
		}
	 @AfterMethod
	 public void afterTest() throws SQLException
	 {
		 //addReminderData.deleteTestData(Queries.deleteReminderQuery,Testbase.tmdbUserName,Testbase.tmdbPassword);

	 }
}
