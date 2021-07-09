package Exposure.RestAssured;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.io.IOException;
import java.sql.SQLException;

import javax.xml.bind.JAXBException;

import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import testData.TestDataCreation;
import base.Testbase;
import RestAPIHelper.RestUtil;

public class Swimlanes extends Testbase {
	public static String externalId;
	@BeforeMethod
	public void beforeTest() throws SQLException
	{
		externalId=TestDataCreation.generateRandomString(10);
		RestAssured.basePath = "/bep-mf-api/swimlanes/"+externalId;
	}
	@Test
	public void createSwimlaneWithClientUILayouts_BEP2036301()throws JAXBException, ClassNotFoundException, SQLException, IOException, ParseException {
		
		//Object s=TestDataCreation.updateJsonParameterValue("clientUiLayouts",externalId).remove("clientUiLayouts");
		Response response = RestUtil.sendPutAPI(TestDataCreation.updateJsonParameterValue("clientUiLayouts",externalId), headerValues("REST"));
		System.out.println(response.prettyPrint());
		//Validating Response
		Assert.assertEquals(response.getStatusCode(), 201);
		Assert.assertEquals(externalId,executeSelectQuery("select * from ( select * from  CLIENTUIITEMS order by id desc) where rownum <= 2","TM").get("EXTERNALID"));
	}
	@Test
	public void createSwimlaneWithClientUILayouts_BEP2036302()throws JAXBException, ClassNotFoundException, SQLException, IOException, ParseException {
		
		//System.out.println(TestDataCreation.updateJsonParameterValue("externalId",externalId));
		Response response = RestUtil.sendPutAPI(TestDataCreation.updateJsonParameterValue("externalId", externalId), headerValues("REST"));
		System.out.println(response.prettyPrint());
		//Validating Response
		Assert.assertEquals(response.getStatusCode(), 201);
	}
	
}
