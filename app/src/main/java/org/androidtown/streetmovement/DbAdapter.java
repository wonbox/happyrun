package org.androidtown.streetmovement;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * 데이터베이스 접속 코드
 */
public class DbAdapter extends CursorAdapter {
    public DbAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.activity_list,parent,false);

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView Text = (TextView) view.findViewById(R.id.textView);
        Text.setText("날짜: " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))) +
                " " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2) )) +"h/m \n"
        + "이동거리: " + cursor.getDouble(cursor.getColumnIndex(cursor.getColumnName(3))) +"Km  " +
           "칼로리: " + cursor.getDouble(cursor.getColumnIndex(cursor.getColumnName(5) ))+ "Kcal   " +
                "걷기 시간: " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(4)))+ "m/s");


    }
}
