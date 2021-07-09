package testData;

import java.util.Map;

import base.Testbase;

public class QueryRepository extends Testbase {

	public static class Reminder {
		public static String query="SELECT STB.MACADDRESS, DTV.EXTERNALID, SFS.SCHEDULETRAILID FROM subscribers s,customer_na@TO_UUSD cna,"
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
		
		public static String updateQuery="SELECT STB.MACADDRESS, DTV.ID,DTV.EXTERNALID,SFS.PROGRAMREFERENCENUMBER,SFS.CHANNELREFERENCENUMBER, SFS.SCHEDULETRAILID "
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
		
		public static String deleteQuery="delete SETTOPBOXREMINDERS where  REMINDERID in ( select REMINDERID from ( select * from SETTOPBOXREMINDERS order by REMINDERCREATIONDATETIME desc ) where rownum<=2)";
		public static String reminderData="SELECT * FROM SETTOPBOXREMINDERS WHERE SETTOPBOXREMINDERS.REMINDERCREATIONDATETIME IS NOT NULL "
				+ "and SETTOPBOXREMINDERS.REMINDERCREATIONDATETIME>sysdate";
	}
    public static class Wishlist {
		public static String query="SELECT STB.MACADDRESS, ci.EXTERNALID,ci.GROUPID FROM subscribers s "
				+ "LEFT JOIN customer_na@TO_UUSD CNA ON S.ID = CNA.SUBSCRIBER_ID "
				+  "LEFT JOIN SETTOPBOXES STB ON S.ID = STB.ASSIGNEDTOSUBSCRIBERID "
				+  "LEFT JOIN subscriberpackages sp ON  STB.ASSIGNEDTOSUBSCRIBERID=SP.SUBSCRIBERID "
				+  "LEFT JOIN packageitems pi ON SP.PACKAGEID = PI.PACKAGEID "
				+  "LEFT JOIN contentitems ci ON PI.ITEMID = ci.ID "
				+  "LEFT JOIN CONTENTITEMDETAILS cid ON CI.ID=CID.CONTENTITEMID "
				+  "LEFT JOIN SERVICE_DATA@TO_UUSD SD "
				+  " ON SP.SUBSCRIBERID = S.ID "
				+  " AND ci.TYPE = 'VOD' "
				+  " AND ci.SUBTYPE = 'VOD' "
				+  " AND ci.RECORDSTATUSCODE='A' "
				+  " AND CI.GROUPID is NOT NULL "
				+  " AND CID.LICENCEPERIODENDDATE > sysdate "
				+  " AND S.STATUS = 'Active' "
				+  " AND S.STATUSCODE = 'A' "
				+  " AND STB.STATUS='Assigned' "
				+  " AND STB.RECORDSTATUSCODE='A' "
				+  " AND SD.HOUSEHOLD_UUID is not null "
				+  " Where ci.EXTERNALID is not null "
				+  " AND CID.LICENCEPERIODENDDATE > sysdate "
				+  " AND CNA.TM_MF_SITE_NAME='TM Management Server' "
				+  " AND rownum < 10 ";
		public static String wishlistdata ="select * from WISHLIST WL ,SERVICE_DATA SD "
				+ "WHERE WL.SERVICE_DATA_ID=SD.SERVICE_DATA_ID order by SD.TIMESTAMP desc";
	}
    public static class Consent {    	
    	//public static String deleteConsentQuery;
    	public static String query="SELECT STB.MACADDRESS,S.ACCOUNTNUMBER,cna.SUBSCRIBER_NA  FROM SUBSCRIBERS S "
              + "LEFT JOIN customer_na@TO_UUSD cna ON S.ACCOUNTNUMBER=CNA.SUBSCRIBER_NA  "
              + "LEFT JOIN  SETTOPBOXES STB  ON STB.ASSIGNEDTOSUBSCRIBERID=CNA.SUBSCRIBER_ID "                
              + "AND S.ID=CNA.SUBSCRIBER_ID "
              + "AND CNA.STATUS_CODE='A' "
              + "AND CNA.TM_MF_SITE_NAME='TM Management Server' "
              + "AND S.STATUSCODE = 'A' "
              + "AND STB.STATUS='Assigned'  "
              + "AND STB.RECORDSTATUSCODE='A' "
              + "Where STB.MACADDRESS is not null " 
              + "AND rownum < 10";
    	public static String deleteConsentQuery="delete FROM consents WHERE id > ( ( SELECT MAX( id ) FROM consents ) - 1 )";
    	public static String getConsentQuery="select TVID,CONSENTTYPE,CONSENTVALUE,LASTUPDATEBY FROM consents WHERE id > ( ( SELECT MAX( id ) FROM consents ) - 1)";
    	public static String CLIENTSETINTEGRATIONVALUE="Select CONSENTTYPE,CLIENTSETINTEGRATION from CONSENTTYPES where CONSENTTYPE='TVTA'";
    	

    }
}
