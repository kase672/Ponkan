package com.android.ponkan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.d("NewIntent", "intent:"+ intent);

        intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        Log.d("intent","intent:"+intent);

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent);
                MainListFragment mainListFragment = new MainListFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentMainContainer,mainListFragment);
                transaction.commit();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onNewIntent(getIntent());

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());//toolbar_titleの場所にタイトルを入れる

        getSupportActionBar().setDisplayShowTitleEnabled(false);//アクションバーのタイトル非表示


    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        Boolean isStringExist = sharedText.contains("http");
        String cutText;
        Log.d("sharedText", "sharedText:"+sharedText);
        if (isStringExist) {
            int delimiterIndex = sharedText.indexOf("\"",2);
            cutText = sharedText.substring(1,delimiterIndex);
            Log.d("cutText", "cutText:"+cutText);
        } else {
            cutText = sharedText;
        }

        Log.d("sharedText", "sharedText:"+sharedText);
        if (cutText != null) {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.addBook(cutText);
        }
    }

}