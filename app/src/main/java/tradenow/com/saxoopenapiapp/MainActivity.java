package tradenow.com.saxoopenapiapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // activity views
        final TextView appLogo = (TextView) findViewById(R.id.welcomeLogo);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressIcon);
        final TextView status = (TextView) findViewById(R.id.saxoConnectStatus);
        WebView webView = (WebView) findViewById(R.id.saxoConnect);

        // webview settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // webview javascriptInterface
        webView.addJavascriptInterface(new HelperJavascriptInterface(getApplicationContext()),"SaxoResponse");

        // webview webViewClient
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onLoadResource(WebView view, String url){
                view.setVisibility(View.VISIBLE);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // ProcessHTML function of HelperJavascriptInterface will be called to retrieve SAML Response
                view.loadUrl("javascript:window.SaxoResponse.processHTML(document.getElementsByTagName('html')[0].innerHTML)");
                appLogo.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                status.setVisibility(View.INVISIBLE);
            }
        });

        this.logonValidation(webView);
    }

    public void logonValidation(WebView webView) {
        String authenticationUrl = getResources().getString(R.string.AuthenticationUrl)+"/AuthnRequest";
        UUID uuid = UUID.randomUUID();
        String currentTimeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date());

        String samlpRequest = "<samlp:AuthnRequest ID=\"_"+uuid+"\" Version=\"2.0\" ForceAuthn=\"false\" IsPassive=\"false\" ProtocolBinding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" IssueInstant=\""+currentTimeStamp+"\" Destination=\""+authenticationUrl+"\" AssertionConsumerServiceURL=\""+getResources().getString(R.string.AppUrl)+"\"> <samlp:NameIDPolicy AllowCreate=\"false\" /> <saml:Issuer xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+getResources().getString(R.string.AppUrl)+"</saml:Issuer></samlp:AuthnRequest>";
        String connectionRequest = "";
        try {
            // Encoded SAML Request
            connectionRequest = "SAMLRequest="+URLEncoder.encode( Base64.encodeToString(samlpRequest.getBytes(),Base64.DEFAULT), "UTF-8");
            webView.postUrl(authenticationUrl,connectionRequest.getBytes());
        } catch (Exception uee) {
            Log.e("postError",uee.toString());
        }
    }
}
