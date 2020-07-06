package model;

import javax.xml.bind.annotation.XmlElement;

//@XmlRootElement(name="AddUserWishListDoc")
public class Wishlist {
	private String VoDExtID;
	private String ID;

	@XmlElement(name = "ID")
	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	@XmlElement(name = "VoDExtID")
	public String getVodExternalId() {
		return VoDExtID;
	}

	public void setVodExternalId(String VoDExtID) {
		this.VoDExtID = VoDExtID;
	}
	

}
