package tradenow.com.saxoopenapiapp;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lokesh on 03-03-2017.
 */

public class CustomStringRequest extends StringRequest {

    private Map<String,String> params;
    private Map<String,String> customHeaders;
    private Context mCtx;

    public CustomStringRequest(int method, String url, Map<String,String> paramsMap,Map<String,String> headersMap, Response.Listener<String> listener, Response.ErrorListener errorListener, Context context)
    {
        super(method,url,listener,errorListener);
        mCtx = context;
        params = paramsMap;
        customHeaders = headersMap;
    }

    public Map<String,String> getParams()
    {
        return params;
    }

    protected Response<String> parseNetworkResponse(NetworkResponse networkResponse)
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
