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

import testData.QueryRepository;
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
	public String postBody;

	@BeforeMethod
	public void beforeTest() throws SQLException {
		RestAssured.basePath = "/broker/bta/addToWishList";
	}
	
	@Test(priority = 1)
	public void addToWishlistWithGroupId_CR1006001()throws JAXBException, ClassNotFoundException, SQLException,IOException {
		// Removing already existing record
		//removeRecordFromTable(deleteQuery, "TM");
		// Sending Request
		Response response = RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForWishList("addWishlist"), headerValues("BTA"),queryParams(QueryRepository.Wishlist.query, "TM"));
		System.out.println(response.prettyPrint());
		// Validating Response
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ID"),executeSelectQuery(QueryRepository.Wishlist.wishlistdata,"UUSD").get("SERVICE_DATA_ID"));	
		//Assert.assertEquals(querydatatm.get("EXTERNALID"),querydatawishlist.get("ITEM_REF"));
		//Assert.assertEquals(querydatatm.get("GROUPID"),querydatawishlist.get("GROUP_ID"));
		//Assert.assertEquals(executeSelectQuery(query, "TM").get("EXTERNALID"),getRecordFromTable(wishlistdata,"UUSD").get("ITEM_REF"));
		//Assert.assertEquals(executeSelectQuery(query, "TM").get("GROUPID"),getRecordFromTable(wishlistdata,"UUSD").get("GROUP_ID"));
	}
	@Test(priority = 2)
	public void addToWishlistWithoutGroupId_CR1006003()throws JAXBException, ClassNotFoundException, SQLException,IOException {
		// Removing already existing record
		//removeRecordFromTable(deleteQuery, "TM");
		// Sending Request
		String newQuery=QueryRepository.Wishlist.query.replaceAll("CI.GROUPID is NOT NULL", "CI.GROUPID is NULL");
		System.out.println(newQuery);
		Response response = RestUtil.sendPostAPI(testdatacreation.createPOSTBodyForWishList("addWishlist"), headerValues("BTA"),queryParams(newQuery, "TM"));
		System.out.println(response.prettyPrint());
		// Validating Response
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.getBody().xmlPath().get("BTAResponse.ID"),executeSelectQuery(QueryRepository.Wishlist.wishlistdata,"UUSD").get("SERVICE_DATA_ID"));	
		//Assert.assertEquals(executeSelectQuery(newQuery, "TM").get("EXTERNALID"),getRecordFromTable(wishlistdata,"UUSD").get("ITEM_REF"));
		//Assert.assertNull(getRecordFromTable(wishlistdata,"UUSD").get("GROUP_ID"));
	}
	@Test(priority = 3)
	 public void getWishList_BEP14477001() throws SQLException, ClassNotFoundException, IOException
	 {
		 	RestAssured.basePath = "/broker/bta/getWishList";		 	
		 	//QueryParams
		 	Map<String, String> queryParams = new HashMap<String, String>();
			queryParams.put("MAC", executeSelectQuery(QueryRepository.Wishlist.query,"TM").get("MACADDRESS"));
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
			Assert.assertEquals(response.body().xmlPath().get("BTAResponse.WishList.WishItem[0].ID.text()"),executeSelectQuery(QueryRepository.Wishlist.wishlistdata,"UUSD").get("SERVICE_DATA_ID"));
			//Assert.assertEquals(response.body().xmlPath().get("BTAResponse.WishList.WishItem[0].MovieRef.@ref.text()"),executeSelectQuery(QueryRepository.Wishlist.wishlistdata,"UUSD").get("ITEM_REF"));
			//Assert.assertEquals(response.body().xmlPath().getInt("BTAResponse.WishList.@totalWishItems"),2);
	 }
	@Test(priority = 4)
	 public void deleteFromWishList_BEP14477004() throws SQLException, ClassNotFoundException, IOException
	 {
		 	RestAssured.basePath = "/broker/bta/deleteFromWishList";		 	
			//Sending Request
		 	String body=testdatacreation.createPOSTBodyForWishList("deleteWishlist");
			Response response =  RestUtil.sendPostAPI(body, headerValues("BTA"), queryParams(QueryRepository.Wishlist.query,"TM"));	    
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
