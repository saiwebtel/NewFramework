package Exposure.RestAssured;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import model.Consent;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import testData.TestDataCreation;
import RestAPIHelper.RestUtil;
import base.Testbase;
import dbConnection.DataBaseConnection;

public class setConsentTest extends Testbase{
	TestDataCreation testdatacreation=new TestDataCreation();
	public static String deleteConsentQuery;
	public static String query="SELECT STB.MACADDRESS,S.ACCOUNTNUMBER,cna.SUBSCRIBER_NA  FROM subscribers s,customer_na@TO_UUSD cna,SETTOPBOXES STB  WHERE S.ACCOUNTNUMBER=CNA.SUBSCRIBER_NA AND STB.ASSIGNEDTOSUBSCRIBERID=CNA.SUBSCRIBER_ID AND S.ID=CNA.SUBSCRIBER_ID AND CNA.STATUS_CODE='A' AND S.STATUSCODE = 'A' AND STB.STATUS='Assigned'  AND STB.RECORDSTATUSCODE='A' AND rownum < 1000";
	//public static String deleteConsentQuery="delete FROM consents WHERE id > ( ( SELECT MAX( id ) FROM consents ) - 1 )";
	public static String getConsentQuery="select TVID,CONSENTTYPE,CONSENTVALUE,LASTUPDATEBY FROM consents WHERE id > ( ( SELECT MAX( id ) FROM consents ) - 1)";
	public static String CLIENTSETINTEGRATIONVALUE="Select CONSENTTYPE,CLIENTSETINTEGRATION from CONSENTTYPES where CONSENTTYPE='TVTA'";
	
	@BeforeMethod
	public void beforeTest() throws SQLException
	{
		RestAssured.basePath = "/broker/bta/setConsent";
	}
	@Test(priority=1)
	public void setConsentTVTA_OPTIN_BEP1423001() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
	{
		
		removeRecordFromTable("delete FROM consents WHERE TVID='"+getRecordFromTable(query,"TM").get("ACCOUNTNUMBER")+"'","COMIC");
		//Sending request
		Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForConsent("setConsent", "TVTA", "OPTIN"), headerValues(), queryParams(query,"TM"));
		System.out.println(response.prettyPrint());
		//Validating Response
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.Status"),"Success");
		//Assert.assertEquals(getRecordFromTable(getConsentQuery,"COMIC").get("CONSENTTYPE"),"TVTA");
		//Assert.assertEquals(getRecordFromTable(getConsentQuery,"COMIC").get("CONSENTVALUE"),"OPTIN");
		//removeRecordFromTable(deleteConsentQuery);
		storeDataInPojo("select * from consents WHERE TVID="+"'"+getRecordFromTable(query,"TM").get("ACCOUNTNUMBER")+"'", "COMIC");
		Assert.assertEquals(consent.getConsentType(),"TVTA");
		Assert.assertEquals(consent.getConsentValue(),"OPTIN");
	}

	@Test(priority=2)
	public void setConsentTVReco_OPTIN_BEP1423002() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
	{
		//Sending request
		Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForConsent("setConsent", "TVTA", "OPTOUT"), headerValues(), queryParams(query,"TM"));
		//Validating Response
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.Status"),"Success");
		Assert.assertEquals(getRecordFromTable(getConsentQuery,"COMIC").get("CONSENTTYPE"),"TVTA");
		Assert.assertEquals(getRecordFromTable(getConsentQuery,"COMIC").get("CONSENTVALUE"),"OPTOUT");
		removeRecordFromTable("delete FROM consents WHERE TVID='"+getRecordFromTable(query,"TM").get("ACCOUNTNUMBER")+"'","COMIC");
		//removeRecordFromTable(deleteConsentQuery);
	}
	
	@Test(priority=3)
	public void setConsentTVTA_OPTOUT_BEP1423003() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
	{
	//Sending request
		Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForConsent("setConsent", "TVReco", "OPTIN"), headerValues(), queryParams(query,"TM"));
		System.out.println(response.prettyPrint());
		//Validating Response
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.Status"),"Success");
		Assert.assertEquals(getRecordFromTable(getConsentQuery,"COMIC").get("CONSENTTYPE"),"TVReco");
		Assert.assertEquals(getRecordFromTable(getConsentQuery,"COMIC").get("CONSENTVALUE"),"OPTIN");
		//removeRecordFromTable(deleteConsentQuery);
		}
	@Test(priority=4)
	public void setConsentTVReco_OPTOUT_BEP1423004() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
	{
		Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForConsent("setConsent", "TVReco", "OPTOUT"), headerValues(), queryParams(query,"TM"));
		System.out.println(response.prettyPrint());
		//Validating Response
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.Status"),"Success");
		Assert.assertEquals(getRecordFromTable(getConsentQuery,"COMIC").get("CONSENTTYPE"),"TVReco");
		Assert.assertEquals(getRecordFromTable(getConsentQuery,"COMIC").get("CONSENTVALUE"),"OPTOUT");
	
	}
	@Test(priority=5)
	public void setConsentwithInvalidConsentType_BEP1423005() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
	{
		Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForConsent("setConsent", "TVReco1", "OPTOUT"), headerValues(), queryParams(query,"TM"));
		System.out.println(response.prettyPrint());
		//Validating Response
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"ConsentType Invalid");
	}
	@Test(priority=5)
	public void setConsentwithInvalidConsentValue_BEP1423006() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
	{
		Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForConsent("setConsent", "TVReco", "OPTOUT1"), headerValues(), queryParams(query,"TM"));
		System.out.println(response.prettyPrint());
		//Validating Response
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Mandatory Parameter ConsentValue should be either OPTIN or OPTOUT");
	}
}
