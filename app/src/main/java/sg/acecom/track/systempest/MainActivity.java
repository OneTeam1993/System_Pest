package sg.acecom.track.systempest;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import sg.acecom.track.systempest.fragment.MainFragment;
import sg.acecom.track.systempest.model.Jobs;
import sg.acecom.track.systempest.util.AppConstant;
import sg.acecom.track.systempest.util.AppController;
import sg.acecom.track.systempest.util.IMEI;
import sg.acecom.track.systempest.util.LocationMonitoringService;
import sg.acecom.track.systempest.util.MyPreferences;

public class MainActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    private CountDownTimer timer;
    //Get IMEI
    private IMEI imei = new IMEI(this);
    MyPreferences pref;
    private boolean mAlreadyStartedService = false;
    boolean isGPSReady = false;
    private String user_lat, user_lng, user_miles;
    private String user_speed, user_direction, user_accuracy;
    private Button buttonLogout;
    AlertDialog.Builder alert_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = new MyPreferences(this);
        frameLayout = findViewById(R.id.frameLayout);
        buttonLogout = findViewById(R.id.buttonLogout);

        loadFragment(new MainFragment());

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        user_lat = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                        user_lng = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);
                        user_speed = intent.getStringExtra(LocationMonitoringService.EXTRA_SPEED);
                        user_direction = intent.getStringExtra(LocationMonitoringService.EXTRA_DIRECTION);
                        user_miles = intent.getStringExtra(LocationMonitoringService.EXTRA_MILEAGE);

                        if (user_lat != null && user_lng != null) {

                            //sendGPSData();

                            if(!isGPSReady)
                            {
                                sendGPSData();
                                isGPSReady = true;
                            }
                        }
                    }
                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        );

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutAlert();
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mAlreadyStartedService) {

            //Start location sharing service to app server.........
            Intent intent = new Intent(this, LocationMonitoringService.class);
            startService(intent);

            setTimer();

            /*myReceiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BackgroundLocationService.GPS_ACTION);
            registerReceiver(myReceiver, intentFilter);
            startService(new Intent(MainActivity.this, BackgroundLocationService.class));*/

            mAlreadyStartedService = true;
            //Ends................................................
        }
    }

    private void setTimer() {
        long sec = 15;
        timer =  new CountDownTimer(TimeUnit.SECONDS.toMillis(sec), 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                sendGPSData();
                resetTimer();
            }
        }.start();
    }

    private void resetTimer() {
        timer.cancel();
        setTimer();
    }

    private void sendGPSData() {

        /*final String gps_message = "$$JDS$," + getDeviceIMEI() + "," + getUTCDateAndTime() + "," + user_lat +
                "," + user_lng + "," + user_speed + "," + user_miles +
                "," + user_direction + "," + "1" + "," + "0" + "," + "0" + "#";*/

        final String gps_message = "$$JDS$," + getDeviceIMEI() + "," + getUTCDateAndTime() + "," + "1.326329" +
                "," + "103.896124" + "," + user_speed + "," + user_miles +
                "," + user_direction + "," + "1" + "," + "0" + "," + "0" + "#";

        //Log.e("GPS :",gps_message);

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                Socket socket = null;
                DataOutputStream dataOutputStream = null;
                DataInputStream dataInputStream = null;
                try {
                    socket = new Socket(AppConstant.ads_host, AppConstant.ads_port);
                    //socket = new Socket(AppConstant.ads_host, AppConstant.ads_port);
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    System.out.println("sending data...");
                    byte[] buf = gps_message.getBytes("UTF-8");
                    dataOutputStream.write(buf, 0, buf.length);
//                    dataOutputStream.writeUTF(gps_message);
                    System.out.println("data sent...");
//                    textIn.setText(dataInputStream.readUTF());
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    Log.e("Unknown Host : ", String.valueOf(e));
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.e("IO Exception : ", String.valueOf(e));
                    e.printStackTrace();
                } finally {
                    if (socket != null) {
                        try {
                            socket.close();
                            System.out.println("socket closed...");
                        } catch (IOException e) {
                            Log.e("IO Exception 2 : ", String.valueOf(e));
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    if (dataOutputStream != null) {
                        try {
                            dataOutputStream.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            Log.e("IO Exception 3 : ", String.valueOf(e));
                            e.printStackTrace();
                        }
                    }

                    if (dataInputStream != null) {
                        try {
                            dataInputStream.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            Log.e("IO Exception 4 : ", String.valueOf(e));
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer != null){
            timer.cancel();
        }
        stopService(new Intent(MainActivity.this, LocationMonitoringService.class));
    }

    private String getDeviceIMEI()
    {
        imei.setDeviceID();

        return imei.getDeviceID();
    }

    private String getUTCDateAndTime() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    public void logoutAlert() {
        alert_dialog = new AlertDialog.Builder(this);
        alert_dialog.setCancelable(true);
        alert_dialog.setTitle("Log Out");
        alert_dialog.setMessage("Are you sure you want to log out?");
        alert_dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert_dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

                pref.removePreferences("session_login");
                pref.removePreferences("session_user_name");
                pref.removePreferences("session_user_password");
                saveLogoutEvents();
            }
        });
        alert_dialog.show();
    }

    private void saveLogoutEvents() {

        String apiEventsUrl = AppConstant.endpoint_url + "savelogout";
        JSONObject params = new JSONObject();
        try {
            params.put("Tag", getIMEI());
            params.put("DriverID", pref.getPreferences("driver_id", ""));
            params.put("Driver", pref.getPreferences("driver_name", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AQuery aq = new AQuery(this);
        aq.post(apiEventsUrl, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String response, AjaxStatus status) {
                if (status.getMessage().equals("OK")) {
                    try {
                        JSONObject json = new JSONObject(response);
                        logoutDriver();
                    } catch (JSONException e) {
                    }
                } else {

                }
            }
        });
    }

    private void logoutDriver() {

        String getAssetUrl = AppConstant.endpoint_url + "logoutdriver";
        JSONObject params = new JSONObject();
        try {
            params.put("Tag", getIMEI());
            params.put("DriverID", pref.getPreferences("driver_id", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AQuery aq = new AQuery(this);
        aq.post(getAssetUrl, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String response, AjaxStatus status) {
                if (status.getMessage().equals("OK")) {
                    try {
                        JSONObject json = new JSONObject(response);
                        stopService(new Intent(MainActivity.this, LocationMonitoringService.class));
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {

                    }
                } else {

                }
            }
        });
    }

    private String getIMEI(){
        imei.setDeviceID();

        return imei.getDeviceID();
    }

}
