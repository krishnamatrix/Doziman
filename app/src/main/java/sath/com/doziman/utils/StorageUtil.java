package sath.com.doziman.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by Krishna on 12/15/2015.
 */
public class StorageUtil {
    public static void saveToRecents(Context context, String doziId) {
        SharedPreferences settings = context.getSharedPreferences("doziIds",
                Context.MODE_PRIVATE);
        Set<String> set = settings.getStringSet("recentdoziIds", null);
        set.add(doziId);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.putStringSet("doziIds", set);
        editor.commit();
    }
    public static Set<String> getRecents(Context context) {
        SharedPreferences settings = context.getSharedPreferences("doziIds",
                Context.MODE_PRIVATE);
        Set<String> set = settings.getStringSet("recentdoziIds", null);
        return set;
    }
    public static String getCustomerAddress(Context mycontext){
        SharedPreferences settings = mycontext.getSharedPreferences("customer",
                Context.MODE_PRIVATE);
        return settings.getString("customeraddress", null);
    }
}
