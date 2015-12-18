package sath.com.doziman.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import sath.com.doziman.integration.HttpDataPostImpl;
import sath.com.doziman.R;
import sath.com.doziman.dto.MapWrapper;
import sath.com.doziman.dto.smsDTO;
import sath.com.doziman.utils.HelperUtil;


public class DozimanActivity extends Activity {

    EditText custname, custphone, custemail,custaddress1,custaddress2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_doziman);
        HelperUtil.setupUI(findViewById(R.id.scrollView1));

        SharedPreferences settings = getApplicationContext().getSharedPreferences("customer",
                Context.MODE_PRIVATE);
        String name = settings.getString("customeraddress", null);
        if(name!= null && !"".equals(name)){
            goToMap();
            //finish();
        }
        /*Set<String> set = settings.getStringSet("customeraddress", null);
        if(set!= null && set.size() > 0){
            Log.i("dozi", "Set data is there" + set);
            goToMap();
            //finish();
        }*/
        TextView skipUser = (TextView) findViewById(R.id.skipAddress);
        skipUser.setMovementMethod(LinkMovementMethod.getInstance());

        Button submitUser = (Button) findViewById(R.id.submitUserAddress);

        custname = (EditText) findViewById(R.id.custname);
        custname.setOnFocusChangeListener(focusListener);

        custphone = (EditText) findViewById(R.id.custphone);
        custphone.setOnFocusChangeListener(focusListener);

        /*custemail = (EditText) findViewById(R.id.custemail);
        cust_email = custemail.getText().toString();*/

        custaddress1 = (EditText) findViewById(R.id.custaddress1);
        custaddress1.setOnFocusChangeListener(focusListener);

        custaddress2 = (EditText) findViewById(R.id.custaddress2);
        custaddress2.setOnFocusChangeListener(focusListener);

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
                } */else if("".equals(cust_address1)){
                    custaddress1.setBackgroundResource(R.drawable.roundedcornersred);
                } else if("".equals(cust_address2)){
                    custaddress2.setBackgroundResource(R.drawable.roundedcornersred);
                } else if("".equals(cust_phone)){
                    custphone.setBackgroundResource(R.drawable.roundedcornersred);
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
                    goToMap();
                }

            }
        });
        skipUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMap();
            }
        });

    }

    private void goToMap(){
        if(HelperUtil.isOnline(DozimanActivity.this)) {
            Intent intent = new Intent();
            intent.setClass(DozimanActivity.this, ShowMapActivity.class);

            DozimanActivity.this.startActivity(intent);
            DozimanActivity.this.finish();

            // transition from splash to main menu
            /*overridePendingTransition(R.anim.slideright,
                    R.anim.slideright);*/
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.no_internet_body).setTitle(R.string.no_internet_header);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_doziman, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    class SendSMSTask extends AsyncTask<smsDTO, Void, String> {
        private Exception exception;
        protected void onPostExecute(String status) {
            Toast.makeText(DozimanActivity.this, "Your Dobhi Request has been placed. Dobhi will arrive shortly!!", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(smsDTO... params) {
            HttpDataPostImpl.postSMS(params[0].getMobileNo(), params[0].getAddress());
            return "";
        }
    }

    private View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            //if (hasFocus){
                v.setBackgroundResource(R.drawable.roundedcorners);
            //}
        }
    };
}
