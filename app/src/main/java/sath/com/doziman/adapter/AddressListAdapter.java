package sath.com.doziman.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import sath.com.doziman.R;
import sath.com.doziman.TransparentProgressDialog;
import sath.com.doziman.activity.AddCustomerAddressActivity;
import sath.com.doziman.asynctask.SendSMSTask;
import sath.com.doziman.dto.AddressViewHolder;
import sath.com.doziman.dto.DoziAddressDTO;
import sath.com.doziman.dto.MapWrapper;
import sath.com.doziman.dto.smsDTO;
import sath.com.doziman.utils.IntentCaller;
import sath.com.doziman.utils.StorageUtil;

/**
 * Created by Krishna on 10/17/2015.
 */
public class AddressListAdapter extends ArrayAdapter<DoziAddressDTO> {
    Context mycontext;
    private TransparentProgressDialog pd;
    Map<String, String> HtKpi;
    String doziPhone;

    public AddressListAdapter(Context context,int textViewResourceId, List<DoziAddressDTO> addresses) {
        super(context,textViewResourceId, addresses);
        mycontext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout addressView;
        AddressViewHolder viewHolder;
        //Get the current alert object
        Log.i("", "Came to get View to populate with position : " + position);
        final DoziAddressDTO doziAddressDTO = getItem(position);

        if (convertView == null) {
            addressView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(R.layout.childlistview, addressView, true);
            viewHolder = new AddressViewHolder();
        } else {
            addressView = (LinearLayout) convertView;
            viewHolder = (AddressViewHolder) convertView.getTag();
        }
        //linlaHeaderProgress = (LinearLayout) addressView.findViewById(R.id.sendSMSProgress);
        pd = new TransparentProgressDialog(mycontext, R.drawable.loadingspinner);

        TextView tv = (TextView) addressView.findViewById(R.id.doziName);
        tv.setText(doziAddressDTO.getDoziName());
        viewHolder.setDoziName(tv);

        tv = (TextView) addressView.findViewById(R.id.doziAddress);
        tv.setText(doziAddressDTO.getDoziAddress());
        viewHolder.setDoziAddress(tv);

        tv = (TextView) addressView.findViewById(R.id.doziDistance);
        tv.setText((double)(Math.round(Double.parseDouble(doziAddressDTO.getDoziDistance())*100))/100 + " Km");
        viewHolder.setDoziDistance(tv);

        //Hidden Fields
        tv = (TextView) addressView.findViewById(R.id.doziPhone);
        tv.setText(doziAddressDTO.getDoziMobile());
        viewHolder.setDoziPhone(tv);

        tv = (TextView) addressView.findViewById(R.id.doziLatitude);
        tv.setText(String.valueOf(doziAddressDTO.getDoziLatitude()));
        viewHolder.setDoziLat(tv);

        tv = (TextView) addressView.findViewById(R.id.doziLongitude);
        tv.setText(String.valueOf(doziAddressDTO.getDoziLongitude()));
        viewHolder.setDoziLong(tv);
        addressView.setTag(viewHolder);

        Button sendSMS = (Button) addressView.findViewById(R.id.sendSMS);

        // Send SMS on click of Button. If the Customer Address is not saved, then redirect to Add Address Activity
        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String wrapperStr = StorageUtil.getCustomerAddress(mycontext);
                if(wrapperStr == null) {
                    MapWrapper wrapper = gson.fromJson(wrapperStr, MapWrapper.class);
                    HtKpi = wrapper.getMyMap();

                    AlertDialog.Builder builder = new AlertDialog.Builder(mycontext);
                    builder.setMessage(HtKpi.get("custaddress1") + "\n" + HtKpi.get("custaddress2")).setTitle(R.string.areyousure);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            smsDTO smsdto = new smsDTO();
                            smsdto.setName(HtKpi.get("custname"));
                            smsdto.setMobileNo(doziPhone);
                            smsdto.setAddress(HtKpi.get("custaddress1") + "\n" + HtKpi.get("custaddress2"));
                            new SendSMSTask(mycontext).execute(smsdto);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //finish();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    IntentCaller.callIntent((Activity)mycontext, AddCustomerAddressActivity.class);
                }

            }
        });

        return addressView;
    }
    /*class SendSMSTask extends AsyncTask<smsDTO, Void, String> {

        private Exception exception;
        protected void onPostExecute(String status) {
            Toast.makeText(mycontext, R.string.dhobi_sms_sent, Toast.LENGTH_LONG).show();
            //Hide the Spinner
            //linlaHeaderProgress.setVisibility(View.GONE);
            pd.hide();
        }

        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS
            //linlaHeaderProgress.setVisibility(View.VISIBLE);
            pd.show();
        }

        @Override
        protected String doInBackground(smsDTO... params) {
            HttpDataPostImpl.postSMS(params[0].getMobileNo(), params[0].getName() + "\n" + params[0].getAddress());
            return "";
        }
    }*/
    /*public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        StringBuilder sb = new StringBuilder();

        sb.append(((Address)parent.getItemAtPosition(position)).getAddressLine(position)).append(",");
        sb.append(((Address)parent.getItemAtPosition(position)).getAdminArea()).append(";");
        sb.append(((Address)parent.getItemAtPosition(position)).getPostalCode()).append(";");
        sb.append(((Address)parent.getItemAtPosition(position)).getCountryName());

        address = sb.toString();
    }*/
}
