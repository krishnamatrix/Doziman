package sath.com.doziman;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import sath.com.doziman.dto.MapWrapper;

/**
 * Created by Krishna on 10/15/2015.
 */
public class AddCustomerAddressActivity extends AppCompatActivity {
    EditText custname, custphone, custemail,custaddress1,custaddress2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_doziman);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        HelperUtil.setupUI(findViewById(R.id.scrollView1));

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        this.getWindow().setAttributes(lp);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        TextView skipUser = (TextView) findViewById(R.id.skipAddress);
        skipUser.setMovementMethod(LinkMovementMethod.getInstance());
        skipUser.setVisibility(View.GONE);

        custname = (EditText) findViewById(R.id.custname);
        custname.setOnFocusChangeListener(focusListener);

        custphone = (EditText) findViewById(R.id.custphone);
        custphone.setOnFocusChangeListener(focusListener);

        /*custemail = (EditText) findViewById(R.id.custemail);
        custemail.setOnFocusChangeListener(focusListener);*/

        custaddress1 = (EditText) findViewById(R.id.custaddress1);
        custaddress1.setOnFocusChangeListener(focusListener);

        custaddress2 = (EditText) findViewById(R.id.custaddress2);
        custaddress2.setOnFocusChangeListener(focusListener);

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
                    Map<String,String> custAddr = new HashMap<String,String>();
                    custAddr.put("custname", cust_name);
                    custAddr.put("custphone", cust_phone);
                    //custAddr.put("custemail", cust_email);
                    custAddr.put("custaddress1", cust_address1);
                    custAddr.put("custaddress2", cust_address2);

                    Gson gson = new Gson();
                    MapWrapper wrapper = new MapWrapper();
                    wrapper.setMyMap(custAddr);
                    String serializedMap = gson.toJson(wrapper);

                    SharedPreferences settings = getSharedPreferences("customer",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.clear();
                    editor.putString("customeraddress", serializedMap);
                    editor.commit();

                    AlertDialog.Builder builder = new AlertDialog.Builder(AddCustomerAddressActivity.this);
                    builder.setMessage(R.string.customer_address_saved).setTitle(R.string.success);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        LinearLayout closeActivityParent = (LinearLayout) findViewById(R.id.closeActivityParent);
        closeActivityParent.setVisibility(View.VISIBLE);

        TextView closeActivity = (TextView) findViewById(R.id.closeActivity);
        closeActivity.setMovementMethod(LinkMovementMethod.getInstance());

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
