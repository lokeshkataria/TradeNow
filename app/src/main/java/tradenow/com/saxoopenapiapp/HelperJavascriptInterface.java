package tradenow.com.saxoopenapiapp;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.net.URLDecoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * Created by Lokesh on 15-04-2017.
 */

public class HelperJavascriptInterface {

    private Context mCtx;

    public HelperJavascriptInterface(Context ctx) {
        mCtx = ctx;
    }

    @JavascriptInterface
    public void processHTML(String html) {

        // verifying if all the required attributes are present in the meta tag
        if(html.contains("<meta name=\"Application-State\"") &&
                html.contains("service=IDP") &&
                html.contains("state=Token") &&
                html.contains("authenticated=True")) {
            // retrieving SAML token from the HTML
            String samlResponse = html.substring(html.indexOf("sso_saml2_token")+17,html.indexOf("\"></body>"));
            try {
                // Decoding
                String base64DecodedResponse = new String(Base64.decode(samlResponse, Base64.DEFAULT));
                String urlDecodeResponse = URLDecoder.decode(base64DecodedResponse, "UTF-8");

                // Parsing response
                String authorizationCode = "";
                ByteArrayInputStream is = new ByteArrayInputStream(urlDecodeResponse.getBytes());
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                documentBuilderFactory.setNamespaceAware(true);
                DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = docBuilder.parse(is);
                Element element = document.getDocumentElement();

                NodeList list = ((Element) document.getElementsByTagName("saml:Assertion").item(0)).getElementsByTagName("saml:AttributeStatement");
                for (int i = 0; i < list.getLength(); i++) {
                    Element e = (Element) list.item(i);
                    Element n = (Element) e.getFirstChild();

                    if (n.getAttribute("Name").equals("AuthorizationCode")) {
                        authorizationCode = ((Element) n.getFirstChild()).getFirstChild().getNodeValue();
                        // Saving the authorization code in the shared preferences for further use
                        GlobalContext globalContext = GlobalContext.getInstance(mCtx);
                        globalContext.setAuthorizationCode(authorizationCode);
                    }
                }
            }
            catch (Exception e) {
                Log.e("urlDecoding",e.getMessage());
            }

            // After successful Login, moving to Home Page
            Intent intent = new Intent(mCtx,HomeActivity.class);
            mCtx.startActivity(intent);
        }
    }

}
