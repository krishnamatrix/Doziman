package sath.com.doziman.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import sath.com.doziman.DelayAutoCompleteTextView;

/**
 * Created by Krishna on 10/16/2015.
 */
public class HelperUtil {
    public static void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        Log.i("","instance of"+(view instanceof EditText));
        Log.i("","instance of Autocomplete"+(view instanceof AutoCompleteTextView));
        if(!(view instanceof EditText) && !(view instanceof AutoCompleteTextView)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    Log.i("","instance of"+(v instanceof EditText));
                    Log.i("","instance of Autocomplete"+(v instanceof AutoCompleteTextView));
                    Log.i("","instance of Autocomplete"+(v instanceof DelayAutoCompleteTextView));
                    Log.i("","instance of Autocomplete"+(v.getClass().getName()));
                    Activity act = (Activity) v.getContext();
                    Log.i("","Came to UI");
                    hideSoftKeyboard(act);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static boolean isOnline(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }
}
