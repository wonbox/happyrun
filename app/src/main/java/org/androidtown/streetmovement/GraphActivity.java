package org.androidtown.streetmovement;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
/**
 *
 * Day별 통계 그래프를 위한 코드
 */

public class GraphActivity extends Activity {
    protected LinearLayout rootLayout;
    Database dbManager;
    ArrayList<String> mDay;
    ArrayList<Double> cal;
    ArrayList<Double> distance;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        dbManager = new Database(getApplicationContext(), "Run.db", null, 1);
        mDay = new ArrayList<String>();
        cal = new ArrayList<Double>();
        distance = new ArrayList<Double>();

        openChart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDay.clear();
        cal.clear();
        distance.clear();

    }

    //통계표 그리기
    private void openChart() {

        Cursor DBcursor = null;


        if(DBcursor == null)
            DBcursor = dbManager.GraphPrint();

        if(DBcursor.getCount() == 0)
            new AlertDialog.Builder(this).setTitle("기록 확인").setMessage("기록이 존재하지 않습니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }}).show();

        while (DBcursor.moveToNext()) {
            mDay.add(DBcursor.getString(DBcursor.getColumnIndex(DBcursor.getColumnName(0))));
            distance.add(DBcursor.getDouble(DBcursor.getColumnIndex(DBcursor.getColumnName(1))));
            cal.add(DBcursor.getDouble(DBcursor.getColumnIndex(DBcursor.getColumnName(2))));

        }

        XYSeries CalSeries = new XYSeries("칼로리");
        for (int i = 0; i < DBcursor.getCount(); i++) {
            CalSeries.add(i, cal.get(i));
        }

        XYSeries DistanceSeries = new XYSeries("이동거리");

        for (int i = 0; i < DBcursor.getCount(); i++) {
            DistanceSeries.add(i, distance.get(i));

        }

       //통계에 표시 될 데이터 추가
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        dataset.addSeries(CalSeries);
        dataset.addSeries(DistanceSeries);


       //통계표 설정
        XYSeriesRenderer CalRenderer = new XYSeriesRenderer();
        CalRenderer.setColor(Color.RED);
        CalRenderer.setFillPoints(true);
        CalRenderer.setDisplayChartValues(true);
        CalRenderer.setChartValuesTextSize(35);
        CalRenderer.setLineWidth(30);
        CalRenderer.setChartValuesTextAlign(Paint.Align.RIGHT);

        XYSeriesRenderer DistanceRenderer = new XYSeriesRenderer();
        DistanceRenderer.setColor(Color.CYAN);
        DistanceRenderer.setFillPoints(true);
        DistanceRenderer.setLineWidth(30);
        DistanceRenderer.setDisplayChartValues(true);
        DistanceRenderer.setChartValuesTextSize(35);
        DistanceRenderer.setChartValuesTextAlign(Paint.Align.CENTER);

        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
        multiRenderer.setXLabels(0);//X축 선마다 숫자를 나타냄
        multiRenderer.setChartTitle("요일별 차트");
        multiRenderer.setXTitle("요일");
        multiRenderer.setYTitle("칼로리/이동거리");

        multiRenderer.setLabelsColor(Color.BLACK);
        multiRenderer.setAxesColor(Color.BLACK);
        multiRenderer.setXLabelsColor(Color.BLACK);
        multiRenderer.setYLabelsColor(0,Color.BLACK);
        multiRenderer.setGridColor(Color.BLACK);
        multiRenderer.setChartTitleTextSize(100);
        multiRenderer.setAxisTitleTextSize(70);//축 제목 사이즈
        multiRenderer.setLabelsTextSize(40); //축 라벨 사이즈
        multiRenderer.setLegendTextSize(70); //밑에 구분자 글자 사이즈
        multiRenderer.setBarWidth(100);
        multiRenderer.setZoomButtonsVisible(true);
        multiRenderer.setPanEnabled(true, false);//오른쪽으로 움직임.
        multiRenderer.setClickEnabled(false);
        multiRenderer.setZoomEnabled(false, false);
        multiRenderer.setShowGridY(false);
        multiRenderer.setFitLegend(true);
        multiRenderer.setShowGrid(false);
        multiRenderer.setZoomEnabled(false);
        multiRenderer.setExternalZoomEnabled(false);
        multiRenderer.setAntialiasing(true);
        multiRenderer.setInScroll(true);
        multiRenderer.setLegendHeight(40);
        multiRenderer.setXLabelsAlign(Paint.Align.CENTER);
        multiRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);
        multiRenderer.setYLabels(20);//Y축 값에 따라 나누어지는 것
        multiRenderer.setYAxisMax(1000);
        multiRenderer.setXAxisMin(-0.5); //x축 막대 표시 와 왼쪽 라인의 떨어지는 값
        multiRenderer.setXAxisMax(4);//x축 보여주는 수!
        multiRenderer.setBarSpacing(0.5f);
        multiRenderer.setBackgroundColor(Color.TRANSPARENT);//차트 배경화면
        multiRenderer.setMarginsColor(Color.WHITE); //바깍쪽 마진 배경화면
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setShowGridX(true); //X축 선 보여주기!

        multiRenderer.setMargins(new int[]{200, 200, 150, 30});

        //x축 값 표시
        for (int i = 0; i < DBcursor.getCount(); i++) {
            multiRenderer.addXTextLabel(i, mDay.get(i));
        }


        multiRenderer.addSeriesRenderer(CalRenderer);
        multiRenderer.addSeriesRenderer(DistanceRenderer);


        rootLayout = (LinearLayout) findViewById(R.id.llBody);
        View view = ChartFactory.getBarChartView(this, dataset, multiRenderer, BarChart.Type.DEFAULT);
        rootLayout.addView(view);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Graph Page", // TODO: Define a title for the content shown.
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
                "Graph Page", // TODO: Define a title for the content shown.
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
}
