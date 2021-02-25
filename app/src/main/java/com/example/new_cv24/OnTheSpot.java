package com.example.new_cv24;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;


public class OnTheSpot extends AppCompatActivity {

    JSONObject jsonObject, Addr;
    String DateTime, longitude, latitude;
    TextView DATETIME, ADDR;
    Button complete;
    long diff;
    Geocoder geocoder;
    Address geoAddr;
    public String address;
    double LATITUDE, LONGITUDE;
    LatLng HERE;

    private static String IP_ADDRESS = "cv24.dothome.co.kr";
    private static String TAG = "phptest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.on_the_spot);

        //잠금화면이어도 화면 뜨게 설정
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

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

        //'해결완료' 버튼을 누르는 순간 해당 사건은 DB에 저장된다.
        complete = findViewById(R.id.complete);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //위도,경도를 주소(String 타입)로 변환한다.
                LATITUDE = Double.parseDouble(latitude);
                LONGITUDE = Double.parseDouble(longitude);
                HERE = new LatLng(LATITUDE, LONGITUDE);
                address = getCurrentAddress(HERE);

                //해당 사건을 DB에 등록하는 과정
                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/insert.php", DateTime, address);
            }
        });
    }

    //String jsonStr = "{ 'addr': {'lat': 37.50497683800223, 'long': 126.9391820120632}, 'DateTime' : '2021-01-25 16:35:31'}";
    /* UDP로 받은 데이터(JSON 구조의 String 타입)를 JSON 객체로 변환하여 내용추출하는 메소드 */
    public void JSONparse(String jsonStr) {
        try {
            jsonObject = new JSONObject(jsonStr);  //JSON string을 JSON 객체로 변경

            //situation = jsonObject.getString("situation");  //위험상황 변수 저장
            DateTime = jsonObject.getString("DateTime");  //사건 발생 일시 및 시간 변수 저장

            Addr = jsonObject.getJSONObject("addr");
            latitude = Addr.getString("lat");  //사건 발생 위치 위도 저장
            longitude = Addr.getString("long");  //사건 발생 위치 경도 저장

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*'자세히'를 누르면 LocationMap으로 이동하는 메소드*/
    private void mapInfo(CharSequence message){
        Intent intent = new Intent(getApplicationContext(), LocationMap.class);
        intent.putExtra("location info", message);
        startActivity(intent);
    }
    

    class InsertData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;  //해당 사건을 DB에 등록 중이라는 걸 알려주는 알림창 객체

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(OnTheSpot.this,
                    "DB 등록", "잠시만 기다려 주세요.", true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
        }

        @Override
        protected String doInBackground(String... params) {

            //InsertData task = new InsertData();
            //task.execute("http://" + IP_ADDRESS + "/insert.php", DateTime, address);
            //에서 "http:// ~ /insert.php"가 params[0]
            //DateTime이 params[1]
            //address가 params[2]

            String serverURL = (String)params[0];
            String time = (String)params[1];
            String address = (String)params[2];

            String postParameters = "time=" + time + "&address=" + address;

            try {
                URL url = new URL(serverURL);
                //URL과 연동하는 객체 생성해서 연결한다.
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                //데이터를 DB에 올린다.
                OutputStream outputStream = httpURLConnection.getOutputStream();  //어플에서 DB로 정보를 보내줄 것이므로 outputStream
                outputStream.write(postParameters.getBytes("UTF-8"));  //데이터를 보낸다.
                outputStream.flush();
                outputStream.close();

                //데이터를 DB 상에 보내는 요청 성공 여부 확인 후...
                //다시 해당 정보를 어플 상에 띄우기?????
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);
                InputStream inputStream;  //다시 해당 정보를 어플리케이션 상에 띄우기 위해 필요??
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {  //특정 http 요청 성공 (=200)이면
                    inputStream = httpURLConnection.getInputStream();
                }
                else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }

    /* 지오코더를 이용해서 GPS를 주소로 변환해주는 메소드 */
    public String getCurrentAddress(LatLng HERE) {
        geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        //예외처리
        try {
            addresses = geocoder.getFromLocation(HERE.latitude, HERE.longitude, 1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }
        else {
            geoAddr = addresses.get(0);
            //Toast.makeText(this,geoAddr.getAddressLine(0),Toast.LENGTH_LONG);
            Log.d("getCurrentAddress",geoAddr.getAddressLine(0));
            return geoAddr.getAddressLine(0).toString();  //리턴값이 String
        }
    }
}