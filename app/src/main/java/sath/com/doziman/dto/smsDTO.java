package sath.com.doziman.dto;

/**
 * Created by Krishna on 10/13/2015.
 */
public class smsDTO {

    private String mobileNo;
    private String address;
    private String name;
    private String doziId;

    public String getDoziId() {
        return doziId;
    }

    public void setDoziId(String doziId) {
        this.doziId = doziId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
