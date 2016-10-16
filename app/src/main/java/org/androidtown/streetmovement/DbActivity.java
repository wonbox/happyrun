package org.androidtown.streetmovement;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 *
 * 현재 까지 기록을 보여주기 위한 코드
*/
public class DbActivity extends Activity {
    Database dbManager;
    ArrayList<Integer> DBinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
        DBinfo = new ArrayList<Integer>();
        final ListView listView = (ListView) findViewById(R.id.ListView);
        dbManager = new Database(getApplicationContext(), "Run.db", null, 1);
        listView.setAdapter(new DbAdapter(DbActivity.this, dbManager.PrintData()));


        readDB();
        //데이터를 길게 클릭시
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new android.app.AlertDialog.Builder(DbActivity.this).setTitle("삭제").setMessage("정말 기록을 삭제하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbManager.delete("DELETE FROM RUN_LIST WHERE _id =" + DBinfo.get(position)+ ";");
                                readDB();
                                listView.setAdapter(new DbAdapter(DbActivity.this, dbManager.PrintData()));
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                return true;
            }
        });

        //데이터 클릭시
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(getApplicationContext() , MapActivity.class);
                intent.putExtra("id",DBinfo.get(position));
                startActivity(intent);

            }
        });


    }

    //listView의 position 값과 DB id 값 일치 시키기 위해
    private void readDB() {
        DBinfo.clear();
        Cursor DBcursor;
        DBcursor = dbManager.PrintData();
        while (DBcursor.moveToNext()) {
            DBinfo.add(DBcursor.getInt(DBcursor.getColumnIndex(DBcursor.getColumnName(0))));
        }

    }

 }
