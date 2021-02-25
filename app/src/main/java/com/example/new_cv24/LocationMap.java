package com.example.new_cv24;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationMap extends AppCompatActivity implements OnMapReadyCallback{
/*public class LocationMap extends AppCompatActivity {*/

    JSONObject jsonObject;
    JSONObject Addr;
    String latitude, longitude;
    TextView ADDR;
    LatLng HERE;
    Geocoder geocoder;
    Address address;  //지오코더로 가져오는 주소변수 (Address 객체)
    public String geoAddr; // 지오코더로 가져오는 주소 변수 (getCurrentAddress()의 return값)

    private GoogleMap mMap;

    double LATITUDE, LONGITUDE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_map);

        //잠금화면이어도 화면 뜨게 설정
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        Intent intent = getIntent();
        String data = intent.getStringExtra("location info");
        JSONparse(data);

        LATITUDE = Double.parseDouble(latitude);
        LONGITUDE = Double.parseDouble(longitude);

        HERE = new LatLng(LATITUDE, LONGITUDE);
        geoAddr = getCurrentAddress(HERE);

        ADDR = findViewById(R.id.address);
        ADDR.setText(geoAddr);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap){
        mMap = googleMap;

        //구글 맵 fragment에 해당 위치 마커 표시
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(HERE);
        markerOptions.title("사건발생위치");
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HERE, 10));
    }

    /* UDP로 받은 데이터(JSON 구조의 String 타입)를 JSON 객체로 변환하여 내용추출하는 메소드 */
    public void JSONparse(String jsonStr) {
        try {
            jsonObject = new JSONObject(jsonStr);  //JSON string을 JSON 객체로 변경
            Addr = jsonObject.getJSONObject("addr");
            latitude = Addr.getString("lat");  //사건 발생 위치 위도 저장
            longitude = Addr.getString("long");  //사건 발생 위치 경도 저장

        } catch (JSONException e) {
            e.printStackTrace();
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
            address = addresses.get(0);
            return address.getAddressLine(0).toString();  //리턴값이 String
        }
    }
}