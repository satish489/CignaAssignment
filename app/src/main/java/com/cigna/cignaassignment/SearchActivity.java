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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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
    ProgressDialog progressDialog;
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
        String searchKey = editText.getText().toString().trim();
        if (!TextUtils.isEmpty(searchKey)) {
            new SearchTask().execute(searchKey);
        }
    }

    class SearchTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected String doInBackground(String... strings) {
            mNewsList.clear();
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(String.format(urlString, strings[0]));
                urlConnection = (HttpURLConnection) url.openConnection();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
                String resultValue = result.toString();
                return resultValue;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            try {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.optString("status");
                    if (!TextUtils.isEmpty(status)) {
                        if (status.equalsIgnoreCase("OK")) {
                            JSONArray jsonArray = jsonObject.optJSONArray("articles");
                            if (jsonArray != null && jsonArray.length() > 0) {
                                Gson gson = new Gson();
                                NewsItem[] lNewsItems = gson.fromJson(jsonArray.toString(), NewsItem[].class);
                                if (lNewsItems != null && lNewsItems.length > 0) {
                                    for (NewsItem lObject : lNewsItems) {
                                        mNewsList.add(lObject);
                                    }
                                }
                            }
                        }
                    }
                }
                updateUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    void showProgress() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, "", "Loading..!");
        }
    }

    void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void updateUI() {
        if (mNewsList != null && mNewsList.size() > 0) {
            mAdapter = new NewsListAdapter(mNewsList);
            recyclerView.setAdapter(mAdapter);
        } else {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = getIntent();
        intent.putExtra("UpdatedText", editText.getText().toString().trim());
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
}
