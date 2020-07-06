package model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Consent")
public class Consent{
	private String ConsentType;
    private String ConsentValue;
    private String LastUpdatedBy;
    private String ConsentMessage;
	private Integer ID;
	private String TVID;
    private String ConsentStatus;
    private Long LastUpdatedOn;   
    private Integer LastDerivedOn;
    private Integer RDSRetryCounter;
    private Integer ConsentFeedback;
	
    @XmlElement(name = "ID" )
    public Integer getID() {
		return ID;
	}

	public void setID(Integer iD) {
		ID = iD;
	}
	@XmlElement(name = "TVID" )
	public String getTVID() {
		return TVID;
	}

	public void setTVID(String tVID) {
		TVID = tVID;
	}
	@XmlElement(name = "ConsentStatus" )
	public String getConsentStatus() {
		return ConsentStatus;
	}

	public void setConsentStatus(String consentStatus) {
		ConsentStatus = consentStatus;
	}
	@XmlElement(name = "LastDerivedOn" )
	public Long getLastUpdatedOn() {
		return LastUpdatedOn;
	}

	public void setLastUpdatedOn(Long lastUpdatedOn) {
		LastUpdatedOn = lastUpdatedOn;
	}
	@XmlElement(name = "LastDerivedOn" )
	public Integer getLastDerivedOn() {
		return LastDerivedOn;
	}

	public void setLastDerivedOn(Integer lastDerivedOn) {
		LastDerivedOn = lastDerivedOn;
	}
	@XmlElement(name = "RDSRetryCounter" )
	public Integer getRDSRetryCounter() {
		return RDSRetryCounter;
	}

	public void setRDSRetryCounter(Integer rDSRetryCounter) {
		RDSRetryCounter = rDSRetryCounter;
	}
	@XmlElement(name = "ConsentFeedback" )
	public Integer getConsentFeedback() {
		return ConsentFeedback;
	}

	public void setConsentFeedback(Integer consentFeedback) {
		ConsentFeedback = consentFeedback;
	}
	
    @XmlElement(name = "ConsentType" )
    public String getConsentType() {
    	return ConsentType;
    }

    public void setConsentType(String ConsentType) {
    	this.ConsentType = ConsentType;
    }

    @XmlElement(name = "ConsentValue" )
    public String getConsentValue() {
    	return ConsentValue;
    }

    public void setConsentValue(String ConsentValue) {
    	this.ConsentValue = ConsentValue;
    }
    @XmlElement(name = "LastUpdatedBy" )
    public String getLastUpdatedBy() {	
    	return LastUpdatedBy;
    }
    public void setLastUpdatedBy(String LastUpdatedBy) {
    	this.LastUpdatedBy = LastUpdatedBy;
    }

    @XmlElement(name = "ConsentMessage" )
    public String getConsentMessage() {
    	return ConsentMessage;
    }
    public void setConsentMessage(String ConsentMessage) {
    	this.ConsentMessage = ConsentMessage;
    }

}