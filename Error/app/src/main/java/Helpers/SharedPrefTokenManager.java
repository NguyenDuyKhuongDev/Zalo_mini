package Helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefTokenManager {
    private static final String PREF_NAME = "auth_prefs";
    private final String KEY_ACCESS_TOKEN;
    private final String KEY_REFRESH_TOKEN;
    private final String TOKEN_EXP_KEY;
    private final String REFRESH_EXP_KEY;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefTokenManager(Context context, String phoneNumber) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        KEY_ACCESS_TOKEN = "access_token_" + phoneNumber;
        KEY_REFRESH_TOKEN = "refresh_token_" + phoneNumber;
        TOKEN_EXP_KEY = "access_Token_Exp_" + phoneNumber;
        REFRESH_EXP_KEY = "refresh_Token_Exp_" + phoneNumber;
    }

    public void saveTokens(String accessToken, String refreshToken, long accessTokenExp, long refreshTokenExp) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putLong(TOKEN_EXP_KEY, accessTokenExp);
        editor.putLong(REFRESH_EXP_KEY, refreshTokenExp);
        editor.apply();
    }

    public Long getTokenExpKey() {
        return sharedPreferences.getLong(TOKEN_EXP_KEY, 0);
    }

    public long getRefreshTokenExpKey() {
        return sharedPreferences.getLong(REFRESH_EXP_KEY, 0);
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public void clearTokens() {
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_REFRESH_TOKEN);
        editor.remove(TOKEN_EXP_KEY);
        editor.remove(REFRESH_EXP_KEY);
        editor.apply();
    }

}
