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
import com.google.gson.reflect.TypeToken;
import com.hustaty.homeautomation.R;
import com.hustaty.homeautomation.enums.Appliance;
import com.hustaty.homeautomation.enums.Command;
import com.hustaty.homeautomation.enums.SharedPreferencesKeys;
import com.hustaty.homeautomation.exception.HomeAutomationException;
import com.hustaty.homeautomation.model.*;
import com.hustaty.homeautomation.service.LocationService;
import com.hustaty.homeautomation.util.ApplicationPreferences;
import com.hustaty.homeautomation.util.LogUtil;
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
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private static String globalServerIPFromGCM;
    private static String URL_TO_USE;
    private static double homeLatitude;
    private static double homeLongitude;

    private final Context context;

    private String cookieInformation;

    public MyHttpClient(Context context) {
        this.context = context;

        try {
            Map<String, ?> preferences = ApplicationPreferences.getPreferences(context);
            MyHttpClient.login = (String) preferences.get(SharedPreferencesKeys.APPLICATIONPREFERENCES_USERNAME.getKey());
            MyHttpClient.password = (String) preferences.get(SharedPreferencesKeys.APPLICATIONPREFERENCES_PASSWORD.getKey());
            HOME_WIFI_SSID = (String) preferences.get(SharedPreferencesKeys.APPLICATIONPREFERENCES_WIFI_SSID.getKey());
            localNetworkServerIP = (String) preferences.get(SharedPreferencesKeys.APPLICATIONPREFERENCES_LOCALSERVERIPADDRESS.getKey());
            globalServerIP = (String) preferences.get(SharedPreferencesKeys.APPLICATIONPREFERENCES_GLOBALSERVERIPADDRESS.getKey());
            globalServerIPFromGCM = (String) preferences.get(SharedPreferencesKeys.APPLICATIONPREFERENCES_RECEIVED_GLOBALSERVERIPADDRESS.getKey());
            homeLatitude = Double.parseDouble((String) preferences.get(SharedPreferencesKeys.APPLICATIONPREFERENCES_HOMEGPSLAT.getKey()));
            homeLongitude = Double.parseDouble((String) preferences.get(SharedPreferencesKeys.APPLICATIONPREFERENCES_HOMEGPSLON.getKey()));


        } catch (Exception e) {
            Log.e(LOG_TAG, "#MyHttpClient(): " + e.getMessage());
            LogUtil.appendLog(LOG_TAG + "#MyHttpClient(): " + e.getMessage());

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
        ClientConnectionManager clientConnectionManager = new ThreadSafeClientConnManager(getParams(), registry);
//        ClientConnectionManager clientConnectionManager = new SingleClientConnManager(getParams(), registry);
        return clientConnectionManager;
    }

    /**
     * Athenticates the request.
     *
     * @return
     * @throws IOException
     */
    private boolean authenticate() throws IOException {
        Location location = LocationService.obtainCurrentLocation(context);
        String gpsData = "";
        Double distance = attemptToGuessURL(location);

        if (location != null) {
            String deviceID = PreferenceManager.getDefaultSharedPreferences(context).getString(SharedPreferencesKeys.APPLICATIONPREFERENCES_DEVICEID.getKey(), "unknown");
            DeviceLocationInfo deviceLocationInfo = new DeviceLocationInfo();
            deviceLocationInfo.setLatitude(location.getLatitude());
            deviceLocationInfo.setLongitude(location.getLongitude());
            deviceLocationInfo.setAccuracy(location.getAccuracy());
            deviceLocationInfo.setDistance(distance == null ? 150000 : distance); //150km rather than expecting the user be home
            deviceLocationInfo.setDeviceId(deviceID);
            WifiInfo wifiInfo = getWifiInfo();
            if(wifiInfo != null
                && wifiInfo.getSSID() != null) {
                switch(wifiInfo.getSupplicantState()) {
                    case DISCONNECTED:
                    case UNINITIALIZED:
                    case INACTIVE:
                        break;
                    default:
                        deviceLocationInfo.setWifi(wifiInfo.getSSID().replace("\"",""));
                        LogUtil.appendLog(LOG_TAG + "#authenticate(): setting wifi " + deviceLocationInfo.getWifi());
                        Log.d(LOG_TAG, "#authenticate(): setting wifi " + deviceLocationInfo.getWifi());
                        break;
                }
            }
            Gson gson = new Gson();
            gpsData = gson.toJson(deviceLocationInfo, DeviceLocationInfo.class);
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if(this.getConnectionManager() == null) {
            this.createClientConnectionManager();
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


    /**
     * Gets current state of thermoServer.
     *
     * @return
     * @throws IOException
     * @throws HomeAutomationException
     */
    public ArduinoThermoServerStatus getThermoServerStatus(boolean shutdownAfterGettingInfo) throws IOException, HomeAutomationException {

        authenticate();

        HttpGet getV1 = new HttpGet("https://" + URL_TO_USE + "/v1/");
        getV1.addHeader("Host", URL_TO_USE);
        getV1.addHeader("Cookie", cookieInformation);

        HttpResponse response = this.execute(getV1);

        Gson gson = new Gson();
        ArduinoThermoServerStatus status = null;
        try {
            status = gson.fromJson(httpResponseText(response), ArduinoThermoServerStatus.class);
            LogUtil.appendLog(LOG_TAG + "#getStoredEventResults(" + shutdownAfterGettingInfo + "): " + status);
        } catch (JsonSyntaxException jsonSyntaxException) {
            LogUtil.appendLog(LOG_TAG + "#getThermoServerStatus(" + shutdownAfterGettingInfo + "): " + jsonSyntaxException.getMessage());
            throw new HomeAutomationException(jsonSyntaxException);
        }

        if(shutdownAfterGettingInfo) {
            shutdownConnectionManager();
        }

        return status;

    }

    /**
     * Adds a stored event.
     *
     * @param appliance
     * @param command
     * @param validFrom
     * @param validUntil
     * @return
     * @throws IOException
     * @throws HomeAutomationException
     */
    public CommonResult addStoredEvent(Appliance appliance, Command command, Date validFrom, Date validUntil, boolean shutdownAfterGettingInfo) throws IOException, HomeAutomationException {

        authenticate();

        HttpPost post = new HttpPost("https://" + URL_TO_USE + "/v1/addStoredEvent.php");

        post.addHeader("Host", URL_TO_USE);
        post.addHeader("User-Agent", "Hustaty Home Automation Android Client");
        post.addHeader("Cookie", cookieInformation);
        post.addHeader("Connection", "Keep-Alive");

        SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("applianceName", appliance.getValue()));
        nameValuePairs.add(new BasicNameValuePair("commandName", command.getValue()));
        nameValuePairs.add(new BasicNameValuePair("validFrom", mysqlDateFormat.format(validFrom)));
        nameValuePairs.add(new BasicNameValuePair("validUntil", mysqlDateFormat.format(validUntil)));
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = this.execute(post);

        Gson gson = new Gson();
        CommonResult result = null;
        try {
            result = gson.fromJson(httpResponseText(response), CommonResult.class);
            Log.d(LOG_TAG, "#addStoredEvent(" + appliance.getValue() + "," + command.getValue() + "," + validFrom + "," + validUntil + "," + shutdownAfterGettingInfo + "): " + result.getResult());
            LogUtil.appendLog(LOG_TAG + "#addStoredEvent(" + appliance.getValue() + "," + command.getValue() + "," + validFrom + "," + validUntil + "," + shutdownAfterGettingInfo + "): " + result.getResult());
        } catch (JsonSyntaxException jsonSyntaxException) {
            LogUtil.appendLog(LOG_TAG + "#addStoredEvent(" + appliance.getValue() + "," + command.getValue() + "," + validFrom + "," + validUntil + "," + shutdownAfterGettingInfo + "): " + jsonSyntaxException.getMessage());
            Log.d(LOG_TAG, "#addStoredEvent(" + appliance.getValue() + "," + command.getValue() + "," + validFrom + "," + validUntil + "," + shutdownAfterGettingInfo + "): " + result.getResult());
            throw new HomeAutomationException(jsonSyntaxException);
        }

        if(shutdownAfterGettingInfo) {
            shutdownConnectionManager();
        }

        return result;

    }

    /**
     * Removes all old and ongoing stored events for desired appliance.
     *
     * @param appliance
     * @return
     * @throws IOException
     * @throws HomeAutomationException
     */
    public CommonResult removeStoredEvent(Appliance appliance, boolean shutdownAfterGettingInfo) throws IOException, HomeAutomationException {

        authenticate();

        HttpPost post = new HttpPost("https://" + URL_TO_USE + "/v1/removeStoredEvent.php");

        post.addHeader("Host", URL_TO_USE);
        post.addHeader("User-Agent", "Hustaty Home Automation Android Client");
        post.addHeader("Cookie", cookieInformation);
        post.addHeader("Connection", "Keep-Alive");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("applianceName", appliance.getValue()));
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = this.execute(post);

        Gson gson = new Gson();
        CommonResult result = null;
        try {
            result = gson.fromJson(httpResponseText(response), CommonResult.class);
            Log.d(LOG_TAG, "#removeStoredEvent(" + appliance.getValue() + "," + shutdownAfterGettingInfo + "): " + result);
            LogUtil.appendLog(LOG_TAG + "#removeStoredEvent(" + appliance.getValue() + "," + shutdownAfterGettingInfo + "): " + result.getResult());
        } catch (JsonSyntaxException jsonSyntaxException) {
            LogUtil.appendLog(LOG_TAG + "#removeStoredEvent(" + shutdownAfterGettingInfo + "): " + jsonSyntaxException.getMessage());
            Log.d(LOG_TAG, "#removeStoredEvent(" + appliance.getValue() + "," + shutdownAfterGettingInfo + "): " + result.getResult());
            throw new HomeAutomationException(jsonSyntaxException);
        }

        if(shutdownAfterGettingInfo) {
            shutdownConnectionManager();
        }

        return result;

    }

    /**
     * Adds entry about successfully registered device to DB.
     *
     * @param deviceID
     * @param gcmId
     * @param shutdownAfterGettingInfo
     * @return
     * @throws IOException
     * @throws HomeAutomationException
     */
    public CommonResult addGCMDeviceEntry(String deviceID, String gcmId, boolean shutdownAfterGettingInfo) throws IOException, HomeAutomationException {

        authenticate();

        HttpPost post = new HttpPost("https://" + URL_TO_USE + "/v1/registerGcm.php");

        post.addHeader("Host", URL_TO_USE);
        post.addHeader("User-Agent", "Hustaty Home Automation Android Client");
        post.addHeader("Cookie", cookieInformation);
        post.addHeader("Connection", "Keep-Alive");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("deviceID", deviceID));
        nameValuePairs.add(new BasicNameValuePair("gcmid", gcmId));
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = this.execute(post);

        Gson gson = new Gson();
        CommonResult result = null;
        try {
            result = gson.fromJson(httpResponseText(response), CommonResult.class);
            LogUtil.appendLog(LOG_TAG + "#addGCMDeviceEntry('" + deviceID + "','" + gcmId + "'," + shutdownAfterGettingInfo + "): " + result);
        } catch (JsonSyntaxException jsonSyntaxException) {
            LogUtil.appendLog(LOG_TAG + "#addGCMDeviceEntry(" + shutdownAfterGettingInfo + "): " + jsonSyntaxException.getMessage());
            throw new HomeAutomationException(jsonSyntaxException);
        }

        if(shutdownAfterGettingInfo) {
            shutdownConnectionManager();
        }

        return result;

    }

    /**
     * Gets List of all stored events.
     *
     * @param appliance
     * @return
     * @throws IOException
     * @throws HomeAutomationException
     */
    public List<StoredEventResult> getStoredEventResults(Appliance appliance, boolean shutdownAfterGettingInfo) throws IOException, HomeAutomationException {

        authenticate();

        HttpPost post = new HttpPost("https://" + URL_TO_USE + "/v1/getStoredEvents.php");

        post.addHeader("Host", URL_TO_USE);
        post.addHeader("User-Agent", "Hustaty Home Automation Android Client");
        post.addHeader("Cookie", cookieInformation);
        post.addHeader("Connection", "Keep-Alive");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("applianceName", appliance.getValue()));
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = this.execute(post);

        Gson gson = new Gson();
        List<StoredEventResult> result = null;

        Type arr = new TypeToken<List<StoredEventResult>>(){}.getType();

        try {
            result = gson.fromJson(httpResponseText(response), arr);
            LogUtil.appendLog(LOG_TAG + "#getStoredEventResults(" + appliance.getValue() + "," + shutdownAfterGettingInfo + "): " + result);
        } catch (JsonSyntaxException jsonSyntaxException) {
            LogUtil.appendLog(LOG_TAG + "#getStoredEventResults(" + shutdownAfterGettingInfo + "): " + jsonSyntaxException.getMessage());
            throw new HomeAutomationException(jsonSyntaxException);
        }

        if(shutdownAfterGettingInfo) {
            shutdownConnectionManager();
        }


        return result;

    }


    /**
     * Retreives traffic onformation in your area.
     *
     * @param shutdownAfterGettingInfo connection manager shutdown true/false
     * @return list of traffic information in your area
     * @throws IOException
     * @throws HomeAutomationException
     */
    public List<TrafficInformation> getTrafficInformation(boolean shutdownAfterGettingInfo)  throws IOException, HomeAutomationException {

        Location location = LocationService.obtainCurrentLocation(context);

        if(location == null
                || (new Date()).after(new Date(location.getTime() + LocationService.GPS_TIMEOUT))) {
            return null;//new ArrayList<TrafficInformation>();
        }

        //this is by default done in authentication but this part is allowed also for unauthenticated access
        double distance = attemptToGuessURL(location);

        HttpPost post = new HttpPost("https://" + URL_TO_USE + "/traffic/");

        post.addHeader("Host", URL_TO_USE);
        post.addHeader("User-Agent", "Hustaty Home Automation Android Client");
        post.addHeader("Connection", "Keep-Alive");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if(location != null) {
            nameValuePairs.add(new BasicNameValuePair("lat", "" + location.getLatitude()));
            nameValuePairs.add(new BasicNameValuePair("lon", "" + location.getLongitude()));
        }
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = this.execute(post);

        Gson gson = new Gson();
        List<TrafficInformation> result = null;

        Type arr = new TypeToken<List<TrafficInformation>>(){}.getType();

        try {
            result = gson.fromJson(httpResponseText(response), arr);
            LogUtil.appendLog(LOG_TAG + "#getTrafficInformation(shutdown:" + shutdownAfterGettingInfo + "): " + result);
        } catch (JsonSyntaxException jsonSyntaxException) {
            LogUtil.appendLog(LOG_TAG + "#getTrafficInformation(shutdown:" + shutdownAfterGettingInfo + "): " + jsonSyntaxException.getMessage());
            throw new HomeAutomationException(jsonSyntaxException);
        } finally {
            if(shutdownAfterGettingInfo) {
                shutdownConnectionManager();
            }

            return result;
        }

    }

    private void shutdownConnectionManager() {
        this.getConnectionManager().shutdown();
    }

    /**
     * SSL with self-signed certificate.
     *
     * @return
     */
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

    /**
     * Util function for guessing URL.
     *
     * @param location
     * @return
     */
    private double attemptToGuessURL(Location location) {

        double distance = 0;

        if(location != null) {
            Location house = new Location(LocationManager.NETWORK_PROVIDER);
            house.setLatitude(homeLatitude);
            house.setLongitude(homeLongitude);

            //your current distance from home
            distance = house.distanceTo(location);
        }

        WifiInfo wifiInfo = getWifiInfo();
        if (HOME_WIFI_SSID != null
                && (wifiInfo != null)
                && (wifiInfo.getSSID() != null)
                && HOME_WIFI_SSID.equalsIgnoreCase(wifiInfo.getSSID().replace("\"", ""))) {
            URL_TO_USE = localNetworkServerIP;
        } else {
            if(!URL_TO_USE.equals(globalServerIPFromGCM)
                    && !"0.0.0.0".equals(globalServerIPFromGCM)) {
                URL_TO_USE = globalServerIPFromGCM;
            } else {
                URL_TO_USE = globalServerIP;
            }
        }
        return distance;
    }

    /**
     * Gets basic WiFi connection information.
     *
     * @return
     */
    public WifiInfo getWifiInfo() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        int ipAddress = wifiInfo.getIpAddress();
//        String localIP = Formatter.formatIpAddress(ipAddress);
        return wifiInfo;
    }

    /**
     * Swap the URLs.
     */
    public static void useAnotherURL() {
        if (URL_TO_USE.equalsIgnoreCase(localNetworkServerIP)) {
            URL_TO_USE = globalServerIP;
        } else if(URL_TO_USE.equalsIgnoreCase(globalServerIP)
                && !"0.0.0.0".equalsIgnoreCase(globalServerIPFromGCM) //not default
                && globalServerIPFromGCM != null) { //not null
            URL_TO_USE = globalServerIPFromGCM;
        } else {
            URL_TO_USE = localNetworkServerIP;
        }
        Log.d(LOG_TAG, "#useAnotherURL(): switching URL_TO_USE to: " + URL_TO_USE);
    }

    /**
     * Parse response to text.
     *
     * @param response
     * @return
     * @throws IOException
     */
    private String httpResponseText(HttpResponse response) throws IOException {

        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        String responseText = "";
        String line = "";
        while ((line = rd.readLine()) != null) {
            responseText += line;
        }

        return responseText;
    }
}
