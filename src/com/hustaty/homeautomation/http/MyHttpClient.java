package com.hustaty.homeautomation.http;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hustaty.homeautomation.R;
import com.hustaty.homeautomation.exception.HomeAutomationException;
import com.hustaty.homeautomation.model.ArduinoThermoServerStatus;
import com.hustaty.homeautomation.service.LocationService;
import com.hustaty.homeautomation.util.ApplicationPreferences;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyHttpClient extends DefaultHttpClient {

    // logger entry
    private final static String LOG_TAG = MyHttpClient.class.getName();

    private static String login;
    private static String password;
    private static String HOME_WIFI_SSID;
    private static String localNetworkServerIP;
    private static String globalServerIP;
    private static String URL_TO_USE;
    private static double homeLatitude;
    private static double homeLongitude;

    private final Context context;

    private String cookieInformation;

    public MyHttpClient(Context context) {
        this.context = context;

        try {
            Map<String, ?> preferences = ApplicationPreferences.getPreferences(context);
            MyHttpClient.login = (String) preferences.get("username");
            MyHttpClient.password = (String) preferences.get("password");
            HOME_WIFI_SSID = (String) preferences.get("wifiSSID");
            localNetworkServerIP = (String) preferences.get("localNetworkServerIP");
            globalServerIP = (String) preferences.get("globalServerIP");
            homeLatitude = Double.parseDouble((String) preferences.get("homeLatitude"));
            homeLongitude = Double.parseDouble((String) preferences.get("homeLongitude"));

        } catch (Exception e) {
            Log.e(LOG_TAG, "#MyHttpClient(): " + e.getMessage());
        }

        URL_TO_USE = globalServerIP;
    }

    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        // Register for port 443 our SSLSocketFactory with our keystore
        // to the ConnectionManager
        registry.register(new Scheme("https", newSslSocketFactory(), 443));
        return new SingleClientConnManager(getParams(), registry);
    }

    private boolean authenticate() throws IOException {
        Location location = LocationService.obtainCurrentLocation(context);
        String gpsData = "";
        if (location != null) {
            double distance = attemptToGuessURL(location);
            String deviceID = PreferenceManager.getDefaultSharedPreferences(context).getString("deviceID", "unknown");
            gpsData = "lat=" + location.getLatitude() + ",lon=" + location.getLongitude() + ",accuracy=" + location.getAccuracy() + ",distance=" + distance + ",deviceID=" + deviceID;
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        HttpPost post = new HttpPost("https://" + URL_TO_USE + "/index.php");
        post.addHeader("Host", URL_TO_USE);
        post.addHeader("User-Agent", "Hustaty Home Automation Android Client");
        post.addHeader("Connection", "Keep-Alive");
        post.addHeader("GPS-Location", gpsData);


        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("login", login));
        nameValuePairs.add(new BasicNameValuePair("password", password));

        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = this.execute(post);

        String setCookie = "";

        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            if (header.getName().equals("Set-Cookie")) {
                setCookie = header.getValue();
                this.cookieInformation = setCookie;
                break;
            }
        }

        return true;

    }


    public ArduinoThermoServerStatus getThermoServerStatus() throws IOException, HomeAutomationException {

        authenticate();

        HttpGet getV1 = new HttpGet("https://" + URL_TO_USE + "/v1/");
        getV1.addHeader("Host", URL_TO_USE);
        getV1.addHeader("Cookie", cookieInformation);

        HttpResponse response = this.execute(getV1);

        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        String responseText = "";
        String line = "";
        while ((line = rd.readLine()) != null) {
            responseText += line;
        }

        Gson gson = new Gson();
        ArduinoThermoServerStatus status = null;
        try {
            status = gson.fromJson(responseText, ArduinoThermoServerStatus.class);
        } catch (JsonSyntaxException jsonSyntaxException) {
            throw new HomeAutomationException(jsonSyntaxException);
        }

        this.getConnectionManager().shutdown();


        return status;

    }

    private SSLSocketFactory newSslSocketFactory() {
        try {
            // Get an instance of the Bouncy Castle KeyStore format
            KeyStore trusted = KeyStore.getInstance("BKS");
            // Get the raw resource, which contains the keystore with
            // your trusted certificates (root and any intermediate certs)
            InputStream in = context.getResources().openRawResource(R.raw.mykeystore);
            try {
                // Initialize the keystore with the provided trusted certificates
                // Also provide the password of the keystore
                trusted.load(in, "changeit".toCharArray());
            } finally {
                in.close();
            }
            // Pass the keystore to the SSLSocketFactory. The factory is responsible
            // for the verification of the server certificate.
            SSLSocketFactory sf = new SSLSocketFactory(trusted);
            // Hostname verification from certificate
            // http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private double attemptToGuessURL(Location location) {
        Location house = new Location(LocationManager.NETWORK_PROVIDER);
        house.setLatitude(homeLatitude);
        house.setLongitude(homeLongitude);

        //your current distance from home
        double distance = house.distanceTo(location);
        WifiInfo wifiInfo = getWifiInfo();
        if (HOME_WIFI_SSID != null
                && HOME_WIFI_SSID.equals(wifiInfo.getSSID())) {
            URL_TO_USE = localNetworkServerIP;
        } else {
            URL_TO_USE = globalServerIP;
        }
        return distance;
    }

    public WifiInfo getWifiInfo() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        int ipAddress = wifiInfo.getIpAddress();
//        String localIP = Formatter.formatIpAddress(ipAddress);
        return wifiInfo;
    }

    public static void useAnotherURL() {
        if (URL_TO_USE.equals(localNetworkServerIP)) {
            URL_TO_USE = globalServerIP;
        } else {
            URL_TO_USE = localNetworkServerIP;
        }
    }

}
