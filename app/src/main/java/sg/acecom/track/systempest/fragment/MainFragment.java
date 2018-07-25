package sg.acecom.track.systempest.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import sg.acecom.track.systempest.HistoryActivity;
import sg.acecom.track.systempest.JobActivity;
import sg.acecom.track.systempest.LoginActivity;
import sg.acecom.track.systempest.MaintenanceActivity;
import sg.acecom.track.systempest.QrCodeActivity;
import sg.acecom.track.systempest.R;
import sg.acecom.track.systempest.util.AppConstant;
import sg.acecom.track.systempest.util.AppController;
import sg.acecom.track.systempest.util.IMEI;
import sg.acecom.track.systempest.util.MyPreferences;
import sg.acecom.track.systempest.util.SnackBarUtil;

/**
 * Created by jmingl on 5/3/18.
 */

public class MainFragment extends Fragment implements View.OnClickListener{

    View view;
    CardView cvInventory;
    CardView cvJob;
    CardView cvMaintenance;
    CardView cvHistory;
    TextView userName, dateToday, userCompany,numberOfJobs;
    Button buttonLogout;
    MyPreferences pref;
    IMEI imei;
    LinearLayout layout_linear;
    AlertDialog.Builder alert_dialog;
    public static final int SNACKBAR_DURATION = 2000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_home, container, false);
        pref = new MyPreferences(getActivity());
        imei = new IMEI(getActivity());
        cvInventory = view.findViewById(R.id.cvInventory);
        layout_linear = view.findViewById(R.id.layout_linear);
        cvJob = view.findViewById(R.id.cvJob);
        cvMaintenance = view.findViewById(R.id.cvMaintenance);
        cvHistory = view.findViewById(R.id.cvHistory);
        numberOfJobs = view.findViewById(R.id.numberOfJobs);
        userName = view.findViewById(R.id.userName);
        userCompany = view.findViewById(R.id.userCompany);
        dateToday = view.findViewById(R.id.dateToday);
        buttonLogout = view.findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(this);
        cvInventory.setOnClickListener(this);
        cvJob.setOnClickListener(this);
        cvMaintenance.setOnClickListener(this);
        cvHistory.setOnClickListener(this);

        initContent();
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){

            case(R.id.cvInventory):
                intent = new Intent(getActivity(), QrCodeActivity.class);
                startActivity(intent);
                break;

            case(R.id.cvJob):
                intent = new Intent(getActivity(), JobActivity.class);
                startActivity(intent);
                break;

            case(R.id.cvMaintenance):
                intent = new Intent(getActivity(), MaintenanceActivity.class);
                startActivity(intent);
                break;

            case(R.id.cvHistory):
                intent = new Intent(getActivity(), HistoryActivity.class);
                startActivity(intent);
                //Snackbar.make(view, "History is under-development.", SNACKBAR_DURATION).show();
                break;

            case(R.id.buttonLogout):
                logoutAlert();
                break;
        }
    }

    public void initContent(){
        SimpleDateFormat format= new SimpleDateFormat("d MMMM, yyyy", Locale.getDefault());
        String myDate = format.format(new Date());

        userName.setText(pref.getPreferences("driver_name",""));
        userCompany.setText(pref.getPreferences("driver_company",""));
        //userCompany.setText("AceCom Technologies Pte Ltd");
        dateToday.setText(myDate);
        getNumberOfJobs();
    }

    public void logoutAlert() {
        alert_dialog = new AlertDialog.Builder(getActivity());
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

        AQuery aq = new AQuery(getActivity());
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

        AQuery aq = new AQuery(getActivity());
        aq.post(getAssetUrl, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String response, AjaxStatus status) {
                if (status.getMessage().equals("OK")) {
                    try {
                        JSONObject json = new JSONObject(response);

                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().finish();

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

    private void getNumberOfJobs(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(Calendar.getInstance().getTime());
        cal.add(Calendar.DATE, -3);
        cal.add(Calendar.HOUR, -8);
        Date eightHourBack = cal.getTime();
        String timestamp = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(eightHourBack);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 4);
        c.add(Calendar.HOUR, -8);
        String rxtime = sdf.format(c.getTime());

        final String url = AppConstant.endpoint_url + "jobinfo?Timestamp=" + URLEncoder.encode(timestamp) +
                "&RxTime=" + URLEncoder.encode(rxtime);

        //Log.e("Job List API : ", url);

        // prepare the Request
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        try{
                            numberOfJobs.setText(String.valueOf(response.length()));
                        }catch(Exception e){
                            Log.e("Response Exception :", String.valueOf(e));
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", error.toString());
                    }
                }
        );

        // Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(getRequest);
    }

}
