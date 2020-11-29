package org.techtown.foryou;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//import org.techtown.diary.data.WeatherItem;
//import org.techtown.diary.data.WeatherResult;


public class MainActivity extends AppCompatActivity implements OnTabItemSelectedListener, OnRequestListener, AutoPermissionsListener, MyApplication.OnResponseListener  {
    private static final String TAG = "MainActivity";
    WebView webview;
    String url="http://m.facebook.com";
    BottomNavigationView bottomNavigationView;

    Location currentLocation;


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH시");
    SimpleDateFormat dateFormat3 = new SimpleDateFormat("MM월 dd일");
    SimpleDateFormat dateFormat4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    int locationCount = 0;
    String currentWeather;
    String currentAddress;
    String currentDateString;
    Date currentDate;


    org.techtown.foryou.fragment1 fragment1;
    org.techtown.foryou.fragment2 fragment2;
    org.techtown.foryou.fragment3 fragment3;
    org.techtown.foryou.fragment4 fragment4;
    org.techtown.foryou.diary1 diary1;
    public static NoteDatabase mDatabase = null;
    private GPSListener gpsListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webview=(WebView)findViewById(R.id.webview);


        //프래그먼트 생성
        fragment1 = new fragment1();
        fragment2 = new fragment2();
        fragment3 = new fragment3();
        fragment4 = new fragment4();
        diary1 = new diary1();

        //제일 처음 띄워줄 뷰를 세팅해줍니다. commit();까지 해줘야합니다.
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment1).commitAllowingStateLoss();

        //bottomnavigationview의 아이콘을 선택 했을 때 원하는 프래그먼트가 띄워질 수 있도록 리스터를 추가합니다.
            bottomNavigationView = findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                private MenuItem item;
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    switch (item.getItemId()) {
                        //menu_bottom.xml에서 지정해줬던 아이디 값을 받아와서 각 아이디값마다 다른 이벤트를 발생시킵니다.
                        case R.id.tab1: {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_layout, fragment1).commitAllowingStateLoss();
                            return true;
                        }
                        case R.id.tab2: {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_layout, fragment2).commitAllowingStateLoss();
                            return true;
                        }
                        case R.id.tab3: {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_layout, fragment3).commitAllowingStateLoss();
                            return true;
                        }
                        case R.id.tab4: {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_layout, fragment4).commitAllowingStateLoss();
                            return true;
                        }
                    }
                    return false;
                }
                });
        com.pedro.library.AutoPermissions.Companion.loadAllPermissions(this, 101);

        setPicturePath();


        // 데이터베이스 열기
        openDatabase();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
    }

    /**
     * 데이터베이스 열기 (데이터베이스가 없을 때는 만들기)
     */
    public void openDatabase() {
        // open database
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }

        mDatabase = NoteDatabase.getInstance(this);
        boolean isOpen = mDatabase.open();
        if (isOpen) {
            Log.d(TAG, "Note database is open.");
        } else {
            Log.d(TAG, "Note database is not open.");
        }
    }


    public void setPicturePath() {
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        AppConstants.FOLDER_PHOTO = sdcardPath + File.separator + "photo";
    }

    public void onTabSelected(int position) {
        if (position == 0) {
                bottomNavigationView.setSelectedItemId(R.id.tab3);

            } else if (position == 1) {
                diary1 = new diary1();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_layout, diary1).commit();
            }

    }

    @Override
    public void showFragment2(Note item) {

    }

    public void showdiary1(Note item) {

        diary1 = new diary1();
        diary1.setItem(item);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_layout, diary1).commit();

    }

    @Override
    public boolean onCreateOptionMenu(Menu menu) {
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int requestCode, String[] permissions) {
//        Toast.makeText(this, "permissions denied : " + permissions.length, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGranted(int requestCode, String[] permissions) {
//        Toast.makeText(this, "permissions granted : " + permissions.length, Toast.LENGTH_LONG).show();
    }

    public void onRequest(String command) {
        if (command != null) {
            if (command.equals("getCurrentLocation")) {
                getCurrentLocation();
            }
        }
    }

    public void getCurrentLocation() {
        // set current time
        currentDate = new Date();
        currentDateString = dateFormat3.format(currentDate);
        if (diary1 != null) {
            diary1.setDateString(currentDateString);
        }


        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (currentLocation != null) {
                double latitude = currentLocation.getLatitude();
                double longitude = currentLocation.getLongitude();
                String message = "Last Location -> Latitude : " + latitude + "\nLongitude:" + longitude;
                println(message);


                getCurrentAddress();
            }

            gpsListener = new GPSListener();
            long minTime = 10000;
            float minDistance = 0;

            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime, minDistance, gpsListener);

            println("Current location requested.");

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void stopLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            manager.removeUpdates(gpsListener);

            println("Current location requested.");

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processResponse(int requestCode, int responseCode, String response) {

    }



    class GPSListener implements LocationListener {
        public void onLocationChanged(Location location) {
            currentLocation = location;

            locationCount++;

            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            String message = "Current Location -> Latitude : " + latitude + "\nLongitude:" + longitude;
            println(message);

            getCurrentAddress();
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    public void getCurrentAddress() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            currentAddress = address.getLocality();
            if (address.getSubLocality() != null) {
                currentAddress += " " + address.getSubLocality();
            }

            String adminArea = address.getAdminArea();
            String country = address.getCountryName();
            println("Address : " + country + " " + adminArea + " " + currentAddress);

            if (diary1 != null) {
                diary1.setAddress(currentAddress);
            }
        }
    }

    private void println(String data) {
        Log.d(TAG, data);
    }
}

