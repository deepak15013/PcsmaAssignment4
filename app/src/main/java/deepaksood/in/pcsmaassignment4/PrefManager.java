package deepaksood.in.pcsmaassignment4;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by deepak on 4/4/16.
 */
public class PrefManager {

    public static final String TAG = PrefManager.class.getSimpleName();

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Muster";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_MOBILE = "mobile";


    public PrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLogin(String mobile) {
        editor.putString(KEY_MOBILE,mobile);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        Log.v(TAG,"changed: true");
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN,false);
    }

}
