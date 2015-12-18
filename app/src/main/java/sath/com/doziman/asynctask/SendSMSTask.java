package sath.com.doziman.asynctask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import sath.com.doziman.R;
import sath.com.doziman.dto.smsDTO;
import sath.com.doziman.integration.HttpDataPostImpl;
import sath.com.doziman.utils.StorageUtil;

/**
 * Created by Krishna on 12/15/2015.
 */
public class SendSMSTask extends AsyncTask<smsDTO, Void, smsDTO> {
    private Context context;
    private Exception exception;
    public SendSMSTask(Context context) {
        this.context = context;
    }
    protected void onPostExecute(smsDTO smsdto) {
        StorageUtil.saveToRecents(context, smsdto.getDoziId());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.customer_address_saved).setTitle(R.string.success);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                //context.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected smsDTO doInBackground(smsDTO... params) {
        HttpDataPostImpl.postSMS(params[0].getMobileNo(), params[0].getName() + "\n" + params[0].getAddress());
        return params[0];
    }
}
