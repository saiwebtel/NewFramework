package Exposure.RestAssured;
import io.restassured.RestAssured;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import model.Wishlist;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import testData.TestDataCreation;
import RestAPIHelper.RestUtil;
import XMLUtils.XMLUtil;
import base.Testbase;

/**
 * @author Vinay.Singh
 *
 */
public class WishlistTests extends Testbase {
	TestDataCreation testdatacreation=new TestDataCreation();
	
	public static String newQuery;
	public static String query = "SELECT STB.MACADDRESS, ci.EXTERNALID,ci.GROUPID FROM subscribers s,customer_na@TO_UUSD cna,"
			+ "SETTOPBOXES STB,subscriberpackages sp, packageitems pi,contentitems ci,CONTENTITEMDETAILS cid,SERVICE_DATA@TO_UUSD SD"
			+ " WHERE S.ID = CNA.SUBSCRIBER_ID"
			+ " AND S.ID = STB.ASSIGNEDTOSUBSCRIBERID"
			+ " AND STB.ASSIGNEDTOSUBSCRIBERID=SP.SUBSCRIBERID"
			+ " AND SP.SUBSCRIBERID = S.ID"
			+ " AND SP.PACKAGEID = PI.PACKAGEID"
			+ " AND PI.ITEMID = ci.ID"
			+ " AND ci.TYPE = 'VOD'"
			+ " AND ci.SUBTYPE = 'VOD'"
			+ " AND ci.RECORDSTATUSCODE='A'"
			+ " AND CI.GROUPID is NOT NULL"
			+ " AND CI.ID=CID.CONTENTITEMID"
			+ " AND CID.LICENCEPERIODENDDATE > sysdate"
			+ " AND S.STATUS = 'Active'"
			+ " AND S.STATUSCODE = 'A'"
			+ " AND STB.STATUS='Assigned'"
			+ " AND STB.RECORDSTATUSCODE='A'"
			+ " AND SD.HOUSEHOLD_UUID is not null"
			+ " AND rownum < 10 ";
	public static String wishlistdata ="select * from WISHLIST WL ,SERVICE_DATA SD "
			+ "WHERE WL.SERVICE_DATA_ID=SD.SERVICE_DATA_ID order by SD.SERVICE_DATA_ID desc";
	public String postBody;
	public Map<String, String> querydatatm;
	public Map<String, String> querydatawishlist;
	WishlistTests() throws SQLException
	{
		postBody=testdatacreation.createPOSTBodyForWishList("addWishlist");
		querydatatm=executeSelectQuery(query, "TM");
		querydatawishlist=executeSelectQuery(wishlistdata, "UUSD");
	}
	@BeforeMethod
	public void beforeTest() throws SQLException {
		RestAssured.basePath = "/broker/bta/addToWishList";
	}
	
	@Test(priority = 1)
	public void addToWishlistWithGroupId_CR1006001()throws JAXBException, ClassNotFoundException, SQLException,IOException {
		// Removing already existing record
		//removeRecordFromTable(deleteQuery, "TM");
		// Sending Request
		Response response = RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForWishList("addWishlist"), headerValues(),queryParams(query, "TM"));
		System.out.println(response.prettyPrint());
		// Validating Response
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ID"),getRecordFromTable(wishlistdata,"UUSD").get("SERVICE_DATA_ID"));	
		Assert.assertEquals(querydatatm.get("EXTERNALID"),querydatawishlist.get("ITEM_REF"));
		Assert.assertEquals(querydatatm.get("GROUPID"),querydatawishlist.get("GROUP_ID"));
		//Assert.assertEquals(executeSelectQuery(query, "TM").get("EXTERNALID"),getRecordFromTable(wishlistdata,"UUSD").get("ITEM_REF"));
		//Assert.assertEquals(executeSelectQuery(query, "TM").get("GROUPID"),getRecordFromTable(wishlistdata,"UUSD").get("GROUP_ID"));

	}
	@Test(priority = 2)
	public void addToWishlistWithoutGroupId_CR1006003()throws JAXBException, ClassNotFoundException, SQLException,IOException {
		// Removing already existing record
		//removeRecordFromTable(deleteQuery, "TM");
		// Sending Request
		newQuery=query.replaceAll("CI.GROUPID is NOT NULL", "CI.GROUPID is NULL");
		System.out.println(newQuery);
		Response response = RestUtil.sendPostAPI(postBody, headerValues(),queryParams(newQuery, "TM"));
		System.out.println(response.prettyPrint());
		// Validating Response
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ID"),getRecordFromTable(wishlistdata,"UUSD").get("SERVICE_DATA_ID"));	
		Assert.assertEquals(executeSelectQuery(newQuery, "TM").get("EXTERNALID"),getRecordFromTable(wishlistdata,"UUSD").get("ITEM_REF"));
		Assert.assertNull(getRecordFromTable(wishlistdata,"UUSD").get("GROUP_ID"));
	}
	@Test(priority = 3)
	 public void getWishList_BEP14477001() throws SQLException, ClassNotFoundException, IOException
	 {
		 	RestAssured.basePath = "/broker/bta/getWishList";		 	
		 	//QueryParams
		 	Map<String, String> queryParams = new HashMap<String, String>();
			queryParams.put("MAC", getRecordFromTable(query,"TM").get("MACADDRESS"));
			queryParams.put("InterfaceVersion", "5.2.0");
			queryParams.put("fc_HDCapable", "TRUE");		
			//Sending Request
			Response response =  RestUtil.sendGetAPI("application/xml", queryParams);
		    String xmlResponse =response.getBody().asString();
		    System.out.println(xmlResponse);
		    FileWriter fw=new FileWriter("TEST.txt");
		    fw.write(xmlResponse);
		    fw.close();		    
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			System.out.println(response.asString());			
			Assert.assertEquals(response.body().xmlPath().get("BTAResponse.WishList.WishItem[0].ID.text()"),getRecordFromTable(wishlistdata,"UUSD").get("SERVICE_DATA_ID"));
			Assert.assertEquals(response.body().xmlPath().get("BTAResponse.WishList.WishItem[0].MovieRef.@ref.text()"),getRecordFromTable(wishlistdata,"UUSD").get("ITEM_REF"));
			Assert.assertEquals(response.body().xmlPath().getInt("BTAResponse.WishList.@totalWishItems"),2);
	 }
	@Test(priority = 4)
	 public void deleteFromWishList_BEP14477004() throws SQLException, ClassNotFoundException, IOException
	 {
		 	RestAssured.basePath = "/broker/bta/deleteFromWishList";		 	
			//Sending Request
		 	String body=testdatacreation.createPOSTBodyForWishList("deleteWishlist");
			Response response =  RestUtil.sendPostAPI(body, headerValues(), queryParams(query,"TM"));	    
			//Validating Response
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.Status"), "Success");
			//System.out.println(body);
		 	Object number=XMLUtil.getXMLTag(body, "BTA.DeleteFromWishListDoc.ID");
			Assert.assertEquals(executeSelectQuery("select STATUS from SERVICE_DATA where SERVICE_DATA_ID="+"'"+number+"'","UUSD").get("STATUS"),"D");				
	 }
	 @AfterMethod
	 public void afterTest() throws SQLException
	 {
		 //addReminderData.deleteTestData(Queries.deleteReminderQuery,Testbase.tmdbUserName,Testbase.tmdbPassword);

	 }
}
