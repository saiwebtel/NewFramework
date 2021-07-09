package testData;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import model.BTA;
import model.Consent;
import model.Reminder;
import model.Wishlist;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Exposure.RestAssured.ReminderTests;
import Exposure.RestAssured.WishlistTests;
import XMLUtils.XMLUtil;
import base.Testbase;


public class TestDataCreation extends Testbase {
	public String reminderID;
	BTA btaobj;
	Reminder addreminder,deletereminder,updatereminder;
	Wishlist addToWishlist,deleteFromWishlist;
	XMLUtil xmlUtil;
	String requestBody = null;
	public String createPOSTBodyForReminder(String requestType) {
		if (requestType().get("ADDREMINDER").equalsIgnoreCase(requestType)) {
			try{
			btaobj = new BTA();
			addreminder = new Reminder();
			btaobj.setXsinoNamespaceSchemaLocation("BTADocAddReminder.xsd");
			addreminder.setReminderType("Reminder");
			addreminder.setMinsBeforeStart("8");
			addreminder.setChannelExtID(executeSelectQuery(QueryRepository.Reminder.query,"TM").get("EXTERNALID"));
			addreminder.setChannelExtID(executeSelectQuery(QueryRepository.Reminder.query,"TM").get("EXTERNALID"));
			addreminder.setSchTrailID(executeSelectQuery(QueryRepository.Reminder.query,"TM").get("SCHEDULETRAILID"));
			btaobj.setAddReminder(addreminder);
			xmlUtil = new XMLUtil();
			requestBody = xmlUtil.convertToXml(btaobj, btaobj.getClass());
			}
			catch (Exception e){
			e.printStackTrace();
			}
		}
		if (requestType().get("DELETEREMINDER").equalsIgnoreCase(requestType)) {
			btaobj = new BTA();
			deletereminder = new Reminder();
			btaobj.setXsinoNamespaceSchemaLocation("BTADocDeleteReminder.xsd");
			try {
				reminderID=Testbase.executeSelectQuery(
						"SELECT REMINDERID from SETTOPBOXREMINDERS order by REMINDERID desc","TM").get("REMINDERID");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			deletereminder.setID(reminderID);
			btaobj.setDeleteReminder(deletereminder);
			xmlUtil = new XMLUtil();
			requestBody = xmlUtil.convertToXml(btaobj, btaobj.getClass());
		}
		if (requestType().get("UPDATEREMINDER").equalsIgnoreCase(requestType)) {
			
			btaobj = new BTA();
			updatereminder = new Reminder();
			btaobj.setXsinoNamespaceSchemaLocation("BTADocUpdateReminder.xsd");
			
			try {
				updatereminder.setID(Testbase.executeSelectQuery(QueryRepository.Reminder.reminderData,"TM").get("REMINDERID"));
				updatereminder.setChannelExtID(Testbase.executeSelectQuery(QueryRepository.Reminder.updateQuery,"TM").get("EXTERNALID"));
				updatereminder.setSchTrailID(Testbase.executeSelectQuery(QueryRepository.Reminder.updateQuery,"TM").get("SCHEDULETRAILID"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			updatereminder.setReminderType("Autotune");
			updatereminder.setMinsBeforeStart("10");
			btaobj.setUpdateReminder(updatereminder);
			xmlUtil = new XMLUtil();
			requestBody = xmlUtil.convertToXml(btaobj, btaobj.getClass());
		}
		return requestBody;
	}
	public String createPOSTBodyForConsent(String reqestType,String consentType,String ConsentValue)
		{
			if(requestType().get("SETCONSENT").equalsIgnoreCase(reqestType)) 
			{
		BTA obj = new BTA();
		Consent setConsent = new Consent();
		obj.setXsinoNamespaceSchemaLocation("BTADocSetConsent.xsd");
		setConsent.setConsentType(consentType);
		setConsent.setConsentValue(ConsentValue);
		setConsent.setConsentMessage("Dynamic");
		setConsent.setLastUpdatedBy("Automatic_Updation");
		obj.setConsents(setConsent);
		xmlUtil = new XMLUtil();
		requestBody = xmlUtil.convertToXml(obj, obj.getClass());
			}
		return requestBody;
		}

	public String createPOSTBodyForWishList(String requestType) {
		if (requestType.equalsIgnoreCase("addWishlist")|| requestType.equalsIgnoreCase("WISHLIST1")) 
		{
			try{
				btaobj = new BTA();
				addToWishlist=new Wishlist();
				btaobj.setXsinoNamespaceSchemaLocation("BTADocAddUserWishList.xsd");
				if(requestType.equalsIgnoreCase("addWishlist"))
				{
				addToWishlist.setVodExternalId(Testbase.executeSelectQuery(QueryRepository.Wishlist.query,"TM").get("EXTERNALID"));
				}
				else if(requestType.equalsIgnoreCase("WISHLIST1"))
				{
					addToWishlist.setVodExternalId(Testbase.executeSelectQuery(QueryRepository.Wishlist.query,"TM").get("EXTERNALID"));
				}
				btaobj.setAddWishlist(addToWishlist);
				xmlUtil = new XMLUtil();
				requestBody = xmlUtil.convertToXml(btaobj, btaobj.getClass());
				}
				catch (Exception e){
				e.printStackTrace();
				}
		}
		if (requestType.equalsIgnoreCase("deleteWishlist")) 
		{
			try{
				btaobj = new BTA();
				deleteFromWishlist=new Wishlist();
				btaobj.setXsinoNamespaceSchemaLocation("BTADocDeleteFromWishList.xsd");
				deleteFromWishlist.setID(Testbase.executeSelectQuery("select * from SERVICE_DATA WHERE STATUS !='D' AND DATA_TYPE='WLIST'", "UUSD").get("SERVICE_DATA_ID"));
				btaobj.setDeleteWishlist(deleteFromWishlist);
				xmlUtil = new XMLUtil();
				requestBody = xmlUtil.convertToXml(btaobj, btaobj.getClass());
				}
				catch (Exception e){
				e.printStackTrace();
				}
		}

		return requestBody;
	}
	/*public static String createPostBodyForSwimlanes() throws IOException
	{
		String json=System.getProperty("user.dir")+"/src/resources/swimlane.json";
		return new String(Files.readAllBytes(Paths.get(json)));
	}*/
	public static JSONObject updateJsonParameterValue(String SD,String externalId) throws IOException, ParseException
	{
		FileReader reader=new FileReader(System.getProperty("user.dir")+"/src/resources/swimlane.json");
		JSONParser parser=new JSONParser();
		JSONObject object=(JSONObject)parser.parse(reader);
		for (Object key : object.keySet()) {
	        String keyStr = (String)key;
	        Object keyvalue = object.get(keyStr);
	        //System.out.println("KEY: "+ keyStr + " == VALUE: " + keyvalue);
	        if(((JSONObject) object).containsKey(SD))
            {
	          if(SD.equalsIgnoreCase("clientUiLayouts"))
	 	      {
	        	  ((JSONObject) object).remove("clientUiLayouts");
	        	  ((JSONObject) object).put("externalId", externalId);
	              ((JSONObject) object).put("name", externalId);
	        	   break;
	 	      }
	         ((JSONObject) object).put(SD, externalId);
           	 ((JSONObject) object).put("name", externalId);
           	  System.out.println(((JSONObject) object).get("name"));
           	  break;
            }
	        if(keyStr.equalsIgnoreCase("queries"))
	        {
	        	 //System.out.println("KEY: "+ keyStr + " == VALUE: " + keyvalue);
	        	 JSONArray arr = (JSONArray)keyvalue;
	             for (int i=0; i < arr.size()-1; i++) {
	                 Object arrObj = arr.get(0);
	                 if (arrObj instanceof JSONObject) {
	                     //System.out.println(arrObj);
	                     if(((JSONObject) arrObj).containsKey(SD))
	                     {
	                    	 ((JSONObject) arrObj).put(SD, externalId);
	                    	 System.out.println(((JSONObject) arrObj).get("type"));
	                     }
	                     break;
	                 }
	             }
	    	}      
	     }
		return object;
	}
	public static String generateRandomString(int n)
	{
		// chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789"
                                    + "abcdefghijklmnopqrstuvxyz";
  
        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);  
        for (int i = 0; i < n; i++) {
            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                = (int)(AlphaNumericString.length()
                        * Math.random());
            // add Character one by one in end of sb
            sb.append(AlphaNumericString.charAt(index));
        }
        return ("SWIM_"+sb.toString());
	}
	public static void main(String args[]) throws ClassNotFoundException,SQLException {
		//TestDataCreation adt = new TestDataCreation();
		//adt.createPOSTBody("addReminder");
	}

}
