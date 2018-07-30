package sg.acecom.track.systempest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import sg.acecom.track.systempest.adapter.AocAdapter;
import sg.acecom.track.systempest.adapter.FindingsAdapter;
import sg.acecom.track.systempest.adapter.ImageAdapter;
import sg.acecom.track.systempest.adapter.PestAdapter;
import sg.acecom.track.systempest.adapter.RecommendationsAdapter;
import sg.acecom.track.systempest.forms.PageHeaderFooter;
import sg.acecom.track.systempest.forms.PageHeaderFooterMalaysia;
import sg.acecom.track.systempest.model.AreaConcerned;
import sg.acecom.track.systempest.model.Findings;
import sg.acecom.track.systempest.model.Images;
import sg.acecom.track.systempest.model.Mail;
import sg.acecom.track.systempest.model.Pests;
import sg.acecom.track.systempest.model.Recommendations;
import sg.acecom.track.systempest.util.AppConstant;
import sg.acecom.track.systempest.util.AppController;
import sg.acecom.track.systempest.util.MyPreferences;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

/**
 * Created by jmingl on 23/4/18.
 */

public class MaintenanceDetailActivity extends AppCompatActivity implements View.OnClickListener{

    //MaintenanceJobInformation;
    int maintenancejob_MaintenanceJobID;
    int maintenancejob_MaintenanceID;
    String maintenancejob_AlertDate;
    String maintenancejob_Timestamp;
    String maintenancejob_RxTime;
    String maintenancejob_Flag;
    String maintenancejob_FlagValue;
    String maintenancejob_JobCancelled;
    String maintenancejob_CancelRemarks;
    int maintenancejob_isSent;

    private TextView jobReference;
    private TextView cusName;
    private TextView cusEmail;
    private TextView jobDateTime;
    private TextView jobAreaCovered;
    private TextView jobDestination;
    private TextView jobPest;
    private TextView jobAmount;
    private Button buttonReschedule;
    private EditText jobNewAmount;
    private Button buttonCalender;
    private RecyclerView recyclerTreatment;
    private RecyclerView recyclerAreaConcerned;
    private FloatingActionButton fabAreaConcerned;
    private FloatingActionButton fabCamera;
    private RecyclerView recyclerFindings;
    private RecyclerView recyclerImages;
    private RecyclerView recyclerRecommandations;
    private FloatingActionButton fabFindings;
    private FloatingActionButton fabRecommendations;
    private EditText jobRemarks;
    private RadioGroup radioPayment;
    private RadioButton radioCash;
    private RadioButton radioCheque;
    private Button buttonBack;
    private Button buttonRoute;
    private Button buttonDone;
    Calendar date;

    private ImageAdapter img_adapter;
    private List<Images> imagesList;
    private PestAdapter pestAdapter;
    private List<Pests> pestsList;
    private AocAdapter aocAdapter;
    private List<AreaConcerned> aocFinalList;
    private FindingsAdapter findingsAdapter;
    private List<Findings> findingsList;
    private RecommendationsAdapter recommendationsAdapter;
    private List<Recommendations> recFinalList;
    //Area of Concerned
    final ArrayList<String> singlePestList = new ArrayList();
    final ArrayList<String> aocList = new ArrayList();
    final ArrayList<String> recList = new ArrayList();
    final ArrayList<String> selectedAoc = new ArrayList();
    final ArrayList<String> selectedRec = new ArrayList();

    MyPreferences pref;
    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "SystemPestCamera";

    private Uri fileUri; // file url to store image/video/

    // /creating a PdfWriter variable.
    private PdfWriter pdfWriter;
    private int PERMISSION_REQUEST_WRITE = 0;

    //Signatures
    final String DIRECTORY_CLIENT_SIGNATURE = Environment.getExternalStorageDirectory().getPath() + "/SystemPest-MaintenanceClient/";
    final String DIRECTORY_TECHNICIAN_SIGNATURE = Environment.getExternalStorageDirectory().getPath() + "/SystemPest-MaintenanceTechnician/";

    Bitmap bitmap;
    LinearLayout mContent;
    Button mGetSign;
    Button mClear;
    Button mCancel;
    TextView titleSignature;
    TextView labelTitle;
    File file;
    Dialog dialog;
    View view;
    signature mSignature;

    Bitmap bitmap_tech;
    LinearLayout mContent_tech;
    Button mGetSign_tech;
    Button mClear_tech;
    Button mCancel_tech;
    TextView titleSignature_tech;
    TextView labelTitle_tech;
    File file_tech;
    Dialog dialog_tech;
    View view_tech;
    signature_tech mSignature_tech;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private String scheduledDate, scheduledTime;

    AlertDialog.Builder alert_dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pref = new MyPreferences(this);
        // Dialog Function
        dialog = new Dialog(MaintenanceDetailActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_signature);
        dialog.setCancelable(true);

        // Dialog Function
        dialog_tech = new Dialog(MaintenanceDetailActivity.this);
        dialog_tech.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_tech.setContentView(R.layout.dialog_signature);
        dialog_tech.setCancelable(true);

        jobReference = findViewById( R.id.jobReference );
        cusName = findViewById( R.id.cusName );
        cusEmail = findViewById( R.id.cusEmail );
        jobDateTime = findViewById( R.id.jobDateTime );
        jobAreaCovered = findViewById( R.id.jobAreaCovered );
        jobDestination = findViewById( R.id.jobDestination );
        jobPest = findViewById( R.id.jobPest );
        jobAmount = findViewById( R.id.jobAmount );
        jobNewAmount = findViewById( R.id.jobNewAmount );
        buttonCalender = findViewById( R.id.buttonCalender );
        buttonReschedule = findViewById( R.id.buttonReschedule );
        recyclerTreatment = findViewById( R.id.recyclerTreatment );
        recyclerAreaConcerned = findViewById( R.id.recyclerAreaConcerned );
        recyclerImages = findViewById( R.id.recyclerImages );
        fabAreaConcerned = findViewById( R.id.fabAreaConcerned );
        fabCamera = findViewById( R.id.fabCamera );
        recyclerFindings = findViewById( R.id.recyclerFindings );
        fabFindings = findViewById( R.id.fabFindings );
        recyclerRecommandations = findViewById( R.id.recyclerRecommandations );
        fabRecommendations = findViewById( R.id.fabRecommendations );
        jobRemarks = findViewById( R.id.jobRemarks );
        radioPayment = findViewById( R.id.radioPayment );
        radioCash = findViewById( R.id.radioCash );
        radioCheque = findViewById( R.id.radioCheque );
        buttonBack = findViewById( R.id.buttonBack );
        buttonRoute = findViewById( R.id.buttonRoute );
        buttonDone = findViewById( R.id.buttonDone );

        imagesList = new ArrayList<>();
        img_adapter = new ImageAdapter(this, imagesList);
        recyclerImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerImages.setItemAnimator(new DefaultItemAnimator());
        recyclerImages.setAdapter(img_adapter);

        pestsList = new ArrayList<>();
        pestAdapter = new PestAdapter(this, pestsList);
        recyclerTreatment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerTreatment.setItemAnimator(new DefaultItemAnimator());
        recyclerTreatment.setAdapter(pestAdapter);

        aocFinalList = new ArrayList<>();
        aocAdapter = new AocAdapter(this, aocFinalList);
        recyclerAreaConcerned.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerAreaConcerned.setItemAnimator(new DefaultItemAnimator());
        recyclerAreaConcerned.setAdapter(aocAdapter);

        findingsList = new ArrayList<>();
        findingsAdapter = new FindingsAdapter(this, findingsList);
        recyclerFindings.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerFindings.setItemAnimator(new DefaultItemAnimator());
        recyclerFindings.setAdapter(findingsAdapter);

        recFinalList = new ArrayList<>();
        recommendationsAdapter = new RecommendationsAdapter(this, recFinalList);
        recyclerRecommandations.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerRecommandations.setItemAnimator(new DefaultItemAnimator());
        recyclerRecommandations.setAdapter(recommendationsAdapter);

        buttonCalender.setOnClickListener( this );
        fabAreaConcerned.setOnClickListener( this );
        fabFindings.setOnClickListener( this );
        fabCamera.setOnClickListener( this );
        fabRecommendations.setOnClickListener( this );
        buttonBack.setOnClickListener( this );
        buttonRoute.setOnClickListener( this );
        buttonDone.setOnClickListener( this );
        buttonReschedule.setOnClickListener( this );

