package model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


//@XmlRootElement(name = "AddReminderDoc")
public class Reminder {
	
	private String ReminderType;
	private String MinsBeforeStart;
	private String SchTrailID;
	private String ChannelExtID ;
	private String ID;

	@XmlElement(name = "ID")
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	@XmlElement(name = "ReminderType")
	public String getReminderType() {
		return ReminderType;
	}
	public void setReminderType(String reminderType) {
		ReminderType = reminderType;
	}
	@XmlElement(name = "MinsBeforeStart")
	public String getMinsBeforeStart() {
		return MinsBeforeStart;
	}
	public void setMinsBeforeStart(String minsBeforeStart) {
		MinsBeforeStart = minsBeforeStart;
	}
	@XmlElement(name = "SchTrailID")
	public String getSchTrailID() {
		return SchTrailID;
	}
	public void setSchTrailID(String schTrailID) {
		SchTrailID = schTrailID;
	}
	@XmlElement(name = "ChannelExtID")
	public String getChannelExtID() {
		return ChannelExtID;
	}
	public void setChannelExtID(String channelExtID) {
		ChannelExtID = channelExtID;
	}
	
}
