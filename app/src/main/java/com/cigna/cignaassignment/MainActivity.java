package com.cigna.cignaassignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.toString();
    EditText editText;
    ProgressDialog progressDialog;
    String urlString = "https://newsapi.org/v1/articles?source=%s&apiKey=cfb33b18fba149509aec7137ec9c57d0";
    private List<NewsItem> mNewsList;
    NewsListAdapter mAdapter;
    private RelativeLayout mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);

        mNewsList = new ArrayList<>();
        mSearchView = (RelativeLayout) findViewById(R.id.search);
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    search();
                }
                return false;
            }
        });
    }


    private void search() {
        String searchKey = editText.getText().toString().trim();
        if(!TextUtils.isEmpty(searchKey)) {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("SearchText", searchKey);
            startActivity(intent);
        }
    }

}
