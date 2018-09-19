package com.example.user.assignmenthonguk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.example.honguk.IGASDK.HttpPostData;
import static com.example.honguk.IGASDK.addEvent;
import static com.example.honguk.IGASDK.getEvent;
import static com.example.honguk.IGASDK.init;
import static com.example.honguk.IGASDK.setUserProperty;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 2001;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sample_btn = (Button)findViewById(R.id.sample_id);
        Button get_btn = (Button)findViewById(R.id.get_btn);
        final TextView sample_textview = (TextView)findViewById(R.id.sample_text);


        final String method = "get";

        init(this,"phwysl@gmail.com");
        setUserProperty(new HashMap<String, Object>()
        {
            {
                put("birthyear", 1993);
                put("gender", "m");
                put("level", 1);
                put("character_class", "magition");
                put("gold", 10);
            }
        });


        sample_btn.setOnClickListener(new View.OnClickListener()
        {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view)
            {
                String result = null;
                try {
                    result = addEvent("openMenu", new HashMap<String, Object>()
                    {
                        {
                            put("menu_name", "menu1");
                            put("menu_id", "30");

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sample_textview.setText(result);

            }
        });

        final JSONObject get_json = new JSONObject();
        try {
            get_json.put("length","100");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        get_btn.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view)
            {


                String result = "";
                JSONObject mResult=null;

                try {
                    mResult = new JSONObject(getEvent(get_json));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Iterator<?> keys = mResult.keys();
                while(keys.hasNext() ) {
                    String key = (String)keys.next();
                    try {
                        result+=key;
                        result+=" : ";
                        result+=mResult.getString(key);
                        result+="\n";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                Log.d("json", "onClick: "+mResult.toString());

                sample_textview.setText(result);

            }
        });
    }


    private boolean checkAndRequestPermissions() {

        List<String> listPermissionsNeeded = new ArrayList<>();
        listPermissionsNeeded.clear();


        int internet = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);//

        //int read_concise_call_state= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PRECISE_PHONE_STATE);//


        if (internet != PackageManager.PERMISSION_GRANTED) {

            listPermissionsNeeded.add(Manifest.permission.INTERNET);

        }


        int access_loc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);//
        if (access_loc != PackageManager.PERMISSION_GRANTED) {

            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);

        }

        if (!listPermissionsNeeded.isEmpty()) {

            Log.d("permission", "checkAndRequestPermissions: not empty");

            Toast.makeText(this, "not empty", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Please Allow All Permission To Continue..", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;

        }
    }
}
