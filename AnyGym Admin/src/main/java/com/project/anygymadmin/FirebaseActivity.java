package com.project.anygymadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class FirebaseActivity extends AppCompatActivity {
    private WebView firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
        firebase = findViewById(R.id.firebase);
        firebase.getSettings().setLoadsImagesAutomatically(true);
        firebase.getSettings().setJavaScriptEnabled(true);
        firebase.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        firebase.setWebViewClient(new WebViewClient());
        firebase.loadUrl("https://console.firebase.google.com/");
    }
}
