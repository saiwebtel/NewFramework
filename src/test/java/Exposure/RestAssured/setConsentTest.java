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

import testData.QueryRepository;
import testData.TestDataCreation;
import RestAPIHelper.RestUtil;
import base.Testbase;
import dbConnection.DataBaseConnection;

public class setConsentTest extends Testbase{
	TestDataCreation testdatacreation=new TestDataCreation();
	@BeforeMethod
	public void beforeTest() throws SQLException
	{
		RestAssured.basePath = "/broker/bta/setConsent";
	}
	@Test(priority=1)
	public void setConsentTVTA_OPTIN_BEP1423001() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
	{
		
		removeRecordFromTable("delete FROM consents WHERE TVID='"+executeSelectQuery(QueryRepository.Consent.query,"TM").get("ACCOUNTNUMBER")+"'","COMIC");
		//Sending request
		Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForConsent("setConsent", "TVTA", "OPTIN"), headerValues("BTA"), queryParams(QueryRepository.Consent.query,"TM"));
		System.out.println(response.prettyPrint());
		//Validating Response
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.Status"),"Success");
		//Assert.assertEquals(getRecordFromTable(getConsentQuery,"COMIC").get("CONSENTTYPE"),"TVTA");
		//Assert.assertEquals(getRecordFromTable(getConsentQuery,"COMIC").get("CONSENTVALUE"),"OPTIN");
		//removeRecordFromTable(deleteConsentQuery);
		//Assert.assertEquals(consent.getConsentType(),"TVTA");
		//Assert.assertEquals(consent.getConsentValue(),"OPTIN");
	}

	@Test(priority=2)
	public void setConsentTVReco_OPTIN_BEP1423002() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
	{
		//Sending request
		Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForConsent("setConsent", "TVTA", "OPTOUT"), headerValues("BTA"), queryParams(QueryRepository.Consent.query,"TM"));
		//Validating Response
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.Status"),"Success");
		Assert.assertEquals(executeSelectQuery(QueryRepository.Consent.getConsentQuery,"COMIC").get("CONSENTTYPE"),"TVTA");
		Assert.assertEquals(executeSelectQuery(QueryRepository.Consent.getConsentQuery,"COMIC").get("CONSENTVALUE"),"OPTOUT");
		removeRecordFromTable("delete FROM consents WHERE TVID='"+executeSelectQuery(QueryRepository.Consent.query,"TM").get("ACCOUNTNUMBER")+"'","COMIC");
		//removeRecordFromTable(deleteConsentQuery);
	}
	
	@Test(priority=3)
	public void setConsentTVTA_OPTOUT_BEP1423003() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
	{
	//Sending request
		Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForConsent("setConsent", "TVReco", "OPTIN"), headerValues("BTA"), queryParams(QueryRepository.Consent.query,"TM"));
		System.out.println(response.prettyPrint());
		//Validating Response
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.Status"),"Success");
		Assert.assertEquals(executeSelectQuery(QueryRepository.Consent.getConsentQuery,"COMIC").get("CONSENTTYPE"),"TVReco");
		Assert.assertEquals(executeSelectQuery(QueryRepository.Consent.getConsentQuery,"COMIC").get("CONSENTVALUE"),"OPTIN");
		//removeRecordFromTable(deleteConsentQuery);
		}
	@Test(priority=4)
	public void setConsentTVReco_OPTOUT_BEP1423004() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
	{
		Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForConsent("setConsent", "TVReco", "OPTOUT"), headerValues("BTA"), queryParams(QueryRepository.Consent.query,"TM"));
		System.out.println(response.prettyPrint());
		//Validating Response
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.Status"),"Success");
		Assert.assertEquals(executeSelectQuery(QueryRepository.Consent.getConsentQuery,"COMIC").get("CONSENTTYPE"),"TVReco");
		Assert.assertEquals(executeSelectQuery(QueryRepository.Consent.getConsentQuery,"COMIC").get("CONSENTVALUE"),"OPTOUT");
	
	}
	@Test(priority=5)
	public void setConsentwithInvalidConsentType_BEP1423005() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
	{
		Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForConsent("setConsent", "TVReco1", "OPTOUT"), headerValues("BTA"), queryParams(QueryRepository.Consent.query,"TM"));
		System.out.println(response.prettyPrint());
		//Validating Response
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"ConsentType Invalid");
	}
	@Test(priority=6)
	public void setConsentwithInvalidConsentValue_BEP1423006() throws JAXBException, FileNotFoundException,ClassNotFoundException, SQLException 
	{
		Response response =  RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForConsent("setConsent", "TVReco", "OPTOUT1"), headerValues("BTA"), queryParams(QueryRepository.Consent.query,"TM"));
		System.out.println(response.prettyPrint());
		//Validating Response
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ERROR.ErrorDescription"),"Mandatory Parameter ConsentValue should be either OPTIN or OPTOUT");
	}
}
