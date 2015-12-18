package sath.com.doziman.utils;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by Krishna on 10/18/2015.
 */
public class IntentCaller {
    public static void callIntent(Activity callingAct, Class destClass) {
        Intent intent = new Intent();
        intent.setClass(callingAct, destClass);
        callingAct.startActivity(intent);
    }
}