        initContent();

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImages();
        loadMaintenanceJob();
        //loadPestList();
        loadPestInformation();
        loadAOC();
        loadRecommendations();
    }

    private void loadMaintenanceJob(){
        final String url = AppConstant.endpoint_url + "maintenancejobinfo/" + pref.getPreferences("maintenance_jobID","");
        Log.e("MaintennceJob URL : ", url);
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try{

                            maintenancejob_MaintenanceJobID = response.getInt("MaintenanceJobID");
                            maintenancejob_MaintenanceID = response.getInt("MaintenanceID");
                            maintenancejob_AlertDate = response.getString("AlertDate");
                            maintenancejob_Timestamp = response.getString("Timestamp");
                            maintenancejob_RxTime = response.getString("RxTime");
                            maintenancejob_Flag = response.getString("Flag");
                            maintenancejob_JobCancelled = response.getString("JobCancelled");
                            maintenancejob_CancelRemarks = response.getString("CancelRemarks");
                            maintenancejob_isSent = response.getInt("isSent");
                            //JSONObject obj = response.getJSONObject(0);

                            //Log.e("Response : ", response.getString(""MaintenanceJobID));
                            //response.getString()


                            // adapter.notifyDataSetChanged();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removePreferences();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){
            case(R.id.buttonCalender):
                selectDatePicker();
                break;

            case(R.id.fabAreaConcerned):
                areaConcernedDialog();
                break;

            case(R.id.fabFindings):
                PestDialog();
                break;

            case(R.id.fabCamera):
                captureImage();
                break;

            case(R.id.fabRecommendations):
                recommendationDialog();
                break;

            case(R.id.buttonBack):
                //onBackPressed();
                backDialog("Alert", "Are you sure you want to exit the job?");

                break;

            case(R.id.buttonReschedule):
                datePickerDialog();
                break;

            case(R.id.buttonRoute):
                intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" + "542275"));
                startActivity(intent);
                break;

            case(R.id.buttonDone):

                completeDialog("Alert", "Are you sure the job has completed?");

                //updateMaintenanceJob();
                break;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectDatePicker() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();

        DatePickerDialog dpDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);

                SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy",Locale.ENGLISH);
                String strDate = format.format(date.getTime());
                SimpleDateFormat formatTime = new SimpleDateFormat("dd-MMM-yyyy 00:00:00",Locale.ENGLISH);
                Log.e("Date seelcted", formatTime.format(date.getTime()));
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));

        dpDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dpDialog.show();
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /*
	 * Here we store the file url as it will be null after returning from camera
	 * app
	 */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view

                showImageDialog(MaintenanceDetailActivity.this, fileUri);

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void showImageDialog(final Activity activity, final Uri imageUrl){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_image);

        final ImageView jobImages = dialog.findViewById(R.id.jobImages);
        Button buttonDelete = (Button) dialog.findViewById(R.id.buttonDelete);
        Button buttonClose = (Button) dialog.findViewById(R.id.buttonClose);
        final Spinner spinnerPest = dialog.findViewById(R.id.spinnerPest);

       /* ArrayAdapter<String> adapter = new ArrayAdapter<String>(JobsDetailActivity.this,
                android.R.layout.simple_spinner_item, singlePestList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPest.setAdapter(adapter);*/

        Glide.with(activity).load(imageUrl).into(jobImages);

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUrl);
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Image image = new Image();
                image.setJobID(pref.getPreferences("JobID",""));
                image.setTitle(spinnerPest.getSelectedItem().toString());
                image.setImage(imageUrl);
                imageList.add(image);*/
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public Bitmap drawTextToBitmap(Context mContext, String photoPath, String mText) {
        try {
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            /*Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);

            android.graphics.Bitmap.Config bitmapConfig =   bitmap.getConfig();
            // set default bitmap config if none
            if(bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }*/

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
            //selected_photo.setImageBitmap(bitmap);

            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            //bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.rgb(110,110, 110));
            // text size in pixels
            paint.setTextSize((int) (12 * scale));
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width())/6;
            int y = (bitmap.getHeight() + bounds.height())/5;

            canvas.drawText(mText, x * scale, y * scale, paint);

            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception



            return null;
        }

    }

    /**
     * ------------ Helper Methods ----------------------
     * */

	/*
	 * Creating file uri to store image/video
	 */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public void loadImages(){
        File img_Path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getPath() + "/" + IMAGE_DIRECTORY_NAME);

        if(img_Path.exists()){
            String[] fileNames = img_Path.list();
            int imageCount = img_Path.listFiles().length;
            imagesList.clear();
            for (int i = 0; i < imageCount; i++) {
                Bitmap mBitmap = BitmapFactory.decodeFile(img_Path.getPath()+"/"+ fileNames[i]);
                Images img = new Images();
                img.setImage_url(img_Path.getPath()+"/"+ fileNames[i]);
                imagesList.add(img);
            }

            img_adapter.notifyDataSetChanged();
        }
    }

    private void loadPestInformation(){
        final String url = AppConstant.endpoint_url + "maintenancejobinfo/" + pref.getPreferences("maintenance_jobID","");
        //Log.e("Pest URL : ", url);
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try{
                            pestsList.clear();
                            singlePestList.clear();
                            JSONArray pestArray = new JSONArray(response.getString("Pest"));
                            for(int i = 0; i < pestArray.length(); i++){
                                JSONObject pestInfo = pestArray.getJSONObject(i);
                                singlePestList.add(pestInfo.getString("PestDesc"));
                                pestsList.add(new Pests(pestInfo.getInt("ItemNo"), pestInfo.getString("PestDesc"), pestInfo.getString("TreatmentDesc")));
                                pestAdapter.notifyDataSetChanged();
                            }
                            //response.getString()


                            // adapter.notifyDataSetChanged();
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

    private void loadAOC(){
        final String url = AppConstant.endpoint_url + "aocinfo";
        //Log.e("Pest URL : ", url);
        // prepare the Request
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        aocList.clear();
                        try{
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject aocObj = response.getJSONObject(i);

                                aocList.add(aocObj.getString("AocDesc"));
                            }

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

    private void loadRecommendations(){
        final String url = AppConstant.endpoint_url + "recommendationsinfo";
        //Log.e("Pest URL : ", url);
        // prepare the Request
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        recList.clear();
                        //recommendationsList.clear();
                        try{
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject recObj = response.getJSONObject(i);
                                recList.add(recObj.getString("Recommendation"));
                                //recommendationsList.add(new Recommendations(recObj.getInt("RecommendationsID"), recObj.getString("Recommendation")));
                            }

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

    private void initContent(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(Calendar.getInstance().getTime());
        //Change Timestamp Timezone
        Date device_date = cal.getTime();
        String timestamp = new SimpleDateFormat("dd, MMM yyyy").format(device_date);

        // JMINGL 24 - Jan - 2017
        SimpleDateFormat sdf = new SimpleDateFormat("dd, MMM yyyy", Locale.ENGLISH);
        Date d = null;
        Date nextJobDate = null;
        try {
            d = sdf.parse(pref.getPreferences("maintenance_NextJobDate",""));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            //calendar.add(Calendar.HOUR, 8);
            nextJobDate = calendar.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(device_date.after(nextJobDate) || device_date.equals(nextJobDate)){
            buttonDone.setVisibility(View.VISIBLE);
        }else{
            buttonDone.setVisibility(View.GONE);
        }

        String destinationAddress = pref.getPreferences("maintenance_Unit","") + ", " +
                pref.getPreferences("maintenance_Destination","") + ", " +
                pref.getPreferences("maintenance_Postal","");

        cusName.setText(pref.getPreferences("maintenance_PIC",""));
        cusEmail.setText(pref.getPreferences("maintenance_Email",""));
        jobDateTime.setText(pref.getPreferences("maintenance_NextJobDate",""));
        jobDestination.setText(destinationAddress);
        jobAmount.setText(pref.getPreferences("maintenance_Amount",""));
        jobPest.setText(pref.getPreferences("maintenance_jobPest",""));
        jobAreaCovered.setText(pref.getPreferences("maintenance_AreaCovered",""));
        jobReference.setText(pref.getPreferences("maintenance_jobReferenceNo",""));

        buttonReschedule.setVisibility(View.GONE);

        loadPestInformation();

    }

    private String convertDate(String date)
    {
        // JMINGL 24 - Jan - 2017
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aaa   dd MMM yyyy",Locale.ENGLISH);
        Date d = null;
        Date eightHourBack = null;
        try {
            d = sdf.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.HOUR, 8);
            eightHourBack = calendar.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date = output.format(eightHourBack);
    }


    private void generatePDF()
    {
        try{
            File directory = new File(Environment.getExternalStorageDirectory().getPath() + "/SystemPest-MaintenanceService");
            //String bpPath = Environment.getExternalStorageDirectory().getPath() + "/Calpeda-Project/" + pref.getPreferences("ProjectID","");

            if(!directory.exists()){
                directory.mkdirs();
            }
        }catch(Exception e){
            Log.e("Create Path : ",String.valueOf(e));
        }

        try {

            String clientPath = DIRECTORY_CLIENT_SIGNATURE + pref.getPreferences("maintenance_jobID","") + ".png";
            String techPath = DIRECTORY_TECHNICIAN_SIGNATURE + pref.getPreferences("maintenance_jobID","") + ".png";

            Calendar cal = Calendar.getInstance();
            cal.setTime(Calendar.getInstance().getTime());
            //Change Timestamp Timezone
            Date eightHourBack = cal.getTime();
            String timestamp = new SimpleDateFormat("ddMMMyyyy").format(eightHourBack);
            String filename = pref.getPreferences("maintenance_jobID","") + "-" + timestamp + ".pdf";

            String mPath = Environment.getExternalStorageDirectory().getPath() + "/SystemPest-MaintenanceService/"  + "/" + filename;
            //getting the full path of the PDF report name
            //String mPath = Environment.getExternalStorageDirectory().toString() + "/One.pdf"; //reportName could be any name

            //constructing the PDF file
            File pdfFile = new File(mPath);

            //Creating a Document with size A4. Document class is available at  com.itextpdf.text.Document
            Document document;

            if(pref.getPreferences("maintenance_CompanyID","").equals("1")){
                document = new Document(PageSize.A4, 36, 36, 130, 150);
                pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                //PageFooter is an inner class of this class which is responsible to create Header and Footer
                PageHeaderFooter event = new PageHeaderFooter(this, "SystemPest Service Order", clientPath, techPath, Integer.parseInt(pref.getPreferences("maintenance_CompanyID","")));
                pdfWriter.setPageEvent(event);
            }else if(pref.getPreferences("maintenance_CompanyID","").equals("2")){
                document = new Document(PageSize.A4, 36, 36, 130, 250);

                pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                //PageFooter is an inner class of this class which is responsible to create Header and Footer
                PageHeaderFooterMalaysia event = new PageHeaderFooterMalaysia(this, "SystemPest Malaysia Service Order", clientPath, techPath);
                pdfWriter.setPageEvent(event);
            }else if(pref.getPreferences("maintenance_CompanyID","").equals("3")){
                document = new Document(PageSize.A4, 36, 36, 130, 150);
                pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                //PageFooter is an inner class of this class which is responsible to create Header and Footer
                PageHeaderFooter event = new PageHeaderFooter(this, "Asia White Ant Service Order", clientPath, techPath, Integer.parseInt(pref.getPreferences("maintenance_CompanyID","")));
                pdfWriter.setPageEvent(event);
            }else{
                document = new Document(PageSize.A4, 36, 36, 130, 150);
                pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                //PageFooter is an inner class of this class which is responsible to create Header and Footer
                PageHeaderFooter event = new PageHeaderFooter(this, "SystemPest Service Order", clientPath, techPath, Integer.parseInt(pref.getPreferences("maintenance_CompanyID","")));
                pdfWriter.setPageEvent(event);
            }


            //Before writing anything to a document it should be opened first
            document.open();

            //Adding meta-data to the document
            addMetaData(document);

            //Adding Title(s) of the document
            //addTitlePage(document);

            //Adding main contents of the document
            addContent(document);

            //Closing the document
            document.close();
            new MaintenanceDetailActivity.uploadServiceForms().execute();
            sendEmail(mPath);

            Log.e("PDF ", "Created...");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PDF ", String.valueOf(e));

        }
    }

    /**
     *  iText allows to add metadata to the PDF which can be viewed in your Adobe Reader. If you right click
     *  on the file and to to properties then you can see all these information.
     * @param document
     */
    private void addMetaData(Document document) {
        document.addTitle("SystemPest Service");
        document.addSubject("");
        document.addKeywords("Service");
        document.addAuthor("");
        document.addCreator("");
    }

    /**
     * This method is used to add empty lines in the document
     * @param paragraph
     * @param number
     */
    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    /**
     * In this method the main contents of the documents are added
     * @param document
     * @throws DocumentException
     */

    private void addContent(Document document) throws DocumentException {

        Paragraph reportBody = new Paragraph();


        createJobHeader(reportBody);
        addEmptyLine(reportBody, 2);

        try{
            for(int i = 0; i < pestsList.size(); i ++){
                createInnerContents(reportBody, i);
                addEmptyLine(reportBody, 1);
            }
        }catch(Exception e){
            Log.e("Error :", String.valueOf(e));
        }

        //document.newPage();
        createRemarks(reportBody);

        addEmptyLine(reportBody, 1);

        createSignatureArea(reportBody);


        // now add all this to the document
        document.add(reportBody);

    }

    /**
     * This method is responsible to add table using iText
     * @param reportBody
     * @throws BadElementException
     */
    private void createJobHeader(Paragraph reportBody)
            throws BadElementException {

        float[] columnWidths = {1,2,2,1,1}; //total 5 columns and their width. The first three columns will take the same width and the fourth one will be 5/2.
        PdfPTable table = new PdfPTable(columnWidths);

        table.setWidthPercentage(100); //set table with 100% (full page)
        table.getDefaultCell().setUseAscender(true);

        //First Line
        PdfPCell cell = new PdfPCell(new Phrase("SERVICE ADDRESS   : ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(pref.getPreferences("Destination",""), FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.BOTTOM);
        cell.setColspan(2);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("REPORT NO   : ", FontFactory.getFont(FontFactory.HELVETICA,4, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        //cell.setFixedHeight(10); //cell height
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(pref.getPreferences("JobNumber",""), FontFactory.getFont(FontFactory.HELVETICA,4, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.BOTTOM);
        //cell.setFixedHeight(10); //cell height
        table.addCell(cell);

        //Second Line
        cell = new PdfPCell(new Phrase("AREA COVERED   : ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Toilet, and So on", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.BOTTOM);
        cell.setColspan(2);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("AGREEMENT NO   : ", FontFactory.getFont(FontFactory.HELVETICA,4, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        //cell.setFixedHeight(10); //cell height
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("000000", FontFactory.getFont(FontFactory.HELVETICA,4, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.BOTTOM);
        //cell.setFixedHeight(10); //cell height
        table.addCell(cell);

        //Third Line
        cell = new PdfPCell(new Phrase("PAYMENT   : ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);


        if(radioCash.isChecked()){
            cell = new PdfPCell(new Phrase("Cash : $" + pref.getPreferences("Amount",""), FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
            //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
            cell.setBorder(Rectangle.BOTTOM);
            cell.setColspan(2);
            //cell.setFixedHeight(30); //cell height
            table.addCell(cell);
        }else if(radioCheque.isChecked()){
            cell = new PdfPCell(new Phrase("Cheque : $" + pref.getPreferences("Amount",""), FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
            //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
            cell.setBorder(Rectangle.BOTTOM);
            cell.setColspan(2);
            //cell.setFixedHeight(30); //cell height
            table.addCell(cell);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(Calendar.getInstance().getTime());
        //Change Timestamp Timezone
        Date eightHourBack = cal.getTime();
        String timestamp = new SimpleDateFormat("dd - MMM - yyyy").format(eightHourBack);

        cell = new PdfPCell(new Phrase("DATE   : ", FontFactory.getFont(FontFactory.HELVETICA,4, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        //cell.setFixedHeight(10); //cell height
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(timestamp, FontFactory.getFont(FontFactory.HELVETICA,4, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.BOTTOM);
        //cell.setFixedHeight(10); //cell height
        table.addCell(cell);

       /* //Second Line
        cell = new PdfPCell(new Phrase("TYPE OF PESTS   : ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(pestsList.get(i).getPestDesc(), FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(4);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        //Second Line
        cell = new PdfPCell(new Phrase("AREA CONCERNED   : ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        StringBuilder areaConcernedBuilder = new StringBuilder();
        for(int j = 0; j < selectedAoc.size(); j++){
            if(selectedAoc.size() == 1 || i + 1 == selectedAoc.size()){
                areaConcernedBuilder.append(selectedAoc.get(i).toString());
            }else{
                areaConcernedBuilder.append(selectedAoc.get(i).toString());
                areaConcernedBuilder.append(", ");
            }
        }

        cell = new PdfPCell(new Phrase(areaConcernedBuilder.toString(), FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(4);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        //Second Line
        cell = new PdfPCell(new Phrase("TREATMENTS   : ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(pestsList.get(i).getTreatmentDesc(), FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(4);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        //Second Line
        cell = new PdfPCell(new Phrase("FINDING   : ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        StringBuilder findingsBuilder = new StringBuilder();
        for(int k = 0; k < findingsList.size(); k ++){
            if(findingsList.get(k).getPestDesc().equals(pestsList.get(i).getPestDesc())){
                if(findingsList.size() == 1 || i + 1 == findingsList.size()){
                    findingsBuilder.append(findingsList.get(i).getFindings().toString());
                }else{
                    findingsBuilder.append(findingsList.get(i).getFindings().toString());
                    findingsBuilder.append(", ");
                }
            }

        }

        cell = new PdfPCell(new Phrase(findingsBuilder.toString(), FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        //cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(4);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        //Second Line
        cell = new PdfPCell(new Phrase("OTHERS   : ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Bat is BIG and There is a man", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(4);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("  ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(5);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        //Second Line
        cell = new PdfPCell(new Phrase("IMAGES   : ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(4);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);*/

        table.setKeepTogether(true);
        reportBody.add(table);
    }

    /**
     * This method is responsible to add table using iText
     * @param reportBody
     * @throws BadElementException
     */
    private void createInnerContents(Paragraph reportBody, int i)
            throws BadElementException {

        float[] columnWidths = {1,2,2,1,1}; //total 5 columns and their width. The first three columns will take the same width and the fourth one will be 5/2.
        PdfPTable table = new PdfPTable(columnWidths);

        table.setWidthPercentage(100); //set table with 100% (full page)
        table.getDefaultCell().setUseAscender(true);

        //Second Line
        PdfPCell cell = new PdfPCell(new Phrase("TYPE OF PESTS   : ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(pestsList.get(i).getPestDesc(), FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(4);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        //Second Line
        cell = new PdfPCell(new Phrase("AREA CONCERNED   : ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        StringBuilder areaConcernedBuilder = new StringBuilder();
        for(int j = 0; j < selectedAoc.size(); j++){
            if(selectedAoc.size() == 1 || i + 1 == selectedAoc.size()){
                areaConcernedBuilder.append(selectedAoc.get(j).toString());
            }else{
                areaConcernedBuilder.append(selectedAoc.get(j).toString());
                areaConcernedBuilder.append(", ");
            }
        }

        cell = new PdfPCell(new Phrase(areaConcernedBuilder.toString(), FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(4);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        //Second Line
        cell = new PdfPCell(new Phrase("TREATMENTS   : ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(pestsList.get(i).getTreatmentDesc(), FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(4);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        //Second Line
        cell = new PdfPCell(new Phrase("FINDING   : ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        StringBuilder findingsBuilder = new StringBuilder();
        StringBuilder othersBuilder = new StringBuilder();
        for(int k = 0; k < findingsList.size(); k++){
            if(findingsList.get(k).getPestDesc().equals(pestsList.get(i).getPestDesc())){
                if(!findingsList.get(k).getAreaConcerned().equals("Others")){
                    findingsBuilder.append(findingsList.get(k).getFindings().toString());
                    //findingsBuilder.append("\n");
                    findingsBuilder.append(",  ");

                }else{
                    othersBuilder.append(findingsList.get(k).getFindings().toString());
                    //othersBuilder.append("\n");
                    findingsBuilder.append(",  ");
                }

            }

        }

        cell = new PdfPCell(new Phrase(findingsBuilder.toString(), FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        //cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(4);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        //Second Line
        cell = new PdfPCell(new Phrase("OTHERS   : ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(othersBuilder.toString(), FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(4);
        cell.setPaddingTop(5);
        table.addCell(cell);

        /*//Second Line
        cell = new PdfPCell(new Phrase("IMAGES   : ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(4);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);*/

        table.setKeepTogether(true);
        reportBody.add(table);
    }

    /**
     * This method is responsible to add table using iText
     * @param reportBody
     * @throws BadElementException
     */
    private void createRemarks(Paragraph reportBody)
            throws BadElementException {

        File img_Path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getPath() + "/" + IMAGE_DIRECTORY_NAME);

        float[] columnWidths = {1,2,2,1,1}; //total 4 columns and their width. The first three columns will take the same width and the fourth one will be 5/2.
        PdfPTable table = new PdfPTable(columnWidths);

        table.setWidthPercentage(100); //set table with 100% (full page)
        table.getDefaultCell().setUseAscender(true);

        //Second Line
        PdfPCell cell = new PdfPCell(new Phrase("IMAGES   : ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);


        cell = new PdfPCell();
         //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        if(img_Path.exists()){
            String[] fileNames = img_Path.list();
            int imageCount = img_Path.listFiles().length;
            try {
                for (int i = 0; i < imageCount; i++) {
                    Log.e("Image : ", img_Path.getPath()+"/"+ fileNames[i]);
                    Image p_img = Image.getInstance(img_Path.getPath()+"/"+ fileNames[i]);
                    p_img.setWidthPercentage(30);
                    cell.addElement(p_img);
                    //img_Path.getPath()+"/"+ fileNames[i]
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Error PDF : ", String.valueOf(e));
            }
        }
        //cell = new PdfPCell();
        //cell.addElement(p);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(4);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        //Second Line
        cell = new PdfPCell(new Phrase("  ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(5);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        //Second Line
        cell = new PdfPCell(new Phrase("REMARKS : ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(jobRemarks.getText().toString(), FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(4);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        //Second Line
        cell = new PdfPCell(new Phrase("RECOMMENDATIONS : ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        StringBuilder recBuilder = new StringBuilder();
        for(int k = 0; k < recFinalList.size(); k++){
            recBuilder.append(recFinalList.get(k).getRecommendationDesc().toString());
            recBuilder.append("\n");
        }

        cell = new PdfPCell(new Phrase(recBuilder.toString(), FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(4);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);


        //table.setKeepTogether(true);
        //table.isExtendLastRow(true);
        reportBody.add(table);
    }

    /**
     * This method is responsible to add table using iText
     * @param reportBody
     * @throws BadElementException
     */
    private void createSignatureArea(Paragraph reportBody)
            throws BadElementException {

        String clientPath = DIRECTORY_CLIENT_SIGNATURE + pref.getPreferences("maintenance_jobID","") + ".png";
        String techPath = DIRECTORY_TECHNICIAN_SIGNATURE + pref.getPreferences("maintenance_jobID","") + ".png";

        float[] columnWidths = {2,2,1,2,2}; //total 4 columns and their width. The first three columns will take the same width and the fourth one will be 5/2.
        PdfPTable table = new PdfPTable(columnWidths);

        table.setWidthPercentage(100); //set table with 100% (full page)
        table.getDefaultCell().setUseAscender(true);

        //Second Line

        PdfPCell cell = new PdfPCell(); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        try{
            Image p_img = Image.getInstance(clientPath);
            p_img.setWidthPercentage(40);
            p_img.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(p_img);
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
            cell.setBorder(Rectangle.BOTTOM);
            cell.setPaddingTop(5);
            //cell.setFixedHeight(30); //cell height
            table.addCell(cell);
        }catch(IOException e){

        }


        //Second Line
        cell = new PdfPCell(new Phrase("  ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(1);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);


        cell = new PdfPCell(); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        try{
            Image p_img = Image.getInstance(techPath);
            p_img.setWidthPercentage(40);
            p_img.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(p_img);
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
            cell.setBorder(Rectangle.BOTTOM);
            cell.setPaddingTop(5);
            //cell.setFixedHeight(30); //cell height
            table.addCell(cell);
        }catch(IOException e){

        }

        cell = new PdfPCell(new Phrase("CLIENT NAME / SIGNATURE", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setColspan(2);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        //Second Line
        cell = new PdfPCell(new Phrase("  ", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(1);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("TECHNICIAN NAME / SIGNATURE", FontFactory.getFont(FontFactory.HELVETICA,6, Font.NORMAL))); //Public static Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER); //alignment
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setColspan(2);
        cell.setPaddingTop(5);
        //cell.setFixedHeight(30); //cell height
        table.addCell(cell);

        //table.setKeepTogether(true);
        //table.isExtendLastRow(true);
        reportBody.add(table);
    }

    private void areaConcernedDialog(){

        final CharSequence[] dialogList = aocList.toArray(new CharSequence[aocList.size()]);
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(this);
        builderDialog.setTitle("AREA OF CONCERNED");
        final int count = dialogList.length;
        final boolean[] is_checked = new boolean[count];

        aocFinalList.clear();

        for(int i = 0; i < count; i++){
            for(int j = 0; j < selectedAoc.size(); j++){
                if(selectedAoc.get(j).equals(dialogList[i].toString())){
                    is_checked[i] = true;
                }
            }
        }

        // Creating multiple selection by using setMutliChoiceItem method
        builderDialog.setMultiChoiceItems(dialogList, is_checked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton, boolean isChecked) {
                        /*if (isChecked) {
                            selectedAoc.add(aocList.get(whichButton));
                        } else if (selectedAoc.contains(whichButton)) {
                            selectedAoc.remove(Integer.valueOf(whichButton));
                        }*/

                        // Notify the current action
                        //Toast.makeText(getApplicationContext(), aocList.get(whichButton) + " " + isChecked, Toast.LENGTH_SHORT).show();
                    }
                });

        builderDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        selectedAoc.clear();
                        for(int i = 0; i < count; i++){
                            if(is_checked[i]){
                                selectedAoc.add(dialogList[i].toString());
                            }
                        }

                        for(int j = 0; j < selectedAoc.size(); j++){
                            AreaConcerned aoc = new AreaConcerned();
                            aoc.setAocDesc(selectedAoc.get(j));
                            aocFinalList.add(aoc);
                        }

                        aocAdapter.notifyDataSetChanged();

                        //PestDialog();
                        //ListView has boolean array like {1=true, 3=true}, that shows checked items
                    }
                });

        builderDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        aocAdapter.notifyDataSetChanged();
                        //((TextView) JobsDetailActivity.this.findViewById(R.id.text)).setText("Click here to open Dialog");
                    }
                });
        AlertDialog alert = builderDialog.create();
        alert.show();
    }

    private void recommendationDialog(){

        final CharSequence[] dialogList = recList.toArray(new CharSequence[recList.size()]);
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(this);
        builderDialog.setTitle("RECOMMENDATIONS");
        final int count = dialogList.length;
        final boolean[] is_checked = new boolean[count];

        recFinalList.clear();

        for(int i = 0; i < count; i++){
            for(int j = 0; j < selectedRec.size(); j++){
                if(selectedRec.get(j).equals(dialogList[i].toString())){
                    is_checked[i] = true;
                }
            }
        }

        // Creating multiple selection by using setMutliChoiceItem method
        builderDialog.setMultiChoiceItems(dialogList, is_checked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton, boolean isChecked) {
                        /*if (isChecked) {
                            selectedAoc.add(aocList.get(whichButton));
                        } else if (selectedAoc.contains(whichButton)) {
                            selectedAoc.remove(Integer.valueOf(whichButton));
                        }*/

                        // Notify the current action
                        //Toast.makeText(getApplicationContext(), aocList.get(whichButton) + " " + isChecked, Toast.LENGTH_SHORT).show();
                    }
                });

        builderDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        selectedRec.clear();
                        for(int i = 0; i < count; i++){
                            if(is_checked[i]){
                                selectedRec.add(dialogList[i].toString());
                            }
                        }

                        for(int j = 0; j < selectedRec.size(); j++){
                            Recommendations rec = new Recommendations();
                            rec.setRecommendationDesc(selectedRec.get(j));
                            recFinalList.add(rec);
                        }

                        recommendationsAdapter.notifyDataSetChanged();

                        //PestDialog();
                        //ListView has boolean array like {1=true, 3=true}, that shows checked items
                    }
                });

        builderDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        aocAdapter.notifyDataSetChanged();
                        //((TextView) JobsDetailActivity.this.findViewById(R.id.text)).setText("Click here to open Dialog");
                    }
                });
        AlertDialog alert = builderDialog.create();
        alert.show();
    }

    private void PestDialog(){

        final List<String> newPestList = new ArrayList<>(singlePestList);
        //newPestList.add("Others");
        final CharSequence[] dialogList = newPestList.toArray(new CharSequence[newPestList.size()]);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MaintenanceDetailActivity.this);
        mBuilder.setTitle("Choose Pest");
        mBuilder.setSingleChoiceItems(dialogList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Toast.makeText(JobsDetailActivity.this, "Found : " + singlePestList.get(i), Toast.LENGTH_SHORT).show();
                /*if(i + 1 == newPestList.size()){
                    //findingsList.add(new Findings(newPestList.get(i), ""));
                    //findingsList.add(new Findings(newPestList.get(i)));
                    //findingsList.add(new Findings("None", "", newPestList.get(i)));
                    //findingsAdapter.notifyDataSetChanged();
                    othersDialog(newPestList.get(i));
                }else{
                    FindingDialog(newPestList.get(i));
                }*/
                FindingDialog(newPestList.get(i));
                dialogInterface.dismiss();

            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void FindingDialog(final String pest){
        final List<String> newFindingList = new ArrayList<>(selectedAoc);
        newFindingList.add("Others");
        newFindingList.add("NO SIGN OF ACTIVE PESTS ACTIVITY");
        final CharSequence[] dialogList = newFindingList.toArray(new CharSequence[newFindingList.size()]);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MaintenanceDetailActivity.this);
        mBuilder.setTitle("Choose Area Found");
        mBuilder.setSingleChoiceItems(dialogList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //findingsList.add(new Findings(pest, selectedAoc.get(i)));
                //findingsList.add(new Findings(pest + " FOUND AT " + selectedAoc.get(i)));
                if(i + 1 == newFindingList.size()){
                    findingsList.add(new Findings(pest, newFindingList.get(i), newFindingList.get(i)));
                    findingsAdapter.notifyDataSetChanged();
                }else if(i + 2 == newFindingList.size()){
                    othersDialog(pest);
                }else{
                    findingsList.add(new Findings(pest, newFindingList.get(i), pest + " FOUND AT " + newFindingList.get(i)));
                    findingsAdapter.notifyDataSetChanged();
                }


                //Toast.makeText(JobsDetailActivity.this, "In " + selectedAoc.get(i) + " ", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void othersDialog(final String pest){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Others");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissKeyboard(MaintenanceDetailActivity.this);

                //findingsList.add(new Findings(pest ,input.getText().toString()));
                //findingsList.add(new Findings(input.getText().toString()));
                findingsList.add(new Findings(pest,"Others",pest + " : " + input.getText().toString()));
                findingsAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissKeyboard(MaintenanceDetailActivity.this);
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }

    /*private void updateJob(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(Calendar.getInstance().getTime());
        cal.add(Calendar.HOUR, -8);
        Date eightHourBack = cal.getTime();
        String timestamp = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss", Locale.ENGLISH).format(eightHourBack);

        JSONObject params = new JSONObject();
        try {
            params.put("JobID", pref.getPreferences("JobID",""));
            params.put("JobNumber", pref.getPreferences("JobNumber",""));
            params.put("Company", pref.getPreferences("Company",""));
            params.put("AssetID", pref.getPreferences("driver_assets_id",""));
            params.put("AssetCompanyID", pref.getPreferences("AssetCompanyID",""));
            params.put("AssetResellerID", pref.getPreferences("AssetResellerID",""));
            params.put("Timestamp", pref.getPreferences("Timestamp",""));
            params.put("RxTime", timestamp);
            params.put("Amount", pref.getPreferences("Amount",""));
            params.put("PIC", pref.getPreferences("PIC",""));
            params.put("Destination", pref.getPreferences("Destination",""));
            params.put("Phone", pref.getPreferences("Phone",""));
            params.put("Unit", pref.getPreferences("Unit",""));
            params.put("Flag", 0);
            params.put("Remarks", pref.getPreferences("Remarks",""));
            params.put("Receipt", pref.getPreferences("Receipt",""));
            params.put("UserID", pref.getPreferences("UserID",""));
            params.put("DriverID", pref.getPreferences("driver_id",""));
            params.put("Postal", pref.getPreferences("Postal",""));
            params.put("JobAccepted", pref.getPreferences("JobAccepted",""));
            params.put("JobCompleted", timestamp);
            params.put("CusEmail", pref.getPreferences("CusEmail",""));
            params.put("Site", pref.getPreferences("Site",""));

            StringBuilder recommendationBuilder = new StringBuilder();

            for(int j = 0; j < recFinalList.size(); j++){
                if(recFinalList.size() == 1 || j + 1 == recFinalList.size()){
                    recommendationBuilder.append(recFinalList.get(j).getRecommendationDesc());
                }else{
                    recommendationBuilder.append(recFinalList.get(j).getRecommendationDesc());
                    recommendationBuilder.append(", ");
                }
            }

            params.put("Recommendations", recommendationBuilder.toString());
            params.put("ReceivedAmount", jobNewAmount.getText().toString());
            if(radioCash.isChecked()){
                params.put("PaymentType", 1);
            }else if(radioCheque.isChecked()){
                params.put("PaymentType", 2);
            }


        } catch (JSONException e) {
            e.printStackTrace();

        }

        final String url = AppConstant.endpoint_url + "jobinfo?ID=" + pref.getPreferences("JobID","");

        System.out.println(url);

        AQuery aq = new AQuery(this);
        aq.put(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String response, AjaxStatus status) {
                System.out.println(response);
                if (status.getMessage().equals("OK")) {
                    postFindings();
                }
            }
        }.header("Content-Type", "application/x-www-form-urlencoded")
                .header("_method", "PUT"));
    }*/

    private void postFindings(){
        int j = 1;
        for(int i = 0; i < findingsList.size(); i++){
            JSONObject params = new JSONObject();
            try {
                params.put("MaintenanceJobID", pref.getPreferences("maintenance_jobID",""));
                params.put("PestDesc", findingsList.get(i).getPestDesc());
                params.put("AocDesc", findingsList.get(i).getAreaConcerned());
                params.put("Findings", findingsList.get(i).getFindings());
                params.put("ItemNo", j);

            } catch (JSONException e) {
                e.printStackTrace();

            }

            final String url = AppConstant.endpoint_url + "findingsmaintenanceinfo";

            System.out.println(url);

            AQuery aq = new AQuery(this);
            aq.post(url, params, String.class, new AjaxCallback<String>() {
                @Override
                public void callback(String url, String response, AjaxStatus status) {
                    System.out.println(response);
                    if (status.getMessage().equals("OK")) {

                    }
                }
            }.header("Content-Type", "application/x-www-form-urlencoded")
                    .header("_method", "POST"));

            j++;

            if(i + 1 == findingsList.size()){

                for(int k = 0; k < imagesList.size(); k++){
                    File file = new File(imagesList.get(k).getImage_url());
                    file.delete();

                    if(k+1 == imagesList.size()){
                        removePreferences();
                       /* Intent intent = new Intent(this, MaintenanceActivity.class);
                        startActivity(intent);
                        finish();*/

                       onBackPressed();
                    }
                }
            }
        }
    }

    private void removePreferences(){
        //pref.removePreferences("JobID");
        pref.removePreferences("maintenance_JobNumber");
        pref.removePreferences("maintenance_Flag");
        pref.removePreferences("maintenance_FlagValue");
        pref.removePreferences("maintenance_AssetCompanyID");
        pref.removePreferences("maintenance_AssetCompany");
        pref.removePreferences("maintenance_AssetResellerID");
        pref.removePreferences("maintenance_AssetReseller");
        pref.removePreferences("maintenance_UserID");
        pref.removePreferences("maintenance_UserName");
        pref.removePreferences("maintenance_AssetID");
        pref.removePreferences("maintenance_DriverID");
        pref.removePreferences("maintenance_DriverName");
        pref.removePreferences("maintenance_Timestamp");
        pref.removePreferences("maintenance_RxTime");
        pref.removePreferences("maintenance_Company");
        pref.removePreferences("maintenance_Destination");
        pref.removePreferences("maintenance_Postal");
        pref.removePreferences("maintenance_Unit");
        pref.removePreferences("maintenance_PIC");
        pref.removePreferences("maintenance_Phone");
        pref.removePreferences("maintenance_Amount");
        pref.removePreferences("maintenance_CusEmail");
        pref.removePreferences("maintenance_Site");
        pref.removePreferences("maintenance_Pest");
        pref.removePreferences("maintenance_Remarks");
        pref.removePreferences("maintenance_JobAccepted");
        pref.removePreferences("maintenance_JobCompleted");
        pref.removePreferences("maintenance_Receipt");
        pref.removePreferences("maintenance_Image");
        pref.removePreferences("maintenance_ImageFill");
        pref.removePreferences("maintenance_jobPest");
        pref.removePreferences("maintenance_AreaCovered");
    }

    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v, String StoredPath) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);

                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    // Function for Digital Signature
    public void dialogSignature(String signature_title, final String Signature_StoredPath, String DIRECTORY) {

        // Method to create Directory, if the Directory doesn't exists
        file = new File(DIRECTORY);
        if (!file.exists()) {
            file.mkdir();
        }

        mContent = (LinearLayout) dialog.findViewById(R.id.linearLayoutSign);
        mSignature = new signature(getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (Button) dialog.findViewById(R.id.clear);
        mGetSign = (Button) dialog.findViewById(R.id.getsign);
        titleSignature = (TextView) dialog.findViewById(R.id.titleSignature);
        labelTitle = (TextView) dialog.findViewById(R.id.labelTitle);
        mGetSign.setEnabled(false);
        mCancel = (Button) dialog.findViewById(R.id.cancel);
        view = mContent;

        titleSignature.setText(signature_title);
        labelTitle.setText(signature_title);

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });

        mGetSign.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                view.setDrawingCacheEnabled(true);
                mSignature.save(view, Signature_StoredPath);
                dialog.dismiss();
                Calendar cal = Calendar.getInstance();
                cal.setTime(Calendar.getInstance().getTime());
                Date eightHourBack = cal.getTime();
                String date = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(eightHourBack);
                String time = new SimpleDateFormat("HHmmss", Locale.ENGLISH).format(eightHourBack);
                String signature_name = "SIGNATURE_"+ pref.getPreferences("maintenance_jobID","") + "_" + date + "_" + time;
                final String Signature_Technician_StoredPath = DIRECTORY_TECHNICIAN_SIGNATURE + pref.getPreferences("maintenance_jobID","") + ".png";

                dialogSecondSignature("Technician Signature", Signature_Technician_StoredPath, DIRECTORY_TECHNICIAN_SIGNATURE);

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                // Calling the same class
                recreate();
            }
        });
        dialog.show();
    }

    public class signature_tech extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature_tech(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v, String StoredPath) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap_tech == null) {
                bitmap_tech = Bitmap.createBitmap(mContent_tech.getWidth(), mContent_tech.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap_tech);
            try {
                // Output the file
                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);

                // Convert the output file to Image such as .png
                bitmap_tech.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign_tech.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    // Function for Digital Signature
    public void dialogSecondSignature(String signature_title, final String Signature_StoredPath, String DIRECTORY) {

        // Method to create Directory, if the Directory doesn't exists
        file_tech = new File(DIRECTORY);
        if (!file_tech.exists()) {
            file_tech.mkdir();
        }

        mContent_tech = (LinearLayout) dialog_tech.findViewById(R.id.linearLayoutSign);
        mSignature_tech = new signature_tech(getApplicationContext(), null);
        mSignature_tech.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent_tech.addView(mSignature_tech, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear_tech = (Button) dialog_tech.findViewById(R.id.clear);
        mGetSign_tech = (Button) dialog_tech.findViewById(R.id.getsign);
        titleSignature_tech = (TextView) dialog_tech.findViewById(R.id.titleSignature);
        labelTitle_tech = (TextView) dialog_tech.findViewById(R.id.labelTitle);
        mGetSign_tech.setEnabled(false);
        mCancel_tech = (Button) dialog_tech.findViewById(R.id.cancel);
        view_tech = mContent_tech;

        titleSignature_tech.setText(signature_title);
        labelTitle_tech.setText(signature_title);

        mClear_tech.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSignature_tech.clear();
                mGetSign_tech.setEnabled(false);
            }
        });

        mGetSign_tech.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                view_tech.setDrawingCacheEnabled(true);
                mSignature_tech.save(view_tech, Signature_StoredPath);
                dialog_tech.dismiss();
                new MaintenanceDetailActivity.uploadClientSignatures().execute();
                new MaintenanceDetailActivity.uploadTechnicianSignatures().execute();
                //Log.e("TEST", "test");
                generatePDF();
                updateMaintenanceJob();

            }
        });

        mCancel_tech.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog_tech.dismiss();
                // Calling the same class
                recreate();
            }
        });
        dialog_tech.show();
    }

    public class uploadClientSignatures extends AsyncTask<String, Void, Long> {

        protected Long doInBackground(String... FULL_PATH_TO_LOCAL_FILE) {
            {
                FTPClient ftpClient = new FTPClient();
                int reply;

                String servername = AppConstant.ads_host;
                int port = 21;
                String user = "SystemPest_ClientSignatures";
                String pass = "trackacecom";
                try {
                    Log.e("START :", "UPLOADING");
                    //System.out.println("Entered Data Upload loop!");
                    ftpClient.connect(servername, 21);
                    ftpClient.login(user, pass);
                    ftpClient.sendCommand("Enter FTP Server");
                    ftpClient.changeWorkingDirectory("/");

                    reply = ftpClient.getReplyCode();

                    if (FTPReply.isPositiveCompletion(reply)) {
                        System.out.println("Connected Success");
                    } else {
                        System.out.println("Connection Failed");
                        ftpClient.disconnect();
                    }

                    try
                    {
                        Log.e("PROCEEDING :", "UPLOADING");

                        ftpClient.enterLocalPassiveMode(); // important!
                        ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
                        String path = Environment.getExternalStorageDirectory().getPath() + "/SystemPest-MaintenanceClient/" + pref.getPreferences("maintenance_jobID","") + ".png";

                        FileInputStream in = new FileInputStream(new File(path));
                        boolean result = ftpClient.storeFile("/"+ pref.getPreferences("maintenance_jobID","") +".png", in);
                        in.close();
                        if (result) Log.v("upload result", "succeeded");
                        ftpClient.logout();
                        ftpClient.disconnect();
                        //File file = new File(path);
                        //boolean deleted = file.delete();
                    }
                    catch (Exception e)
                    {
                        Log.e("Upload Failed : ", String.valueOf(e));
                        e.printStackTrace();
                    }

                } catch (SocketException e) {
                    Log.e("FTP", e.getStackTrace().toString());
                    //System.out.println("Socket Exception!");
                } catch (UnknownHostException e) {
                    Log.e("FTP", e.getStackTrace().toString());
                } catch (IOException e) {
                    Log.e("FTP", e.getStackTrace().toString());
                    //System.out.println("IO Exception!");
                }
                return null;
            }
        }
    }

    public class uploadTechnicianSignatures extends AsyncTask<String, Void, Long> {

        protected Long doInBackground(String... FULL_PATH_TO_LOCAL_FILE) {
            {
                FTPClient ftpClient = new FTPClient();
                int reply;

                String servername = AppConstant.ads_host;
                int port = 21;
                String user = "SystemPest_TechnicianSignatures";
                String pass = "trackacecom";
                try {
                    Log.e("START :", "UPLOADING");
                    //System.out.println("Entered Data Upload loop!");
                    ftpClient.connect(servername, 21);
                    ftpClient.login(user, pass);
                    ftpClient.sendCommand("Enter FTP Server");
                    ftpClient.changeWorkingDirectory("/");

                    reply = ftpClient.getReplyCode();

                    if (FTPReply.isPositiveCompletion(reply)) {
                        System.out.println("Connected Success");
                    } else {
                        System.out.println("Connection Failed");
                        ftpClient.disconnect();
                    }

                    try
                    {
                        Log.e("PROCEEDING :", "UPLOADING");

                        ftpClient.enterLocalPassiveMode(); // important!
                        ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
                        String path = Environment.getExternalStorageDirectory().getPath() + "/SystemPest-MaintenanceTechnician/" + pref.getPreferences("maintenance_jobID","") + ".png";

                        FileInputStream in = new FileInputStream(new File(path));
                        boolean result = ftpClient.storeFile("/"+ pref.getPreferences("maintenance_jobID","") +".png", in);
                        in.close();
                        if (result) Log.v("upload result", "succeeded");
                        ftpClient.logout();
                        ftpClient.disconnect();
                        //File file = new File(path);
                        //boolean deleted = file.delete();
                    }
                    catch (Exception e)
                    {
                        Log.e("Upload Failed : ", String.valueOf(e));
                        e.printStackTrace();
                    }

                } catch (SocketException e) {
                    Log.e("FTP", e.getStackTrace().toString());
                    //System.out.println("Socket Exception!");
                } catch (UnknownHostException e) {
                    Log.e("FTP", e.getStackTrace().toString());
                } catch (IOException e) {
                    Log.e("FTP", e.getStackTrace().toString());
                    //System.out.println("IO Exception!");
                }
                return null;
            }
        }
    }

    private void imagePestDialog(){

        final List<String> newPestList = new ArrayList<>(singlePestList);
        final CharSequence[] dialogList = newPestList.toArray(new CharSequence[newPestList.size()]);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MaintenanceDetailActivity.this);
        mBuilder.setTitle("Choose Pest");
        mBuilder.setSingleChoiceItems(dialogList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void updateMaintenanceJob(){
        JSONObject params = new JSONObject();

        try {
            params.put("MaintenanceJobID", maintenancejob_MaintenanceJobID);
            params.put("MaintenanceID", maintenancejob_MaintenanceID);
            params.put("AlertDate", maintenancejob_AlertDate);
            params.put("Timestamp", maintenancejob_Timestamp);
            params.put("RxTime", maintenancejob_RxTime);
            params.put("Flag", 0);
            params.put("JobCancelled", maintenancejob_JobCancelled);
            params.put("CancelRemarks", maintenancejob_CancelRemarks);
            params.put("isSent", maintenancejob_isSent);

        } catch (JSONException e) {
            e.printStackTrace();

        }

        final String url = AppConstant.endpoint_url + "maintenancejobinfo?ID=" + pref.getPreferences("maintenance_jobID","");

        System.out.println(url);

        AQuery aq = new AQuery(this);
        aq.put(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String response, AjaxStatus status) {
                System.out.println(response);
                if (status.getMessage().equals("OK")) {
                    postFindings();
                }
            }
        }.header("Content-Type", "application/x-www-form-urlencoded")
                .header("_method", "PUT"));
    }

    private void loadPestList(){
        final String url = AppConstant.endpoint_url + "pestinfo";
        Log.e("MaintennceJob URL : ", url);
        // prepare the Request
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        try{

                            for(int i = 0; i < response.length(); i++){
                                JSONObject obj = response.getJSONObject(i);
                                singlePestList.add(obj.getString("PestDesc"));
                            }
                            //JSONObject obj = response.getJSONObject(0);

                            //Log.e("Response : ", response.getString(""MaintenanceJobID));
                            //response.getString()


                            // adapter.notifyDataSetChanged();
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

    private void sendEmail(String attachment) {
        String sender_address = "";
        String sender_password = "";
        String[] recipients = { "ljmmagi@gmail.com" };
        SendMaintenanceEmailAsyncTask email = new SendMaintenanceEmailAsyncTask();
        email.activity = this;

        StringBuilder emailBody = new StringBuilder();

        emailBody.append("Dear Customer, ");
        emailBody.append("\n");
        emailBody.append("\n");
        emailBody.append("Your job has been completed by SystemPest Engineer, Kindly view the attachment file for the reports. ");
        emailBody.append("\n");
        emailBody.append("Please do not reply this email. ");

        if(pref.getPreferences("maintenance_CompanyID","").equals("1")){
            sender_address = "system.sg.report@gmail.com";
            sender_password = "qwepoi78";
        }else if(pref.getPreferences("maintenance_CompanyID","").equals("2")){
            sender_address = "system.my.report@gmail.com";
            sender_password = "qwepoi78";
        }else if(pref.getPreferences("maintenance_CompanyID","").equals("3")){
            sender_address = "asiawhiteant.report@gmail.com";
            sender_password = "qwepoi78";
        }else{
            sender_address = "system.sg.report@gmail.com";
            sender_password = "qwepoi78";
        }
        email.m = new Mail(sender_address, sender_password);
        email.m.set_from(sender_address);
        email.m.setBody(emailBody.toString());
        email.m.set_to(recipients);
        email.m.set_subject("Do not reply this message");
        try {
            email.m.addAttachment(attachment);
        } catch (Exception e) {
            Log.e("Attachment Failed : ", String.valueOf(e));
            e.printStackTrace();
        }
        /*try {
            email.m.addAttachment(FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        email.execute();
    }

    private void datePickerDialog(){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        scheduledDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        Log.e("Scheduled Date : ", scheduledDate);
                        timePickerDialog();

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void timePickerDialog(){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        scheduledTime = hourOfDay + ":" + minute;
                        Log.e("Scheduled Time : ", scheduledTime);


                        Log.e("Scheduled Date Time : ", scheduledDate + " " + scheduledTime);
                        rescheduledDialog(scheduledDate + " " + scheduledTime);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void rescheduledDialog(final String rescheduledDateTime){
        alert_dialog = new AlertDialog.Builder(this);
        alert_dialog.setCancelable(true);
        alert_dialog.setTitle("Reschedule Date");
        alert_dialog.setMessage("Date Selected : " + rescheduledDateTime);
        alert_dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert_dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                createNewScheduledJob(rescheduledDateTime);
                dialog.dismiss();
            }
        });
        alert_dialog.show();

    }

    private void createNewScheduledJob(String scheduledDate){

        //JN-270718-763
        Calendar cal = Calendar.getInstance();
        cal.setTime(Calendar.getInstance().getTime());
        cal.add(Calendar.HOUR, -8);
        Date eightHourBack = cal.getTime();
        String dateRange = new SimpleDateFormat("ddmmyy", Locale.ENGLISH).format(eightHourBack);

        Random random = new Random();
        int x = random.nextInt(900) + 100;
        String randomNumber = String.valueOf(x);
        if(randomNumber.length() == 1){
            randomNumber = "00" + randomNumber;
        }else if(randomNumber.length() == 2){
            randomNumber = "0" + randomNumber;
        }

        String jobnumber = "JN-" + dateRange + "-" + randomNumber;

        SimpleDateFormat sdf = new SimpleDateFormat("d-m-yyyy", Locale.ENGLISH);
        SimpleDateFormat output = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss",Locale.ENGLISH);
        Date d = null;
        Date utcDate = null;
        String finalDate = "";
        try {
            d = sdf.parse(scheduledDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.HOUR, 8);
            utcDate = calendar.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        finalDate = output.format(eightHourBack);

        JSONObject params = new JSONObject();
        try {
            params.put("JobNumber", jobnumber);
            params.put("Company", pref.getPreferences("Company",""));
            params.put("AssetID", pref.getPreferences("driver_assets_id",""));
            params.put("AssetCompanyID", pref.getPreferences("AssetCompanyID",""));
            params.put("AssetResellerID", pref.getPreferences("AssetResellerID",""));
            params.put("Timestamp", finalDate);
            params.put("RxTime", finalDate);
            params.put("Amount", pref.getPreferences("Amount",""));
            params.put("PIC", pref.getPreferences("PIC",""));
            params.put("Destination", pref.getPreferences("Destination",""));
            params.put("Phone", pref.getPreferences("Phone",""));
            params.put("Unit", pref.getPreferences("Unit",""));
            params.put("Flag", 2);
            params.put("Remarks", pref.getPreferences("Remarks",""));
            params.put("Receipt", pref.getPreferences("Receipt",""));
            params.put("UserID", 0);
            params.put("DriverID", pref.getPreferences("driver_id",""));
            params.put("Postal", pref.getPreferences("Postal",""));
            params.put("CusEmail", pref.getPreferences("CusEmail",""));
            params.put("Site", pref.getPreferences("Site",""));
            params.put("ReferenceNo", pref.getPreferences("jobReferenceNo",""));




        } catch (JSONException e) {
            e.printStackTrace();

        }

        final String url = AppConstant.endpoint_url + "jobinfo";

        System.out.println(url);

        AQuery aq = new AQuery(this);
        aq.post(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String response, AjaxStatus status) {
                System.out.println(response);
                if (status.getMessage().equals("OK")) {
                    try {
                        JSONObject obj = new JSONObject(response);

                        Log.e("Response : ", obj.getString("JobID"));
                        int item_no = 1;
                        for(int j = 0; j < pestsList.size(); j++){
                            uploadPestTreatment(obj.getInt("JobID"), item_no,
                                    pestsList.get(j).getPestDesc(), pestsList.get(j).getTreatmentDesc());

                            item_no++;
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.header("Content-Type", "application/x-www-form-urlencoded")
                .header("_method", "POST"));
    }

    private void uploadPestTreatment(int jobID, int itemNo, String pestDesc, String treatmentDesc){
        JSONObject params = new JSONObject();
        try {
            params.put("JobID", jobID);
            params.put("ItemNo", itemNo);
            params.put("PestDesc", pestDesc);
            params.put("TreatmentDesc", treatmentDesc);

        } catch (JSONException e) {
            e.printStackTrace();

        }

        final String url = AppConstant.endpoint_url + "pesttreatmentinfo";

        System.out.println(url);

        AQuery aq = new AQuery(this);
        aq.post(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String response, AjaxStatus status) {
                System.out.println(response);
                if (status.getMessage().equals("OK")) {


                }
            }
        }.header("Content-Type", "application/x-www-form-urlencoded")
                .header("_method", "POST"));
    }

    public class uploadServiceForms extends AsyncTask<String, Void, Long> {

        protected Long doInBackground(String... FULL_PATH_TO_LOCAL_FILE) {
            {
                FTPClient ftpClient = new FTPClient();
                int reply;

                String servername = AppConstant.ads_host;
                int port = 21;
                String user = "SystemPest_Forms";
                String pass = "trackacecom";
                try {
                    Log.e("START :", "UPLOADING");
                    //System.out.println("Entered Data Upload loop!");
                    ftpClient.connect(servername, 21);
                    ftpClient.login(user, pass);
                    ftpClient.sendCommand("Enter FTP Server");
                    ftpClient.changeWorkingDirectory("/");

                    reply = ftpClient.getReplyCode();

                    if (FTPReply.isPositiveCompletion(reply)) {
                        System.out.println("Connected Success");
                    } else {
                        System.out.println("Connection Failed");
                        ftpClient.disconnect();
                    }

                    try
                    {
                        Log.e("PROCEEDING :", "UPLOADING");

                        ftpClient.enterLocalPassiveMode(); // important!
                        ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(Calendar.getInstance().getTime());
                        //Change Timestamp Timezone
                        Date eightHourBack = cal.getTime();
                        String timestamp = new SimpleDateFormat("ddMMMyyyy").format(eightHourBack);
                        String filename = pref.getPreferences("maintenance_jobID","") + "-" + timestamp + ".pdf";
                        String mPath = Environment.getExternalStorageDirectory().getPath() + "/SystemPest-MaintenanceService/"  + "/" + filename;

                        FileInputStream in = new FileInputStream(new File(mPath));
                        boolean result = ftpClient.storeFile("/"+ pref.getPreferences("maintenance_jobID","") +".png", in);
                        in.close();
                        if (result) Log.v("upload result", "succeeded");
                        ftpClient.logout();
                        ftpClient.disconnect();
                        //File file = new File(path);
                        //boolean deleted = file.delete();
                    }
                    catch (Exception e)
                    {
                        Log.e("Upload Failed : ", String.valueOf(e));
                        e.printStackTrace();
                    }

                } catch (SocketException e) {
                    Log.e("FTP", e.getStackTrace().toString());
                    //System.out.println("Socket Exception!");
                } catch (UnknownHostException e) {
                    Log.e("FTP", e.getStackTrace().toString());
                } catch (IOException e) {
                    Log.e("FTP", e.getStackTrace().toString());
                    //System.out.println("IO Exception!");
                }
                return null;
            }
        }
    }

    private void backDialog(final String title, String message){
        alert_dialog = new AlertDialog.Builder(this);
        alert_dialog.setCancelable(true);
        alert_dialog.setTitle(title);
        alert_dialog.setMessage(message);
        alert_dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert_dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                onBackPressed();
            }
        });
        alert_dialog.show();

    }

    private void completeDialog(final String title, String message){
        alert_dialog = new AlertDialog.Builder(this);
        alert_dialog.setCancelable(true);
        alert_dialog.setTitle(title);
        alert_dialog.setMessage(message);
        alert_dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert_dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                Calendar cal = Calendar.getInstance();
                cal.setTime(Calendar.getInstance().getTime());
                Date eightHourBack = cal.getTime();
                String date = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(eightHourBack);
                String time = new SimpleDateFormat("HHmmss", Locale.ENGLISH).format(eightHourBack);
                String signature_name = "SIGNATURE_"+ pref.getPreferences("maintenance_jobID","") + "_" + date + "_" + time;
                final String Signature_Client_StoredPath = DIRECTORY_CLIENT_SIGNATURE + pref.getPreferences("maintenance_jobID","") + ".png";

                dialogSignature("Client Signature", Signature_Client_StoredPath, DIRECTORY_CLIENT_SIGNATURE);
                //onBackPressed();
            }
        });
        alert_dialog.show();

    }

}

class SendMaintenanceEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
    Mail m;
    MaintenanceDetailActivity activity;

    public SendMaintenanceEmailAsyncTask() {
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (m.send()) {
                Log.e("Mail : ", "Email sent...");
            } else {
                Log.e("Mail : ", "Email failed to send...");
            }

            return true;
        } catch (AuthenticationFailedException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
            Log.e("Authentication", String.valueOf(e));
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            Log.e("Messaging Exception", String.valueOf(e));
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", String.valueOf(e));
            return false;
        }
    }

}