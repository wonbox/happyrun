package org.androidtown.streetmovement;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 *
 * 기록한 데이터의 출발점 및 도착점 알려주기 위한 코드
 */
public class MapActivity extends Activity {
    GoogleMap map;
    Database dbManager;
    int id;
    Cursor DBcursor;
    Double start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapshow)).getMap();
        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);
        dbManager = new Database(getApplicationContext(), "Run.db", null, 1);
        Mapshow(id);
    }

    private void Mapshow(int id){
        Cursor DBcursor;
        Marker start;
        Marker finish;
        LatLng Spostion;
        LatLng Fpostion;
        double startlat = 0.0;
        double startlon = 0.0;
        double finishlat = 0.0;
        double finishlon = 0.0;

        DBcursor = dbManager.MapPrint(id);

        if(startlat == 0 && finishlat == 0)
        {
            while (DBcursor.moveToNext()) {
                startlat = DBcursor.getDouble(DBcursor.getColumnIndex(DBcursor.getColumnName(0)));
                startlon = DBcursor.getDouble(DBcursor.getColumnIndex(DBcursor.getColumnName(1)));
                finishlat = DBcursor.getDouble(DBcursor.getColumnIndex(DBcursor.getColumnName(2)));
                finishlon = DBcursor.getDouble(DBcursor.getColumnIndex(DBcursor.getColumnName(3)));
            }
        }

        Spostion = new LatLng(startlat , startlon);
        Fpostion = new LatLng(finishlat , finishlon);


        map.animateCamera(CameraUpdateFactory.newLatLngZoom(Spostion,18));

        //시작 마커
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(Spostion);
        startMarker.title("출발 지점");
        startMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.startmarker));
        start = map.addMarker(startMarker);
        start.showInfoWindow();


        //도착 마커
        MarkerOptions finishMarker = new MarkerOptions();
        finishMarker.position(Fpostion);
        finishMarker.title("도착 지점");
        finishMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.finishmarker));
        finish = map.addMarker(finishMarker);





    }
}
