package com.example.new_cv24;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

public class AlreadySbOnTheSpot extends AppCompatActivity {

    TextView DATETIME, ADDR;
    JSONObject jsonObject;
    String DateTime;

    /* 뒤로가기 버튼 두 번 누른 상황에 대한 처리를 해주는 객체 */
    BackPressCloseHandler backPressCloseHandler;

    //final static String url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    //final static String url = "192.168.0.7:5000";
    //VideoView videoView;

    private WebView mWebView;
    private WebSettings mWebSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.already_sb_on_the_spot);

        //잠금화면이어도 화면 뜨게 설정
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        //해당 화면은 출동을 하지 않은 경찰에게 뜨는 화면이므로
        //출동 버튼을 누른 경찰이 '보내준 UDP 데이터'를 화면에 띄워야 한다.

        //뒤로가기 버튼 두 번 누르면 어플 종료하는 객체 생성
        backPressCloseHandler = new BackPressCloseHandler(this);

        Intent intent = getIntent();
        String data = intent.getStringExtra("violence case info");
        JSONparse(data);

        //사건 발생 시간 출력
        DATETIME = (TextView) findViewById(R.id.dateTimeDetail);
        DATETIME.setText(DateTime);

        //'자세히'를 누르면 위치 정보를 가지고 LocationMap 실행
        ADDR = (TextView) findViewById(R.id.locationDetail);
        ADDR.setPaintFlags(ADDR.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        ADDR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapInfo(data);
            }
        });

        /*//URL 상의 영상을 가져와 재생한다.
        videoView = findViewById(R.id.videoView);
        loadVideo(videoView);*/

        mWebView = (WebView)findViewById(R.id.webview_login);
        mWebView.setWebViewClient(new WebViewClient());
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl("http://192.168.0.7:5000/");


        //'홈으로' 버튼을 누르면 홈화면으로 이동한다.
        Button home = (Button) findViewById(R.id.goHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomePage.class);
                startActivity(intent);
            }
        });
    }

    private void mapInfo(CharSequence message){
        Intent intent = new Intent(getApplicationContext(), LocationMap.class);
        intent.putExtra("location info", message);
        startActivity(intent);
    }

    //String jsonStr = "{ 'addr': {'lat': 37.50497683800223, 'long': 126.9391820120632}, 'DateTime' : '2021-01-25 16:35:31'}";
    /* UDP로 받은 데이터(JSON 구조의 String 타입)를 JSON 객체로 변환하여 내용추출하는 메소드 */
    public void JSONparse(String jsonStr) {
        try {
            jsonObject = new JSONObject(jsonStr);  //JSON string을 JSON 객체로 변경
            DateTime = jsonObject.getString("DateTime");  //사건 발생 일시 및 시간 변수 저장

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*public void loadVideo(View view) {
        videoView.setVideoURI(Uri.parse(url));
        videoView.requestFocus();

        //동영상 재생
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
                //Toast.makeText(getApplicationContext(), "Playing Video", Toast.LENGTH_SHORT).show();
            }
        });
    }*/
}