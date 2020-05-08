package pt.ulisboa.tecnico.cmov.foodist.fetch;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import javax.net.ssl.HttpsURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.security.cert.CertificateFactory;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.net.HttpURLConnection;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import pt.ulisboa.tecnico.cmov.foodist.activities.ListFoodServicesActivity;
import pt.ulisboa.tecnico.cmov.foodist.activities.MenuActivity;
import pt.ulisboa.tecnico.cmov.foodist.domain.Dish;
import pt.ulisboa.tecnico.cmov.foodist.domain.FoodService;
import pt.ulisboa.tecnico.cmov.foodist.domain.Menu;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;
import pt.ulisboa.tecnico.cmov.foodist.R;

public class fetchBaseCustom extends AsyncTask<Void, Void, Void> {

    private String URL;
    private String data = "";
    private GlobalClass global;

    public fetchBaseCustom(GlobalClass global, String URL) {
        this.global = global;
        this.URL = URL;
    }

    // NEEDS TO BE IMPLEMENTED BY SUBCLASS
    protected String buildBody() {
        return null;
    }
    // NEEDS TO BE IMPLEMENTED BY SUBCLASS
    protected void requestProperties(HttpsURLConnection conn) {
        return;
    }
    // NEEDS TO BE IMPLEMENTED BY SUBCLASS
    protected void parse(String data) {
        return;
    }
    // NEEDS TO BE IMPLEMENTED BY SUBCLASS
    protected void requestProperties(HttpURLConnection conn) {
        return;
    }

    @Override
    protected Void doInBackground(Void... voids){
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        // From https://www.washington.edu/itconnect/security/ca/load-der.crt
        try {
            InputStream caInput = global.getContext().getResources().openRawResource(R.raw.mycert);
            Certificate ca = null;

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            /*NOTE: this should not have been overrided*/
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    HostnameVerifier hv =
                            HttpsURLConnection.getDefaultHostnameVerifier();
                    //return hv.verify(hostname, session);
                    return URL.startsWith("https://"+session.getPeerHost());
                }
            };

            // Tell the URLConnection to use a SocketFactory from our SSLContext
            URL url = new URL(this.URL);
            HttpsURLConnection urlConnection =
                    (HttpsURLConnection) url.openConnection();
            urlConnection.setHostnameVerifier(hostnameVerifier);
            urlConnection.setSSLSocketFactory(context.getSocketFactory());


            Log.i("URL:", this.URL);
            urlConnection.setDoOutput(true);

            urlConnection.setRequestMethod("POST");
            //httpURLConnection.setRequestProperty("Authorization", key);
            requestProperties(urlConnection);
            urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);

            OutputStream out = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(buildBody());
            Log.i("BODY", buildBody());
            writer.flush();
            writer.close();

            urlConnection.connect();
            Log.i("RESPONSE", urlConnection.getResponseMessage());
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                data = data + line;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        parse(data);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.i("RESPONSE:", data);
    }


    public GlobalClass getGlobal(){
        return global;
    }

    public String getData() {
        return data;
    }

    public String getURL(){
        return URL;
    }
}
