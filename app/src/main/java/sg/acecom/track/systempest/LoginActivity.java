package sg.acecom.track.systempest;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.List;

import sg.acecom.track.systempest.util.AppConstant;
import sg.acecom.track.systempest.util.IMEI;
import sg.acecom.track.systempest.util.Keccak;
import sg.acecom.track.systempest.util.MyPreferences;

/**
 * Created by jmingl on 5/3/18.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    MyPreferences pref = new MyPreferences(this);
    IMEI imei;
    EditText etUserName;
    EditText etUserPassword;
    CheckBox checkboxSaveLogin;
    Button buttonLogin;
    ProgressDialog nDialog;
    private Boolean saveLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        imei = new IMEI(this);
        nDialog = new ProgressDialog(LoginActivity.this);
        etUserName = findViewById(R.id.etUserName);
        etUserPassword = findViewById(R.id.etUserPassword);
        checkboxSaveLogin = findViewById(R.id.checkboxSaveLogin);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(this);

        if(pref.getPreferences("session_login","").equals("true")){
            sessionLogin();
        }

        saveLogin = pref.getPreferencesBoolean("saveLogin", false);
        if (saveLogin == true) {
            etUserName.setText(pref.getPreferences("username",""));
            etUserPassword.setText(pref.getPreferences("password",""));
            checkboxSaveLogin.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case(R.id.buttonLogin):
                showDialog();
                login();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermissions();
    }

    private void login() {
        String url = AppConstant.endpoint_url + "app";
        JSONObject params = new JSONObject();
        try {
            params.put("Name", etUserName.getText().toString());
            params.put("Password", sha3(etUserPassword.getText().toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AQuery aq = new AQuery(this);
        aq.post(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String response, AjaxStatus status) {
                if (status.getMessage().equals("OK")) {
                    try
                    {
                        JSONObject json = new JSONObject(response);
                        String error_message = json.getString("ErrorMessage");
                        String driver_id = json.getString("DriverID");
                        String name = json.getString("Name");
                        String password = json.getString("Password");
                        String address = json.getString("Address");
                        String email = json.getString("Email");
                        String phone = json.getString("Phone");
                        String company_id = json.getString("CompanyID");
                        String company_name = json.getString("Company");
                        String reseller_id = json.getString("ResellerID");

                        if(error_message.equals("null"))
                        {
                            pref.savePreferences("driver_id",driver_id);
                            pref.savePreferences("driver_password",etUserPassword.getText().toString());
                            pref.savePreferences("driver_name",name);
                            pref.savePreferences("driver_address",address);
                            pref.savePreferences("driver_company",company_name);
                            pref.savePreferences("driver_email", email);
                            pref.savePreferences("driver_phone", phone);
                            pref.savePreferences("driver_company_id", company_id);
                            pref.savePreferences("driver_reseller_id", reseller_id);

                            //pref.savePreferences("driver_company_id", String.valueOf(AppConstant.jds_company_id));
                            //Log.e("driver id", pref.getPreferences("driver_id",""));
                            getAsset();
                            getAssetEx();
                            countDriver();

                        }
                        else
                        {
                            nDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                        }

                    }
                    catch (JSONException e) {
                        //Failed login
                        nDialog.dismiss();
                        Log.e("Login Exception :", String.valueOf(e));
                    }
                } else {
                    nDialog.dismiss();
                    //No Connection
                }
            }
        });
    }

    private void getAsset() {
        String getAssetUrl = AppConstant.endpoint_url + "getasset";
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
                        String assets_id = json.getString("AssetID");
                        String company_id = json.getString("CompanyID");
                        //pref.savePreferences("driver_company_id", company_id);

                        //Log.e("Company ID : ", pref.getPreferences("driver_company_id",""));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        nDialog.dismiss();
                        Log.e("GetAsset Exception", String.valueOf(e));
                    }
                } else {
                    nDialog.dismiss();
                    //hideDialog();
                    //Exit
                }
            }
        });
    }

    private void countDriver(){

        String getAssetUrl = AppConstant.endpoint_url + "countdriver";

        JSONObject params = new JSONObject();
        try {
            params.put("Name", etUserName.getText().toString());
            params.put("Password", sha3(etUserPassword.getText().toString()));
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
                        String error_message = json.getString("ErrorMessage");
                        if (!error_message.equals("null")) {
                            nDialog.dismiss();
                            Log.e("Countdriver Error", error_message);
                        } else {
                            saveLoginEvents();
                        }
                    } catch (JSONException e) {
                        nDialog.dismiss();
                    }

                } else {
                    nDialog.dismiss();
                }
            }
        });
    }

    private void getAssetEx() {
        String getAssetExUrl = AppConstant.endpoint_url + "getassetex";
        JSONObject params = new JSONObject();
        try {
            params.put("Tag", getIMEI());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AQuery aq = new AQuery(this);
        aq.post(getAssetExUrl, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String response, AjaxStatus status) {
                if (status.getMessage().equals("OK")) {
                    try {
                        JSONObject json = new JSONObject(response);
                        String error_message = json.getString("ErrorMessage");
                        String tag = json.getString("Tag");
                        if (tag.equals(getIMEI())) {
                            String assets_id = json.getString("AssetID");
                            String company_id = json.getString("CompanyID");
                            pref.savePreferences("driver_assets_id",assets_id);
                            //Log.e("Asset_id", assets_id);
                            //Log.e("Company_id", company_id);

                        } else {
                            nDialog.dismiss();
                            Log.e("GetAssetEx", error_message);
                        }

                    } catch (JSONException e) {
                        nDialog.dismiss();
                        Log.e("GetAssetEx Exception", String.valueOf(e));
                    }
                } else {
                    nDialog.dismiss();
                }
            }
        });
    }

    private void saveLoginEvents() {

        String apiEventsUrl = AppConstant.endpoint_url + "savelogin";

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

                        if (checkboxSaveLogin.isChecked()) {
                            pref.savePreferences("username", etUserName.getText().toString());
                            pref.savePreferences("password", etUserPassword.getText().toString());
                            pref.savePreferencesBoolean("saveLogin", true);
                        } else {
                            pref.removePreferences("username");
                            pref.removePreferences("password");
                            pref.removePreferences("saveLogin");
                        }

                        pref.savePreferences("session_login", "true");
                        pref.savePreferences("session_user_name", etUserName.getText().toString());
                        pref.savePreferences("session_user_password", etUserPassword.getText().toString());

                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {
                        nDialog.dismiss();
                    }
                } else {
                    nDialog.dismiss();
                }
            }
        });
    }

    private void sessionLogin() {
        String url = AppConstant.endpoint_url + "app";
        JSONObject params = new JSONObject();
        try {
            params.put("Name", pref.getPreferences("session_user_name",""));
            params.put("Password", sha3(pref.getPreferences("session_user_password","")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AQuery aq = new AQuery(this);
        aq.post(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String response, AjaxStatus status) {
                if (status.getMessage().equals("OK")) {
                    try
                    {
                        JSONObject json = new JSONObject(response);
                        String error_message = json.getString("ErrorMessage");
                        String driver_id = json.getString("DriverID");
                        String name = json.getString("Name");
                        String password = json.getString("Password");
                        String address = json.getString("Address");
                        String email = json.getString("Email");
                        String phone = json.getString("Phone");
                        String company_id = json.getString("CompanyID");
                        String company_name = json.getString("Company");
                        String reseller_id = json.getString("ResellerID");

                        if(error_message.equals("null"))
                        {
                            pref.savePreferences("driver_id",driver_id);
                            pref.savePreferences("driver_password",etUserPassword.getText().toString());
                            pref.savePreferences("driver_name",name);
                            pref.savePreferences("driver_address",address);
                            pref.savePreferences("driver_company",company_name);
                            pref.savePreferences("driver_email", email);
                            pref.savePreferences("driver_phone", phone);
                            pref.savePreferences("driver_company_id", company_id);
                            pref.savePreferences("driver_reseller_id", reseller_id);

                            //pref.savePreferences("driver_company_id", String.valueOf(AppConstant.jds_company_id));
                            //Log.e("driver id", pref.getPreferences("driver_id",""));
                            getAsset();
                            getAssetEx();
                            sessionCountDriver();

                        }
                        else
                        {
                            nDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                        }

                    }
                    catch (JSONException e) {
                        //Failed login
                        nDialog.dismiss();
                        Log.e("Login Exception :", String.valueOf(e));
                    }
                } else {
                    nDialog.dismiss();
                    //No Connection
                }
            }
        });
    }

    private void sessionCountDriver(){

        String getAssetUrl = AppConstant.endpoint_url + "countdriver";

        JSONObject params = new JSONObject();
        try {
            params.put("Name", pref.getPreferences("session_username",""));
            params.put("Password", sha3(pref.getPreferences("session_password","")));
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
                        String error_message = json.getString("ErrorMessage");
                        if (!error_message.equals("null")) {
                            nDialog.dismiss();
                            Log.e("Countdriver Error", error_message);
                        } else {
                            sessionSaveLoginEvents();
                        }
                    } catch (JSONException e) {
                        nDialog.dismiss();
                    }

                } else {
                    nDialog.dismiss();
                }
            }
        });
    }

    private void sessionSaveLoginEvents() {

        String apiEventsUrl = AppConstant.endpoint_url + "savelogin";

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

                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {
                        nDialog.dismiss();
                    }
                } else {
                    nDialog.dismiss();
                }
            }
        });
    }

    private static String sha3(final String input) {
        byte[] b = input.getBytes();
        String s = getHexStringByByteArray(b);
        Keccak keccak = new Keccak(1600);
        return keccak.getHash(s, 1088, 32);
    }

    public static String getHexStringByByteArray(byte[] array) {
        if (array == null)
            return null;
        StringBuilder stringBuilder = new StringBuilder(array.length * 2);
        @SuppressWarnings("resource")
        Formatter formatter = new Formatter(stringBuilder);
        for (byte tempByte : array)
            formatter.format("%02x", tempByte);
        return stringBuilder.toString();
    }

    public static final String md5(final String s) {
        try {
            //create md5 has
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            //create hex string
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getIMEI(){
        imei.setDeviceID();

        return imei.getDeviceID();
    }

    private void showDialog(){
        nDialog.setMessage("Loading..");
        nDialog.setTitle("Please Wait...");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        nDialog.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void requestPermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.VIBRATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            openSettings();
                            // permission is denied permenantly, navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }
}
