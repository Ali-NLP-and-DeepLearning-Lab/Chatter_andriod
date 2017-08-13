package com.hanium.chat.chatter_andriod.csr;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.hanium.chat.chatter_andriod.MainActivity;
import com.hanium.chat.chatter_andriod.R;
import com.hanium.chat.chatter_andriod.csr.utils.AudioWriterPCM;
import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by kliv on 2017-08-05.
 */

public class ToTextActivity extends AppCompatActivity {

    private WebView webView;
    private WebSettings webSettings;

    private FloatingActionButton fab;
    private TextView txtResult;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CLIENT_ID = "User's_Client_ID";

    private Context context;

    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;

    private String mResult;

    private AudioWriterPCM writer;

    // Handle speech recognition Messages.
    private void handleMessage(Message msg) {
        context = this;

        switch (msg.what) {
            case R.id.clientReady:
                // Now an user can speak.
                Toast.makeText(getApplicationContext(), "Speak", Toast.LENGTH_SHORT).show();
                writer = new AudioWriterPCM(context.getCacheDir() + "/SpeechPCM");
                writer.open("stt");
                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;

            case R.id.partialResult:
                // Extract obj property typed with String.
                mResult = (String) (msg.obj);
                txtResult.setText(mResult);
                break;

            case R.id.finalResult:
                // 최종 결과값이 정확도가 높은 5개 순으로 List<String>으로 리턴
                // The first element is recognition result for speech.
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
//                StringBuilder strBuf = new StringBuilder();
//                for(String result : results) {
//                    strBuf.append(result);
//                    strBuf.append("\n");
//                }
//                mResult = strBuf.toString();
                // 일단 정확도 관계없이 리스트의 가장 처음 문장을 출력하도록
                mResult = results.get(0);
                txtResult.setText(mResult);
                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }
                mResult = "Error code : " + msg.obj.toString();
                txtResult.setText(mResult);
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totext);


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

        txtResult = (TextView) findViewById(R.id.txt_result);

        // 음성인식
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_MOVE) {
                    mResult = "";
                    Toast.makeText(getApplicationContext(), "Wait...", Toast.LENGTH_SHORT).show();
                    naverRecognizer.recognize();
                } else if (event.getAction() == MotionEvent.ACTION_UP){
                    Log.d(TAG, "stop and wait Final Result");
                    Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
                    naverRecognizer.getSpeechRecognizer().stop();
                    webView.loadUrl("javascript:toInput('" + mResult + "')");
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // NOTE : initialize() must be called on start time.
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mResult = "";
        txtResult.setText("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // NOTE : release() must be called on stop time.
        naverRecognizer.getSpeechRecognizer().release();
    }

    // Declare handler for handling SpeechRecognizer thread's Messages.
    static class RecognitionHandler extends Handler {
        private final WeakReference<ToTextActivity> mActivity;

        RecognitionHandler(ToTextActivity activity) {
            mActivity = new WeakReference<ToTextActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ToTextActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
}
