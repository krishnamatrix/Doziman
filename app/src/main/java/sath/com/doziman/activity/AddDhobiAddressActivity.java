package sath.com.doziman.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import sath.com.doziman.integration.HttpDataPostImpl;
import sath.com.doziman.R;
import sath.com.doziman.TransparentProgressDialog;
import sath.com.doziman.utils.HelperUtil;

/**
 * Created by Krishna on 10/15/2015.
 */
public class AddDhobiAddressActivity extends Activity {
    EditText custname, custphone, custemail,custaddress1,custaddress2;
    private TransparentProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_doziman);
        pd = new TransparentProgressDialog(this, R.drawable.loadingspinner);

        HelperUtil.setupUI(findViewById(R.id.scrollView1));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        this.getWindow().setAttributes(lp);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ScrollView scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //scrollView1.setBackground(new ColorDrawable(0xffC1EE75));
        }

        TextView skipUser = (TextView) findViewById(R.id.skipAddress);
        skipUser.setMovementMethod(LinkMovementMethod.getInstance());
        skipUser.setVisibility(View.GONE);

        custname = (EditText) findViewById(R.id.custname);
        custname.setOnFocusChangeListener(focusListener);

        custphone = (EditText) findViewById(R.id.custphone);
        custname.setOnFocusChangeListener(focusListener);

        /*custemail = (EditText) findViewById(R.id.custemail);
        cust_email = custemail.getText().toString();*/
        custaddress1 = (EditText) findViewById(R.id.custaddress1);
        custname.setOnFocusChangeListener(focusListener);

        custaddress2 = (EditText) findViewById(R.id.custaddress2);
        custname.setOnFocusChangeListener(focusListener);

        Button submitUser = (Button) findViewById(R.id.submitUserAddress);
        //new RetrieveFeedTask().execute();
        submitUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cust_name = custname.getText().toString();
                String cust_phone = custphone.getText().toString();
                String cust_address1 = custaddress1.getText().toString();
                String cust_address2 = custaddress2.getText().toString();
                if("".equals(cust_name)){
                    custname.setBackgroundResource(R.drawable.roundedcornersred);
                } /*else if("".equals(cust_email)){
                    custemail.setBackgroundResource(R.drawable.roundedcornersred);
                } */else if("".equals(cust_phone)){
                    custphone.setBackgroundResource(R.drawable.roundedcornersred);
                } else if("".equals(cust_address1)){
                    custaddress1.setBackgroundResource(R.drawable.roundedcornersred);
                }else if("".equals(cust_address2)){
                    custaddress2.setBackgroundResource(R.drawable.roundedcornersred);
                } else {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    LatLng latlng = getLocationFromAddress(cust_address1 + " " + cust_address2);
                    if(latlng != null) {
                        nameValuePairs.add(new BasicNameValuePair("doziName", cust_name));
                        nameValuePairs.add(new BasicNameValuePair("doziAddress1", cust_address1));
                        nameValuePairs.add(new BasicNameValuePair("doziAddress2", cust_address2));
                        nameValuePairs.add(new BasicNameValuePair("doziCity", "Chennai"));
                        nameValuePairs.add(new BasicNameValuePair("doziState", "TN"));
                        nameValuePairs.add(new BasicNameValuePair("doziPhone", cust_phone));
                        nameValuePairs.add(new BasicNameValuePair("dozilat", String.valueOf(latlng.latitude)));
                        nameValuePairs.add(new BasicNameValuePair("dozilong", String.valueOf(latlng.longitude)));
                        new SaveDhobiAddressTask().execute(nameValuePairs);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddDhobiAddressActivity.this);
                        builder.setMessage(R.string.address_notrecognised).setTitle(R.string.wrong_address);
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //finish();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }
        });
        TextView closeActivity = (TextView) findViewById(R.id.closeActivity);
        closeActivity.setMovementMethod(LinkMovementMethod.getInstance());
        closeActivity.setVisibility(View.VISIBLE);
        closeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
        //if (hasFocus){
            v.setBackgroundResource(R.drawable.roundedcorners);
        //}
        }
    };
    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng((int) (location.getLatitude() * 1E6),
                    (int) (location.getLongitude() * 1E6));

        } catch(Exception e){}
        return p1;
    }
    class SaveDhobiAddressTask extends AsyncTask<List<NameValuePair>, Void, String> {
        private Exception exception;
        @Override
        protected void onPreExecute() {
            pd.show();
        }

        protected void onPostExecute(String output) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddDhobiAddressActivity.this);
            builder.setMessage(R.string.customer_address_saved).setTitle(R.string.success);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

            //Hide the Spinner
            pd.hide();
        }

        @Override
        protected String doInBackground(List<NameValuePair>... params) {
            //locationTv.setText("Before getting markers");
            return HttpDataPostImpl.insertDhobiData(params[0]);
        }
    }
}
