package tradenow.com.saxoopenapiapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {

    private ListView listView;
    private InstrumentAdapter instrumentAdapter;
    private ProgressBar progressBar;
    private GlobalContext globalContext;
    private String accessToken;
    private String tokenType;
    private FxSpotInstrument[] fxSpotInstrument;
    private String[] uics;
    private final int THREE_SECONDS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        listView = (ListView) findViewById(R.id.instrument_list);
        progressBar = (ProgressBar) findViewById(R.id.home_progress);

        /* Setting Toolbar*/
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        globalContext = GlobalContext.getInstance(getApplicationContext());

        this.startOpenApi();
    }

    public void startOpenApi() {
        /* Retrieve access token from shared preferences */
        final GlobalContext gCtx = GlobalContext.getInstance(getApplicationContext());
        String token = gCtx.getAccessToken();

        /* evaluating accessToken */
        switch(token) {
            case "login": // if system is idle from long time
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            case "newToken": // if user has just logged in and needed a fresh token
                NetworkConnection.getInstance(getApplicationContext()).sendStringRequest(Request.Method.POST, gCtx.getAccessTokenURL(), gCtx.getAccessTokenParams(),gCtx.getAccessTokenHeaders(), new NetworkResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        gCtx.saveTokenResponse(response);
                        getInstrumentList();
                    }
                });
                break;
            default: // when accessToken available
                getInstrumentList();
                break;
        }
    }

    public void getInstrumentList() {

        // retrieving stored accessToken
        this.accessToken = GlobalContext.getInstance(getApplicationContext()).setStringFromPreferences("accessToken","False");
        this.tokenType = GlobalContext.getInstance(getApplicationContext()).setStringFromPreferences("token_type","False");

        // Preparing request header
        Map<String,String> accessTokenHeaders = new HashMap<>();
        accessTokenHeaders.put("Authorization",this.tokenType+" "+this.accessToken);

        // Retrieving Top 15 Instrument List
        String instrumentsListURL = getResources().getString(R.string.OpenApiBaseUrl)+getResources().getString(R.string.InstrumentListUrl)+"?AssetTypes=FxSpot&$top=15";
        NetworkConnection.getInstance(getApplicationContext()).sendStringRequest(Request.Method.GET, instrumentsListURL, null,accessTokenHeaders, new NetworkResponseHandler() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray jsonArray = obj.getJSONArray("Data");

                    fxSpotInstrument = new FxSpotInstrument[jsonArray.length()];
                    for(int i = 0;i < jsonArray.length();i++){
                        JSONObject instrument = jsonArray.getJSONObject(i);

                        FxSpotInstrument fx = new FxSpotInstrument();
                        fx.setDescription(instrument.getString("Description"));
                        fx.setIdentifier(instrument.getString("Identifier"));
                        fx.setSymbol(instrument.getString("Symbol"));
                        fxSpotInstrument[i] = fx;
                    }
                    fetchInstruments();
                } catch(Exception e) {
                    Log.e("JSON Parse",e.getMessage());
                }
            }
        });
    }

    public void fetchInstruments() {

        getInstrumentsPrice();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                getInstrumentsPrice();         // this method will contain your almost-finished HTTP calls
                handler.postDelayed(this, THREE_SECONDS);
            }
        }, THREE_SECONDS);

    }

    public void getInstrumentsPrice() {
        // Preparing request header
        Map<String,String> accessTokenHeaders = new HashMap<>();
        accessTokenHeaders.put("Authorization",this.tokenType+" "+this.accessToken);

        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < fxSpotInstrument.length; i++) {
            if(i > 0)
                strBuilder.append(','+fxSpotInstrument[i].getIdentifier());
            else
                strBuilder.append(fxSpotInstrument[i].getIdentifier());
        }
        String uic = strBuilder.toString();
        String infoPricesUrl = getResources().getString(R.string.OpenApiBaseUrl)+getResources().getString(R.string.InfoPriceListUrl)+"?AssetType=FxSpot&Uics="+uic;

        NetworkConnection.getInstance(getApplicationContext()).sendStringRequest(Request.Method.GET, infoPricesUrl, null,accessTokenHeaders, new NetworkResponseHandler() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray jsonArray = obj.getJSONArray("Data");

                    for(int count = 0;count < jsonArray.length();count++){
                        JSONObject instrument = jsonArray.getJSONObject(count);
                        JSONObject quote = instrument.getJSONObject("Quote");
                        fxSpotInstrument[count].setPrice(quote.getString("Mid"));
                    }

                    displayFxSpotInstruments();
                } catch(Exception e) {
                    Log.e("JSON Parse",e.getMessage());
                }
            }
        });
    }

    public void displayFxSpotInstruments() {
        if(instrumentAdapter == null)
        {
            instrumentAdapter = new InstrumentAdapter(getApplicationContext(),R.layout.card_instrument,fxSpotInstrument);
            listView.setAdapter(instrumentAdapter);
            progressBar.setVisibility(View.INVISIBLE);
        }
        else {
            instrumentAdapter.setFxSpotInstrument(fxSpotInstrument);
            instrumentAdapter.notifyDataSetChanged();
        }
    }
}
