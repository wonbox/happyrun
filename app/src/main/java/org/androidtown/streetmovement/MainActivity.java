package org.androidtown.streetmovement;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

/**
 *
 * Main화면 코드
 */
public class MainActivity extends Activity {
    LocationManager locManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locManager = (LocationManager)getSystemService(this.LOCATION_SERVICE);

        //GPS연결 여부
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            GpsChecked();
        }

        //시작 버튼 클릭시
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , StartActivity.class);
                startActivity(intent);
            }
        });
        //통계 버튼 클릭시
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , GraphActivity.class);
                startActivity(intent);
            }
        });
        //기록 버튼 클릭시
        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , DbActivity.class);
                startActivity(intent);
            }
        });
    }
    //GPS연결 확인
    private void GpsChecked(){
        new AlertDialog.Builder(this).setTitle("GPS setting").setMessage("GPS가 꺼져 있습니다.\n GPS를 켜시겠습니까?")
                .setPositiveButton("GPS 켜기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //gps settingView show
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                }).setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

    }
}
