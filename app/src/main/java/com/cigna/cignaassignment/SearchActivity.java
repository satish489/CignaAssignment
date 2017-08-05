package com.cigna.cignaassignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.toString();
    EditText editText;
    ProgressBar progressBar;
    String urlString = "https://newsapi.org/v1/articles?source=%s&apiKey=cfb33b18fba149509aec7137ec9c57d0";
    private List<NewsItem> mNewsList;
    private RecyclerView recyclerView;
    NewsListAdapter mAdapter;
    private RelativeLayout mSearchView;
    private String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        editText = (EditText) findViewById(R.id.editText);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            searchText = bundle.getString("SearchText");
        }

        editText.setText(searchText);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mNewsList = new ArrayList<>();
        mSearchView = (RelativeLayout) findViewById(R.id.search);
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideVirtualKeyboard();
                search();
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideVirtualKeyboard();
                    search();
                }
                return false;
            }
        });

        hideVirtualKeyboard();
        search();
    }

    private void hideVirtualKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void search() {
        mNewsList.clear();
        String searchKey = editText.getText().toString().trim();
        if (!TextUtils.isEmpty(searchKey)) {
            String tag_json_obj = "json_obj_req";

            String url = String.format(urlString,searchKey);
            showProgress();
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    url, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            hideProgress();
                            if(response==null) return;
                            String status = response.optString("status");
                            if (!TextUtils.isEmpty(status) && status.equalsIgnoreCase("OK")) {
                                    JSONArray jsonArray = response.optJSONArray("articles");
                                    if (jsonArray != null && jsonArray.length() > 0) {
                                        Gson gson = new Gson();
                                        mNewsList = gson.fromJson(jsonArray.toString(), new TypeToken<List<NewsItem>>(){}.getType());
                                    }
                            }
                            updateUI();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    // hide the progress dialog
                    hideProgress();
                    updateUI();
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }
    }

    void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    void hideProgress() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void updateUI() {
        if (mNewsList != null && mNewsList.size()>0) {
            mAdapter = new NewsListAdapter(mNewsList);
            recyclerView.setAdapter(mAdapter);
        } else {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        }
    }

}
