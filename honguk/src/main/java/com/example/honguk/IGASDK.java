package com.example.honguk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;


import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class IGASDK extends AsyncTask<Void, Void, String>{
    private static HttpURLConnection http;
    static String myResult = "fail";

    public static String key = "";
    public static String birth_day = "";
    public static String gender = "";
    public static String level = "";
    public static String character_class = "";
    public static String gold = "";

    private static Context mContext = null;
    private static String adid = "";
    private static boolean ad_state = false;

    @Override
    protected String doInBackground(final Void... params) {
        String adId = null;
        boolean state = false;
        try {
            adId = AdvertisingIdClient.getAdvertisingIdInfo(mContext).getId();
            state = AdvertisingIdClient.getAdvertisingIdInfo(mContext).isLimitAdTrackingEnabled();

            Log.d(TAG, "hello: "+adId+state);
            ad_state = state;
        } catch (IllegalStateException ex) {
            ex.printStackTrace();

        } catch (GooglePlayServicesRepairableException ex) {
            ex.printStackTrace();

        } catch (IOException ex) {
            ex.printStackTrace();

        } catch (GooglePlayServicesNotAvailableException ex) {
            ex.printStackTrace();

        }
        return adId;
    }

    @Override
    protected void onPostExecute(String adId)
    {
        adid = adId;

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    public static String HttpPostData(JSONObject sample, String method) {

        try {

            String temp_url = "http://assignment.ad-brix.com/api/AddEvent";

            if (method.equals("add")) {
                temp_url = "http://assignment.ad-brix.com/api/AddEvent";
            } else if (method.equals("get")) {
                temp_url = "http://assignment.ad-brix.com/api/GetEvent";
            }


            URL url = new URL(temp_url);
            // URL 설정
            http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");


            http.setRequestProperty("content-type", "application/json");


            String myJson = sample.toString();


            StringBuffer buffer = new StringBuffer();

            buffer.append(myJson);
//
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();


            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;


            while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
            }
            myResult = builder.toString();

        } catch (MalformedURLException e) {
            //
        } catch (IOException e) {
            //
        } // try


        http.disconnect();

        return myResult;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String GetEventLog(JSONObject hong) throws JSONException {
        String app_key = "appkey(" + key + ")";
        hong.put("appkey", app_key);

        return HttpPostData(hong, "get");


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String addEvent(String eventName, Map<String, Object> hong) throws JSONException {

        JSONObject ResultJson = new JSONObject();

        JSONObject mJson = new JSONObject();
        for (Map.Entry<String, Object> entry : hong.entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue();
            mJson.put(key, value);
            // ...
        }

        JSONObject evtJson = new JSONObject();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        String current = dateFormat.format(new Date());

        evtJson.put("created_at", current);
        evtJson.put("event", eventName);
        if (get_mylocation() == null) {
            evtJson.put("location", null);
        } else {
            evtJson.put("location", get_mylocation());
        }
        evtJson.put("param", mJson);

        JSONObject userPropertyJson = new JSONObject();
        userPropertyJson.put("birthyear", birth_day);
        userPropertyJson.put("gender", gender);
        userPropertyJson.put("level", level);
        userPropertyJson.put("character_class", character_class);
        userPropertyJson.put("gold", gold);

        evtJson.put("user_properties", userPropertyJson);


        ResultJson.put("evt", evtJson);


        JSONObject common = new JSONObject();
        common = getCommonJson();


        ResultJson.put("common", common);


        Log.d("resultJson", ResultJson.toString());


        return HttpPostData(ResultJson, "add");


    }

    public static void init(Context context, String appkey) {

        key = appkey;
        mContext = context;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(mContext);
                    adid = adInfo != null ? adInfo.getId() : null;
                    ad_state = adInfo != null? adInfo.isLimitAdTrackingEnabled() : null;

                    Log.d(TAG, "andinfo " + adid + " "+ ad_state);
                    // Use the advertising id
                } catch (IOException | GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException exception) {
                    // Error handling if needed
                }
            }
        });
    }

    public static void setUserProperty(Map<String, Object> keyvalue) {
        birth_day = keyvalue.get("birthyear").toString();
        gender = keyvalue.get("gender").toString();
        level = keyvalue.get("level").toString();
        character_class = keyvalue.get("character_class").toString();
        gold = keyvalue.get("gold").toString();

    }

    public static JSONObject get_mylocation() throws JSONException {
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location l = lm.getLastKnownLocation(provider);


            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {

                bestLocation = l;
            }
        }
        if (bestLocation == null) 
        {
            Log.d(TAG, "get_mylocation: best location null");
            return null;
        }


        JSONObject myLoc = new JSONObject();
        myLoc.put("lat",Double.toString(bestLocation.getLatitude()));
        myLoc.put("lng",Double.toString(bestLocation.getLongitude()));

        return myLoc;
    }

    public static JSONObject getCommonJson() throws JSONException {
        JSONObject commonJson = new JSONObject();
        JSONObject device_info_json = new JSONObject();

        final JSONObject identity_json = new JSONObject();

        identity_json.put("adid",adid);
        identity_json.put("adid_opt_out",ad_state);
        commonJson.put("identity",identity_json);


        String release = Build.VERSION.RELEASE;
        device_info_json.put("os",release);
        device_info_json.put("model",Build.MODEL);



        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        device_info_json.put("resolution",width+"x"+height);


        if(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            device_info_json.put("is_portrait","true");
        }
        else
        {
            device_info_json.put("is_portrait","false");
        }
        device_info_json.put("platform","android");
        ConnectivityManager cm =  (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            {
                device_info_json.put("network","WIFI");
            }
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                device_info_json.put("network","mobile_data");
            }
        }
        else
        {

            device_info_json.put("network","not_connected");
        }
        TelephonyManager manager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName = manager.getNetworkOperatorName();
        device_info_json.put("carrier",carrierName);
        device_info_json.put("language",mContext.getResources().getConfiguration().locale.getLanguage());
        device_info_json.put("country",Locale.getDefault().getCountry());






        commonJson.put("device_info",device_info_json);


        commonJson.put("package_name", mContext.getPackageName());
        String app_key = "appkey(" + key + ")";
        commonJson.put("appkey", app_key);





        return commonJson;

    }



    // HttpPostData
}
