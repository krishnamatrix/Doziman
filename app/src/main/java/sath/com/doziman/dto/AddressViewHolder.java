package sath.com.doziman.dto;

import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Krishna on 10/18/2015.
 */
public class AddressViewHolder {
    TextView doziName;
    TextView doziAddress;
    TextView doziPhone;
    TextView doziDistance;
    TextView doziLat;
    TextView doziLong;
    Button sendSMS;

    public Button getSendSMS() {
        return sendSMS;
    }

    public void setSendSMS(Button sendSMS) {
        this.sendSMS = sendSMS;
    }

    public TextView getDoziPhone() {
        return doziPhone;
    }

    public void setDoziPhone(TextView doziPhone) {
        this.doziPhone = doziPhone;
    }

    public TextView getDoziName() {
        return doziName;
    }

    public void setDoziName(TextView doziName) {
        this.doziName = doziName;
    }

    public TextView getDoziAddress() {
        return doziAddress;
    }

    public void setDoziAddress(TextView doziAddress) {
        this.doziAddress = doziAddress;
    }

    public TextView getDoziDistance() {
        return doziDistance;
    }

    public void setDoziDistance(TextView doziDistance) {
        this.doziDistance = doziDistance;
    }

    public TextView getDoziLat() {
        return doziLat;
    }

    public void setDoziLat(TextView doziLat) {
        this.doziLat = doziLat;
    }

    public TextView getDoziLong() {
        return doziLong;
    }

    public void setDoziLong(TextView doziLong) {
        this.doziLong = doziLong;
    }

}
