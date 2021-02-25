package com.example.new_cv24;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/* [변경] UDP 통신으로 전달받을 JSON 객체 */
// {
//    "addr": {"lat":37.50497683800223, "long":126.9391820120632},
//    "DateTime" : "2021-01-25 16:35:31"
// }


public class alertDanger extends AppCompatActivity {

    TextView DATETIME, ADDR;
    String DateTime;
    JSONObject jsonObject;
    Server server;
    Client client;

    Bitmap bitmap = null;  //비디오 프레임(받은 직후는 byte배열 형태)을 bitmap으로 변환하여 저장하기 위한 변수

    /* 뒤로가기 버튼 두 번 누른 상황에 대한 처리를 해주는 객체 */
    private BackPressCloseHandler backPressCloseHandler;

    /* 비디오 스트리밍에 관한 변수 */
    //ImageView VIDEO;
    //VideoView VIDEO;  //비디오 객체
    //final String path = "udp://192.168.0.7:9091";  //비디오 받는 서버 주소 및 포트
    //MediaController mediaController;
    //Uri video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_danger);

        //잠금화면이어도 화면 뜨게 설정
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

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


        //'출동' 버튼을 누르는 이벤트 처리
        Button callOut = findViewById(R.id.goHome);
        callOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                /*Intent intent = new Intent(getApplicationContext(), OnTheSpot.class);
                intent.putExtra("violence case info", data);
                startActivity(intent);*/


                //alertDialog 띄우기
                AlertDialog.Builder builder = new AlertDialog.Builder(alertDanger.this);
                builder.setTitle("현장 출동").setMessage("정말 출동하시겠습니까?");

                //1. '네'를 누른 상황 : 클라이언트
                //1) 서버 측으로 JSON 데이터 보내기
                //2) OnTheSpot으로 이동
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*client = new Client(data);  //JSON 데이터 넘겨주면서 스레드 생성
                        client.start();*/
                        
                        Intent intent = new Intent(getApplicationContext(), OnTheSpot.class);
                        intent.putExtra("violence case info", data);
                        startActivity(intent);
                    }
                });

                //2. '아니오'를 누른 상황 : 서버
                //1) 서버 세팅 시작하고 클라이언트 기다리기
                //2) 클라이언트로부터 JSON 데이터 받고 AlreadySbOnTheSpot으로 이동
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*server = new Server();  //서버소캣 생성
                        server.start();*/

                        Intent intent = new Intent(getApplicationContext(), AlreadySbOnTheSpot.class);
                        //intent.putExtra("violence case info", server.recvMsg);
                        intent.putExtra("violence case info", data);
                        startActivity(intent);
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
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


    /* 뒤로가기 버튼 두 번 누를 시 앱 종료하는 메소드 */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }


    private void mapInfo(CharSequence message){
        Intent intent = new Intent(alertDanger.this, LocationMap.class);
        intent.putExtra("location info", message);
        startActivity(intent);
    }
}
