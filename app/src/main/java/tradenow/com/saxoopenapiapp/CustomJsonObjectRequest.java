package tradenow.com.saxoopenapiapp;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
/**
 * Created by Lokesh on 07-05-2017.
 */

public class CustomJsonObjectRequest extends JsonObjectRequest {
    private JSONObject params;
    private Map<String,String> customHeaders;
    private Context mCtx;

    public CustomJsonObjectRequest(int method, String url, JSONObject params, Map<String,String> headersMap, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Context context)
    {
        super(method,url,params,listener,errorListener);
        mCtx = context;
        this.params = params;
        customHeaders = headersMap;
    }

    public JSONObject getParamsObject()
    {
        return params;
    }

    protected Response<JSONObject> parseNetworkResponse(NetworkResponse networkResponse)
    {
        NetworkConnection.getInstance(mCtx.getApplicationContext()).checkSessionCookie(networkResponse.headers);
        return super.parseNetworkResponse(networkResponse);
    }

    public Map<String,String> getHeaders() throws AuthFailureError
    {
        Map<String,String> headers = super.getHeaders();

        if(headers == null || headers.equals(Collections.emptyMap()))
        {
            headers = new HashMap<String, String>();
        }
        //Map<String,String> customHeaders = NetworkConnection.getInstance(mCtx.getApplicationContext()).getCustomHeaders();
        Map<String,String> finalHeaders = new HashMap<>();
        finalHeaders.putAll(headers);
        if(customHeaders != null)
            finalHeaders.putAll(customHeaders);

        return finalHeaders;
    }
}
