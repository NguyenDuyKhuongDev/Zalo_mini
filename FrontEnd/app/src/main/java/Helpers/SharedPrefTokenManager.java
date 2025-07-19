package Helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefTokenManager {
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefTokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveTokens(String accessToken, String refreshToken) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    public String getAccessToken(){
        return sharedPreferences.getString(KEY_ACCESS_TOKEN,null);
    }
    public String getRefreshToken(){
        return sharedPreferences.getString(KEY_REFRESH_TOKEN,null);
    }

    public void clearTokens(){
        editor.clear();
        editor.apply();
    }
}
