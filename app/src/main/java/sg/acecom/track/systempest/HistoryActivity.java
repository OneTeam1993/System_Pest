package sg.acecom.track.systempest;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sg.acecom.track.systempest.adapter.HistoryJobAdapter;
import sg.acecom.track.systempest.adapter.JobAdapter;
import sg.acecom.track.systempest.model.Jobs;
import sg.acecom.track.systempest.util.AppConstant;
import sg.acecom.track.systempest.util.AppController;
import sg.acecom.track.systempest.util.MyDividerItemDecoration;
import sg.acecom.track.systempest.util.MyPreferences;

/**
 * Created by jmingl on 4/7/18.
 */

public class HistoryActivity extends AppCompatActivity implements HistoryJobAdapter.HistoryJobAdapterListenner, SwipeRefreshLayout.OnRefreshListener{

    MyPreferences pref;
    RecyclerView recyclerHistory;
    private HistoryJobAdapter adapter;
    private List<Jobs> jobsList;
    private SearchView searchView;
    ProgressBar progressbar;
    SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("History");

        pref = new MyPreferences(this);
        jobsList = new ArrayList<>();
        adapter = new HistoryJobAdapter(this, jobsList, this);
        recyclerHistory = findViewById(R.id.recyclerHistory);
        // white background notification bar
        //whiteNotificationBar(recyclerHistory);
        swipeContainer = findViewById(R.id.swipeContainer);

        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerHistory.setItemAnimator(new DefaultItemAnimator());
        //recyclerHistory.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerHistory.setAdapter(adapter);

        progressbar = findViewById(R.id.progressbar);

        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeContainer.post(new Runnable() {

            @Override
            public void run() {

                swipeContainer.setRefreshing(true);

                // Fetching data from server
                getJobs();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getJobs();
    }

    @Override
    public void onRefresh() {
        getJobs();
    }

    private void getJobs(){
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
                "&RxTime=" + URLEncoder.encode(rxtime) + "&AssetResellerID=" + pref.getPreferences("driver_reseller_id","") +
                "&AssetCompanyID=" + pref.getPreferences("driver_company_id","") + "&DriverID=" + pref.getPreferences("driver_id","");

        Log.e("History List API : ", url);

        // prepare the Request
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        jobsList.clear();
                        swipeContainer.setRefreshing(true);
                        try{
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                if(obj.getInt("Flag") == 0){
                                    Jobs job = new Jobs();
                                    job.setJobID(obj.getInt("JobID"));
                                    job.setJobNumber(obj.getString("JobNumber"));
                                    job.setFlag(obj.getInt("Flag"));
                                    job.setFlagValue(obj.getString("FlagValue"));
                                    job.setAssetCompanyID(obj.getInt("AssetCompanyID"));
                                    job.setAssetCompany(obj.getString("AssetCompany"));;
                                    job.setAssetResellerID(obj.getInt("AssetResellerID"));
                                    job.setAssetReseller(obj.getString("AssetReseller"));
                                    job.setUserID(obj.getInt("UserID"));
                                    job.setUserName(obj.getString("UserName"));
                                    job.setAssetID(obj.getInt("AssetID"));
                                    job.setDriverID(obj.getInt("DriverID"));
                                    job.setDriverName(obj.getString("DriverName"));
                                    job.setTimestamp(obj.getString("Timestamp"));
                                    job.setRxTime(obj.getString("RxTime"));
                                    job.setCompany(obj.getString("Company"));
                                    job.setDestination(obj.getString("Destination"));
                                    job.setPostal(obj.getString("Postal"));
                                    job.setUnit(obj.getString("Unit"));
                                    job.setPIC(obj.getString("PIC"));
                                    job.setPhone(obj.getString("Phone"));
                                    job.setAmount(obj.getDouble("Amount"));
                                    job.setCusEmail(obj.getString("CusEmail"));
                                    job.setSite(obj.getString("Site"));
                                    //job.setPest(obj.getString("Pest"));
                                    job.setRemarks(obj.getString("Remarks"));
                                    job.setJobAccepted(obj.getString("JobAccepted"));
                                    job.setJobCompleted(obj.getString("JobCompleted"));

                                    job.setReceipt(obj.getString("Receipt"));
                                    job.setImage(obj.getString("Image"));
                                    job.setImageFill(obj.getString("ImageFill"));
                                    job.setReferenceNo(obj.getString("ReferenceNo"));

                                    JSONArray arrayPest = obj.getJSONArray("Pest");
                                    StringBuilder pestBuilder = new StringBuilder();
                                    StringBuilder treatmentBuilder = new StringBuilder();

                                    for(int j = 0; j < arrayPest.length(); j++){
                                        JSONObject pest = arrayPest.getJSONObject(j);
                                        if(arrayPest.length() == 1 || j + 1 == arrayPest.length()){
                                            pestBuilder.append(pest.getString("PestDesc"));
                                        }else{
                                            pestBuilder.append(pest.getString("PestDesc"));
                                            pestBuilder.append(", ");
                                        }

                                    }

                                    job.setPest(pestBuilder.toString());
                                    if(pref.getPreferences("driver_company_id","").equals("2")){
                                        JSONObject objectAc = obj.getJSONObject("AcMyInfo");
                                        job.setAreaCovered(objectAc.getString("Groups"));
                                    }else{
                                        JSONObject objectAc = obj.getJSONObject("AcInfo");
                                        job.setAreaCovered(objectAc.getString("GeneralLocation"));
                                    }


                                    jobsList.add(job);
                                }
                            }

                            // adapter.notifyDataSetChanged();
                        }catch(Exception e){
                            swipeContainer.setRefreshing(false);
                            Log.e("Response Exception :", String.valueOf(e));
                        }
                        swipeContainer.setRefreshing(false);
                        adapter.notifyDataSetChanged();

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeContainer.setRefreshing(false);
                        Log.e("Error.Response", error.toString());
                    }
                }
        );

        // Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(getRequest);
    }

    @Override
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onHistoryJobSelected(Jobs jobs) {

        pref.savePreferences("history_jobReference","");
        pref.savePreferences("history_jobID", String.valueOf(jobs.getJobID()));
        Intent intent = new Intent(this, HistoryDetailActivity.class);
        startActivity(intent);

        //Toast.makeText(getApplicationContext(), "Selected: " + jobs.getDestination() + ", " + jobs.getPIC(), Toast.LENGTH_LONG).show();
    }

}
