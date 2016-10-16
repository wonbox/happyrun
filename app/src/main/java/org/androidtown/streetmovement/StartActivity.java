package org.androidtown.streetmovement;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
/**
 *
 * 걷기 측정을 위한 측정화면 코드
 */

public class StartActivity extends Activity {
    LocationManager Locationmanager;
    GPSListenner gpsListenner;
    Criteria criteria;
    String provider;
    GoogleMap map;
    MarkerOptions startmarker;
    MarkerOptions personmarker;
    Marker person;
    RelativeLayout slidPage;
    Animation translateDown;
    Animation translateUp;
    TextView distanceText;
    TextView calText;
    TextView speedText;
    Polyline polyline;
    PolylineOptions options;
    double oxygen;
    double calogies;
    double distance;
    double startlan;
    double startlon;
    double prelat;
    double prelon;
    double finishlat;
    double finishlon;
    double speed;
    Chronometer chronometer;
    long mLastStopTime;
    Database dbManager;
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        chronometer = (Chronometer) findViewById(R.id.chronometer);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        slidPage = (RelativeLayout) findViewById(R.id.slidingPage);
        translateDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_down);
        translateUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_up);
        startLocationService();


        distanceText = (TextView) findViewById(R.id.textView3);
        calText = (TextView) findViewById(R.id.textView4);
        speedText = (TextView) findViewById(R.id.textView2);

        //측정버튼 클릭시
        Button startButton = (Button) findViewById(R.id.button5);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });

        //메뉴버튼 클릭시
        Button Downbutton = (Button) findViewById(R.id.button4);
        Downbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidPage.setVisibility(View.VISIBLE);
                slidPage.startAnimation(translateDown);
            }
        });
        //취소 버튼 클릭시
        Button Upbutton = (Button) findViewById(R.id.button3);
        Upbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidPage.setVisibility(View.GONE);
                slidPage.startAnimation(translateUp);
            }
        });
        //다시시작 버튼 클릭시
        Button refreshButton = (Button) findViewById(R.id.button1);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        //정지 버튼 클릭시
        Button finish = (Button) findViewById(R.id.button2);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //측정이 시작했다면
                if (options != null) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    chronoPause();

                    if (gpsListenner != null) {
                        Locationmanager.removeUpdates(gpsListenner);
                        gpsListenner = null;
                    }
                    GpsFinsh();

                }
                else{
                    Toast.makeText(getApplicationContext(),"측정을 시작하세요." , Toast.LENGTH_LONG).show();
                }
            }

        });



        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //db , polyline , timer , gps 초기화
    private void init() {

        if(options == null)
        {
            options = new PolylineOptions();
            options.width(10);
            options.color(Color.BLUE);
            dbManager = new Database(getApplicationContext(), "Run.db", null, 1);
            chronoStart();
        }
        else {
            Toast.makeText(getApplicationContext(), "현재 측정 중입니다." ,Toast.LENGTH_LONG ).show();

        }
   }
    //GPS 설정 및 연결
    private void startLocationService() {
        Locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gpsListenner = new GPSListenner();

        criteria = new Criteria();
        criteria.setAccuracy(criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);

        provider = Locationmanager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            Locationmanager.requestLocationUpdates(provider, 1000, 1, gpsListenner);
        }

    }
    //현재 위치 정보
    private void showCurrentLocation(Double latitue, Double longitude) {
        LatLng curPoint = new LatLng(latitue, longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 18));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        animateMarker(person, curPoint, false);
    }


    //거리 계산
    public double calDistance(double lat1, double lon1, double lat2, double lon2) {

        double theta, dist;
        theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.60934;    // 단위 mile 에서 km 변환.
        return dist;
    }

    // 주어진 도(degree) 값을 라디언으로 변환
    private double deg2rad(double deg) {
        return (double) (deg * Math.PI / (double) 180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad) {
        return (double) (rad * (double) 180d / Math.PI);
    }

    //refresh버튼 눌럿을시
    private void refresh() {
        map.clear();
        if(options != null)
            options.getPoints().clear();

        if (polyline != null)
            polyline.remove();
        polyline = null;

        if(person != null)
          person = null;
        chronometer.setBase(SystemClock.elapsedRealtime());
        startlan = 0.0;
        startlon = 0.0;
        distance = 0.0;
        speed = 0.0;
        calogies = 0.0;
        speedText.setText("이동속도\n" + speed + "Km/h");
        distanceText.setText("이동거리\n" + distance + "Km");
        calText.setText("칼로리\n" + calogies + "Kcal");

    }

    //GPS 연결 여부 함수
    private void GpsFinsh() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this).setTitle("결과").setMessage(distanceText.getText() + "\n걷기 시간\n " + chronometer.getText() + "\n" + calText.getText())
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbManager.insert("insert into RUN_LIST values(null, strftime('%Y-%m-%d','now','localtime') , strftime('%H:%M','now','localtime') ,"
                                + Double.parseDouble(String.format("%.3f", distance))
                                + ", '" + chronometer.getText() + "'," + RunningCal() + "," + startlan + ","
                                + startlon + "," + finishlat + "," + finishlon + ");");
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        finish();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        if (gpsListenner == null) {
                            gpsListenner = new GPSListenner();
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            }
                            Locationmanager.requestLocationUpdates(provider, 1000, 1, gpsListenner);
                        }
                        chronoStart();
                    }
                });

        alertDialog.show();

    }


    //칼로리 계산
    private Double RunningCal() {
        //4km/h 걷기 MET = 2.9 20세 기준 남자 65 여자 54 중간인 60kg으로 측정

        long current = SystemClock.elapsedRealtime() - chronometer.getBase();
        int time = (int) (current / 1000);
        int min = time % (60 * 60) / 60;
        oxygen = 2.9 * (3.5 * 60 * min);
        calogies = (oxygen * 5) / 1000;
        return calogies;

    }

    //스톱워치 시작
    private void chronoStart() {
        // on first start
        if (mLastStopTime == 0)
            chronometer.setBase(SystemClock.elapsedRealtime());
            // on resume after pause
        else {
            long intervalOnPause = (SystemClock.elapsedRealtime() - mLastStopTime);
            chronometer.setBase(chronometer.getBase() + intervalOnPause);
        }

        chronometer.start();
    }

    //스톱워치 일시정지
    private void chronoPause() {
        chronometer.stop();
        mLastStopTime = SystemClock.elapsedRealtime();
    }


    //마커 부드러운 움직임을 위한 함수
    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1000;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;

                polyline = map.addPolyline(options.add(new LatLng(lat, lng)));
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gpsListenner != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Locationmanager.removeUpdates(gpsListenner);
            gpsListenner = null;
        }
        if (polyline != null)
            polyline.remove();
        polyline = null;

        if(person != null)
            person = null;
    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Start Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://org.androidtown.streetmovement/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

         // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Start Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://org.androidtown.streetmovement/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    private class GPSListenner implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {


            if (options != null){
                if (startlan == 0.0 && startlon == 0.0) {
                    //marker 초기화
                    startmarker = new MarkerOptions();
                    startmarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.startmarker));
                    personmarker = new MarkerOptions();
                    personmarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.user));

                    LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());

                    //marker 위치 표시
                    startmarker.position(curPoint);
                    map.addMarker(startmarker);

                    personmarker.position(curPoint);
                    person = map.addMarker(personmarker);

                    //초기값 설정 및 초기값 계산 X
                    speed = 0.0;

                    //db 저장할 초기 위치
                    startlan = location.getLatitude();
                    startlon = location.getLongitude();

                    //거리계산을 위한 이전 위치
                    prelat = location.getLatitude();
                    prelon = location.getLongitude();

                } else {

                    showCurrentLocation(location.getLatitude(), location.getLongitude());

                    if (location.hasSpeed())
                        speed = location.getSpeed() * 3.6;

                    //거리계산을 위한 마지막 수신 위치
                    finishlat = location.getLatitude();
                    finishlon = location.getLongitude();

                    //이동거리 측정
                    if (prelat != finishlat || prelon != finishlon)//현재 위도 및 경도 또는 이전 위도 및 경도 같지 않다면 계산
                    {
                        distance += calDistance(prelat, prelon, finishlat, finishlon);
                        prelat = finishlat;
                        prelon = finishlon;
                        calText.setText("칼로리\n" + RunningCal() + "Kcal");
                        speedText.setText("이동속도\n" + Double.parseDouble(String.format("%.1f", speed)) + "Km/h");
                        distanceText.setText("이동거리\n" + Double.parseDouble(String.format("%.3f", distance)) + "Km");
                    }

                }
           }
           else{
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 17));
           }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

}