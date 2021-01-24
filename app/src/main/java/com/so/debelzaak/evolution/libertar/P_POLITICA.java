package com.so.debelzaak.evolution.libertar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.so.debelzaak.evolution.libertar.ShowActivity.MainActivity;

public class P_POLITICA extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_politica);

        WebView webView = (WebView) findViewById(R.id.wv_content);
        webView.loadUrl("file:///android_asset/pprivacidade.html");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent inteny = new Intent(P_POLITICA.this, MainActivity.class);
        startActivity(inteny);
        overridePendingTransition(R.anim.volte, R.anim.volte_ii);
        finish();
    }
}
