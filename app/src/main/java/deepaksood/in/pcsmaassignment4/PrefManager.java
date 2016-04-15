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
    private static final String KEY_DISPLAY_NAME = "displayName";
    private static final String KEY_DISPLAY_EMAIL_ID = "displayEmailId";
    private static final String KEY_PHOTO_URL = "photoUrl";
    private static final String KEY_COVER_URL = "coverUrl";


    public PrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveUserData(String displayName, String displayEmailId, String photoUrl, String coverUrl) {
        editor.putString(KEY_DISPLAY_NAME, displayName);
        editor.putString(KEY_DISPLAY_EMAIL_ID, displayEmailId);
        editor.putString(KEY_PHOTO_URL, photoUrl);
        editor.putString(KEY_COVER_URL, coverUrl);
        editor.commit();
    }

    public void createLogin(String mobile) {
        editor.putString(KEY_MOBILE,mobile);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        Log.v(TAG,"changed: true");
        editor.commit();
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN,false);
    }

    public String getMobileNumber() {
        return pref.getString(KEY_MOBILE,"0000000000");
    }

    public String getDisplayName() {
        return pref.getString(KEY_DISPLAY_NAME, "Deepak Sood Default");
    }

    public String getDisplayEmailId() {
        return pref.getString(KEY_DISPLAY_EMAIL_ID, "deepaksood619@gmail.com default");
    }

    public String getPhotoUrl() {
        return pref.getString(KEY_PHOTO_URL, "https://drive.google.com/uc?id=0B1jHFoEHN0zfek43ajZrMDZSSms");
    }

    public String getCoverUrl() {
        return pref.getString(KEY_COVER_URL, "https://drive.google.com/uc?id=0B1jHFoEHN0zfek43ajZrMDZSSms");
    }

}
