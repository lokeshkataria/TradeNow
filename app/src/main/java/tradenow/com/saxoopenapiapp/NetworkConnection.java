package tradenow.com.saxoopenapiapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Lokesh on 03-03-2017.
 */

public class NetworkConnection {

    private static NetworkConnection networkConnection;
    private Context mCtx;
    private RequestQueue requestQueue;

    private NetworkConnection(Context context)
    {
        mCtx = context;
        requestQueue = Volley.newRequestQueue(mCtx);
    }

    public static synchronized NetworkConnection getInstance(Context context)
    {
        if(networkConnection == null)
            networkConnection = new NetworkConnection(context);
        return networkConnection;
    }

    public void sendStringRequest(int method,String url,Map<String,String> params,Map<String,String> headers,final NetworkResponseHandler handler)
    {
        //RequestFuture<String> future = RequestFuture.newFuture();

        CustomStringRequest customStringRequest = new CustomStringRequest(method,url,params,headers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handler.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
                ,mCtx);

        requestQueue.add(customStringRequest);

    }

    public void sendJSONRequest(int method, String url, JSONObject params, Map<String,String> headers, final NetworkResponseHandler handler)
    {
        CustomJsonObjectRequest customJsonObjectRequest = new CustomJsonObjectRequest(method,url,params,headers,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        handler.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
                ,mCtx);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url,params , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //RequestFuture<String> future = RequestFuture.newFuture();

        requestQueue.add(jsonObjectRequest);

    }

    public void checkSessionCookie(Map<String,String> headers)
    {
        String SET_COOKIE_KEY = mCtx.getResources().getString(R.string.SET_COOKIE_KEY);
        String COOKIE_INITIALS = mCtx.getResources().getString(R.string.COOKIE_INITIALS);
        String STICKINESS_COOKIE = mCtx.getResources().getString(R.string.STICKINESS_COOKIE);

        if(headers.containsKey(SET_COOKIE_KEY) && headers.get(SET_COOKIE_KEY).startsWith(COOKIE_INITIALS))
        {
            String cookie = headers.get(SET_COOKIE_KEY);
            String[] splitCookie = cookie.split(";");

            SharedPreferences sharedPreference = GlobalContext.getInstance(mCtx).getPreference();
            SharedPreferences.Editor editor = sharedPreference.edit();
            editor.putString(STICKINESS_COOKIE,splitCookie[0]);
            editor.commit();
        }
    }

    public void addSessionCookie(Map<String,String> headers)
    {
        String SET_COOKIE_KEY = mCtx.getResources().getString(R.string.SET_COOKIE_KEY);
        String COOKIE_INITIALS = mCtx.getResources().getString(R.string.COOKIE_INITIALS);
        String STICKINESS_COOKIE = mCtx.getResources().getString(R.string.STICKINESS_COOKIE);
        String COOKIE = mCtx.getResources().getString(R.string.COOKIE);

        SharedPreferences sharedPreference = GlobalContext.getInstance(mCtx).getPreference();
        String stickiness = sharedPreference.getString(STICKINESS_COOKIE,"");

        if(stickiness.length() > 0){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(stickiness);
            if(headers.containsKey(COOKIE))
            {
                stringBuilder.append("; ");
                stringBuilder.append(headers.get(COOKIE));
            }
            headers.put(COOKIE,stringBuilder.toString());
        }
    }

}
