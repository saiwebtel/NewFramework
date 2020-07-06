package testData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import model.Reminder;
import model.BTA;
import model.Consent;
import model.Wishlist;
import Exposure.RestAssured.ReminderTests;
import Exposure.RestAssured.WishlistTests;
import XMLUtils.XMLUtil;
import base.Testbase;
import dbConnection.DataBaseConnection;

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
			addreminder.setChannelExtID(getRecordFromTable(ReminderTests.query,"TM").get("EXTERNALID"));
			addreminder.setSchTrailID(getRecordFromTable(ReminderTests.query,"TM").get("SCHEDULETRAILID"));
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
				updatereminder.setID(Testbase.executeSelectQuery(ReminderTests.reminderData,"TM").get("REMINDERID"));
				updatereminder.setChannelExtID(Testbase.executeSelectQuery(ReminderTests.updateQuery,"TM").get("EXTERNALID"));
				updatereminder.setSchTrailID(Testbase.executeSelectQuery(ReminderTests.updateQuery,"TM").get("SCHEDULETRAILID"));
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
				addToWishlist.setVodExternalId(Testbase.executeSelectQuery(WishlistTests.query,"TM").get("EXTERNALID"));
				}
				else if(requestType.equalsIgnoreCase("WISHLIST1"))
				{
					addToWishlist.setVodExternalId(Testbase.executeSelectQuery(WishlistTests.newQuery,"TM").get("EXTERNALID"));
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
	public static void main(String args[]) throws ClassNotFoundException,SQLException {
		//TestDataCreation adt = new TestDataCreation();
		//adt.createPOSTBody("addReminder");
	}

}
