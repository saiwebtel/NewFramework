package model;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="BTA")
public class BTA 
{
	@XmlAttribute(name = "xmlns:xsi")
	private String xmlnsxsi = "http://www.w3.org/2001/XMLSchema-instance";
	private String xsinoNamespaceSchemaLocation;
	private Reminder addReminder;
	private Reminder deleteReminder;
	private Reminder updateReminder;
	private Consent consent ;
	private Wishlist addWishlist;
	private Wishlist deleteWishlist;
	
	@XmlElement(name="AddUserWishListDoc")
	public Wishlist getAddWishlist() {
		return addWishlist;
	}
	public void setAddWishlist(Wishlist addWishlist) {
		this.addWishlist = addWishlist;
	}
	@XmlElement(name="DeleteFromWishListDoc")
	public Wishlist getDeleteWishlist() {
		return deleteWishlist;
	}
	public void setDeleteWishlist(Wishlist deleteWishlist) {
		this.deleteWishlist = deleteWishlist;
	}	
	@XmlElement(name="UpdateReminderDoc")
	public Reminder getUpdateReminder() {
		return updateReminder;
	}
	public void setUpdateReminder(Reminder updateReminder) {
		this.updateReminder = updateReminder;
	}
	@XmlElement(name="Consent")
	public Consent getConsents() {
		return consent;
	}
	public void setConsents(Consent consent) {
		this.consent = consent;
	}
	
	@XmlElement(name="AddReminderDoc")
	public Reminder getAddReminder() {
		return addReminder;
	}

	public void setAddReminder(Reminder addReminder) {
		this.addReminder = addReminder;
	}
	@XmlElement(name="DeleteReminderDoc")
	public Reminder getDeleteReminder() {
		return deleteReminder;
	}
	public void setDeleteReminder(Reminder deleteReminder) {
		this.deleteReminder = deleteReminder;
	}
	
	@XmlAttribute(name = "xsi:noNamespaceSchemaLocation")
	public String getXsinoNamespaceSchemaLocation() {
		return xsinoNamespaceSchemaLocation;
	}

	public void setXsinoNamespaceSchemaLocation(String xsinoNamespaceSchemaLocation) {
		this.xsinoNamespaceSchemaLocation = xsinoNamespaceSchemaLocation;
	}
}
