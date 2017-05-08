package tradenow.com.saxoopenapiapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lokesh on 03-03-2017.
 */
/*
*   Singleton class: to be shared between all the app activities
* */
public class GlobalContext {

    private static GlobalContext globalContext;
    private SharedPreferences sharedPreferences;
    private Context mCtx;

    private String authorizationCode = "";
    private String accessTokenURL = "";
    private Map<String,String> accessTokenHeaders;
    private Map<String,String> accessTokenParams;

    public String getAccessTokenURL() {
        return accessTokenURL;
    }

    public void setAccessTokenURL(String accessTokenURL) {
        this.accessTokenURL = accessTokenURL;
    }

    public Map<String, String> getAccessTokenHeaders() {
        return accessTokenHeaders;
    }

    public void setAccessTokenHeaders(Map<String, String> accessTokenHeaders) {
        this.accessTokenHeaders = accessTokenHeaders;
    }

    public Map<String, String> getAccessTokenParams() {
        return accessTokenParams;
    }

    public void setAccessTokenParams(Map<String, String> accessTokenParams) {
        this.accessTokenParams = accessTokenParams;
    }

    private GlobalContext(Context context) {
        mCtx = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mCtx.getApplicationContext());
        authorizationCode = "";
    }

    public static synchronized GlobalContext getInstance(Context context) {
        if (globalContext == null)
            globalContext = new GlobalContext(context);

        return globalContext;
    }

    public SharedPreferences getPreference()
    {
        return sharedPreferences;
    }

    public void setStringInPreferences(String key,String value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public String setStringFromPreferences(String key,String nullableValue)
    {
        return getPreference().getString(key,nullableValue);
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getAccessToken() {

        accessTokenURL = mCtx.getResources().getString(R.string.AuthenticationUrl) + "/token";
        accessTokenHeaders = new HashMap<>();
        try {
            accessTokenHeaders.put("Authorization", "Basic " + Base64.encodeToString(String.format("%1$s:%2$s", mCtx.getResources().getString(R.string.AppKey), mCtx.getResources().getString(R.string.AppSecret)).getBytes("UTF-8"), Base64.NO_WRAP));
        } catch (Exception e) {
            Log.e("GET TOKEN",e.getStackTrace().toString());
        }

        String accessToken = setStringFromPreferences("accessToken","FALSE");
        String refreshToken = setStringFromPreferences("refreshToken","FALSE");
        String tokenExpiresIn = setStringFromPreferences("token_expires_in","FALSE");
        String refreshTokenExpiresIn = setStringFromPreferences("refresh_token_expires_in","FALSE");
        String tokenSetTime = setStringFromPreferences("token_set_time","FALSE");

        // if any of the required value is not present
        if(accessToken.equals("FALSE") ||
                refreshToken.equals("FALSE") ||
                tokenExpiresIn.equals("FALSE") ||
                refreshTokenExpiresIn.equals("FALSE") ||
                tokenSetTime.equals("FALSE")) {
            // if authorization code is available, means user just logged in
            if(!authorizationCode.equals("")) {
                accessTokenParams = new HashMap<>();
                accessTokenParams.put("grant_type","authorization_code");
                accessTokenParams.put("code",authorizationCode);
                return "newToken";
            }
            else
                return "login";
        }

        Date d = new Date();
        // If token has not expired
        if( d.getTime() < (Long.parseLong(tokenExpiresIn)*1000 + Long.parseLong(tokenSetTime)) ){
            return accessToken;
        }
        else {
            // if refresh token has expired
            if( d.getTime() > (Long.parseLong(refreshTokenExpiresIn)*1000 + Long.parseLong(tokenSetTime)) ){
                this.setStringInPreferences("accessToken","FALSE");
                this.setStringInPreferences("refreshToken","FALSE");
                this.setStringInPreferences("token_expires_in","FALSE");
                this.setStringInPreferences("refresh_token_expires_in","FALSE");
                this.setStringInPreferences("token_set_time","FALSE");
                return "login";
            }
            else {
                // retrieving new token using previous refresh token
                accessTokenParams = new HashMap<>();
                accessTokenParams.put("grant_type","refresh_token");
                accessTokenParams.put("refresh_token",refreshToken);
                return "newToken";
            }
        }
    }

    // Saving token values into sharedPreferences
    public void saveTokenResponse(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            this.setStringInPreferences("accessToken",obj.getString("access_token"));
            this.setStringInPreferences("refreshToken",obj.getString("refresh_token"));
            this.setStringInPreferences("token_type",obj.getString("token_type"));
            this.setStringInPreferences("token_expires_in",obj.getString("expires_in"));
            this.setStringInPreferences("refresh_token_expires_in",obj.getString("refresh_token_expires_in"));
            Date date = new Date();
            this.setStringInPreferences("token_set_time",Long.toString(date.getTime()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
