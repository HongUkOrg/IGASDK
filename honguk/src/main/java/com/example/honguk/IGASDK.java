package com.example.honguk;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

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
import java.util.Map;

public class IGASDK
{
    private  static HttpURLConnection http;
    static String myResult = "fail";

    public static String key = "";
    public static String birth_day = "";
    public static String gender = "";
    public static String level = "";
    public static String character_class = "";
    public static String gold = "";



    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    public static String HttpPostData(JSONObject sample,String method)
    {

        try {

            String temp_url = "http://assignment.ad-brix.com/api/AddEvent";

            if(method.equals("add"))
            {
                temp_url = "http://assignment.ad-brix.com/api/AddEvent";
            }
            else if(method.equals("get"))
            {
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


            ContentValues contentValues = new ContentValues();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");






            JSONObject mjson= new JSONObject();
            JSONObject menu = new JSONObject();

            menu.put("menu_name","menu1");
            menu.put("menu_id",30);

            mjson.put("evt",menu);

            String myJson=sample.toString();







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
        catch (JSONException e) {
            e.printStackTrace();
        }



        http.disconnect();

        return myResult;

    }
    public static String getEvent(JSONObject hong)
    {

        return HttpPostData(hong,"get");


    }
    public static String addEvent(String eventName,Map<String,Object> hong) throws JSONException {

        JSONObject ResultJson = new JSONObject();

        JSONObject mJson = new JSONObject();
        for (Map.Entry<String, Object> entry : hong.entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue();
            mJson.put(key,value);
            // ...
        }

        JSONObject evtJson = new JSONObject();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        String current = dateFormat.format(new Date());

        evtJson.put("created_at",current);
        evtJson.put("event",eventName);
        evtJson.put("location",null);
        evtJson.put("param",mJson);

        JSONObject userPropertyJson = new JSONObject();
        userPropertyJson.put("birthyear",birth_day);
        userPropertyJson.put("gender",gender);
        userPropertyJson.put("level",level);
        userPropertyJson.put("character_class",character_class);
        userPropertyJson.put("gold",gold);

        evtJson.put("user_properties",userPropertyJson);


        ResultJson.put("evt",evtJson);


        JSONObject commonJson = new JSONObject();


        commonJson.put("package_name","com.example.honguk");
        String app_key = "appkey("+key+")";
        commonJson.put("appkey",app_key);

        ResultJson.put("common",commonJson);


        Log.d("resultJson", ResultJson.toString());


        return HttpPostData(ResultJson,"add");


    }

    public static void init(String appkey)
    {

        key = appkey;
    }

    public static void setUserProperty(Map<String,Object> keyvalue)
    {
        birth_day = keyvalue.get("birthyear").toString();
        gender = keyvalue.get("gender").toString();
        level = keyvalue.get("level").toString();
        character_class = keyvalue.get("character_class").toString();
        gold = keyvalue.get("gold").toString();

    }


    // HttpPostData
}
