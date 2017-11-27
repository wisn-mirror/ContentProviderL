package com.wisn.contentproviderl;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.wisn.contentproviderl.adapter.MyAdapter;
import com.wisn.contentproviderl.bean.Student;
import com.wisn.contentproviderl.observer.PersonOberserver;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ContentResolver contentResolver;
    private ListView lvShowInfo;
    private MyAdapter adapter;
    private Button btnInit;
    private Button btnInsert;
    private Button btnDelete;
    private Button btnUpdate;
    private Button btnQuery;
    private Cursor cursor;

    private static final String AUTHORITY = "com.example.studentProvider";
    private static final Uri STUDENT_ALL_URI = Uri.parse("content://" + AUTHORITY + "/student");
    protected static final String TAG = "MainActivity";

    private Handler handler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            //在此我们可以针对数据改变后做一些操作，比方说Adapter.notifyDataSetChanged()等，根据业务需求来定。。
            cursor = contentResolver.query(STUDENT_ALL_URI, null, null, null,null);
            adapter.changeCursor(cursor);
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvShowInfo=(ListView) findViewById(R.id.lv_show_info);
        initData();
    }

    private void initData() {
        btnInit=(Button) findViewById(R.id.btn_init);
        btnInsert=(Button) findViewById(R.id.btn_insert);
        btnDelete=(Button) findViewById(R.id.btn_delete);
        btnUpdate=(Button) findViewById(R.id.btn_update);
        btnQuery=(Button) findViewById(R.id.btn_query);

        btnInit.setOnClickListener(this);
        btnInsert.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnQuery.setOnClickListener(this);

        contentResolver = getContentResolver();
        //注册内容观察者
        contentResolver.registerContentObserver(STUDENT_ALL_URI,true,new PersonOberserver(handler));

        adapter=new MyAdapter(MainActivity.this,cursor);
        lvShowInfo.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //初始化
            case R.id.btn_init:
                ArrayList<Student> students = new ArrayList<Student>();

                Student student1 = new Student("AA",25,"aaaaaaa");
                Student student2 = new Student("BB",26,"bbbbbbb");
                Student student3 = new Student("CC",27,"ccccccc");
                Student student4 = new Student("DD",28,"ddddddd");
                Student student5 = new Student("EE",29,"eeeeeee");

                students.add(student1);
                students.add(student2);
                students.add(student3);
                students.add(student4);
                students.add(student5);

                for (Student Student : students) {
                    ContentValues values = new ContentValues();
                    values.put("name", Student.getName());
                    values.put("age", Student.getAge());
                    values.put("introduce", Student.getIntroduce());
                    contentResolver.insert(STUDENT_ALL_URI, values);
                }
                break;

            //增
            case R.id.btn_insert:

                Student student = new Student("旺仔", 26, "帅气");

                //实例化一个ContentValues对象
                ContentValues insertContentValues = new ContentValues();
                insertContentValues.put("name",student.getName());
                insertContentValues.put("age",student.getAge());
                insertContentValues.put("introduce",student.getIntroduce());

                //这里的uri和ContentValues对象经过一系列处理之后会传到ContentProvider中的insert方法中，
                //在我们自定义的ContentProvider中进行匹配操作
                contentResolver.insert(STUDENT_ALL_URI,insertContentValues);
                break;

            //删
            case R.id.btn_delete:

                //删除所有条目
                contentResolver.delete(STUDENT_ALL_URI, null, null);
                //删除_id为1的记录
                Uri delUri = ContentUris.withAppendedId(STUDENT_ALL_URI, 1);
                contentResolver.delete(delUri, null, null);
                break;

            //改
            case R.id.btn_update:

                ContentValues contentValues = new ContentValues();
                contentValues.put("introduce","26更改");
                //更新数据，将age=26的条目的introduce更新为"性感"，原来age=26的introduce为"大方".
                //生成的Uri为：content://com.example.studentProvider/student/26
                Uri updateUri = ContentUris.withAppendedId(STUDENT_ALL_URI,26);
                contentResolver.update(updateUri,contentValues, null, null);

                break;

            //查
            case R.id.btn_query:
                //通过ContentResolver获得一个调用ContentProvider对象
                Cursor cursor = contentResolver.query(STUDENT_ALL_URI, null, null, null,null);
                adapter=new MyAdapter(MainActivity.this,cursor);
                lvShowInfo.setAdapter(adapter);
                cursor = contentResolver.query(STUDENT_ALL_URI, null, null, null,null);
                adapter.changeCursor(cursor);
                break;
        }
    }
}