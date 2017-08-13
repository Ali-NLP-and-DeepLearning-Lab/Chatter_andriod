package com.hanium.chat.chatter_andriod.css;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.hanium.chat.chatter_andriod.R;

/**
 * Created by kliv on 2017-08-06.
 */

public class ToSpeakActivity extends AppCompatActivity {

    private WebView webView;
    private WebSettings webSettings;

    private String clientId = "User's_Client_ID";
    private String clientSecret = "User's_Client_SECRET";

    private Context context;

    private TTSTask task;

    private String[] message;

    private FloatingActionButton fab;
    private EditText input;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tospeak);

        context = this;


        // 웹뷰 세팅
        webView = (WebView) findViewById(R.id.webView);     // 레이어와 연결
        webView.setWebViewClient(new WebViewClient());      // 클릭시 새 창 안뜨게

        webSettings = webView.getSettings();               // 세부 세팅 등록
        webSettings.setJavaScriptEnabled(true);             // Javascript 사용 허용
        webSettings.setLoadWithOverviewMode(true);          // 컨텐츠가 웹뷰보다 크면 사이즈 맞춤

        // 실제 서버
        webView.loadUrl("http://ec2-52-78-43-210.ap-northeast-2.compute.amazonaws.com:8080/chatter/chat");

        // 테스트 서버
//        webView.loadUrl("http://121.142.148.18:8080/chatter/chat");

        input = (EditText) findViewById(R.id.input);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp;
                if (input.getText().length() > 0) {
                    temp = input.getText().toString();
                    message = new String[]{ temp };

                    task = new TTSTask();
                    task.execute(message);
                    input.setText("");
                }
            }
        });
    }

    private class TTSTask extends AsyncTask<String[], Void, String> {
        TTSConfig tts = new TTSConfig(clientId, clientSecret, context);

        @Override
        protected String doInBackground(String[]... strings) {
            tts.speak(message);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
