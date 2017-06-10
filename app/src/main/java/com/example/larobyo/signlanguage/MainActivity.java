package com.example.larobyo.signlanguage;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.larobyo.signlanguage.Listener.HttpCallbackListener;
import com.example.larobyo.signlanguage.adapter.WordAdapter;
import com.example.larobyo.signlanguage.domain.Word;
import com.example.larobyo.signlanguage.support.DBSupport;
import com.example.larobyo.signlanguage.support.ReptileSupport;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int GET_SEARCH_RESULT = 1;
    private static final String URL = "http://shouyu.z12345.com/";
    private DBSupport dbSupport;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private MaterialSearchView searchView;
    private ProgressBar progressBar;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_SEARCH_RESULT:
                    WordAdapter wordAdapter = new WordAdapter((List<Word>) msg.obj);
                    recyclerView.setAdapter(wordAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 加载最近搜索的5条记录
        dbSupport = new DBSupport(this, "WordStore.db", null, 2);
        SQLiteDatabase db = dbSupport.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Word ORDER BY id DESC LIMIT 0, 5", null);
        if (cursor.moveToFirst()) {
            List<Word> wordList = new ArrayList<>();
            do {
                Word word = new Word();
                word.setChineseName(cursor.getString(cursor.getColumnIndex("chineseName")));
                word.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                word.setPictureUrl(cursor.getString(cursor.getColumnIndex("pictureUrl")));
                wordList.add(word);
            } while (cursor.moveToNext());
            Message message = new Message();
            message.what = GET_SEARCH_RESULT;
            message.obj = wordList;
            handler.sendMessage(message);
        }

        // 加载toolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // 设置actionBar
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_view_headline_white_18dp);
        }

        // 设置图片列表
        recyclerView = (RecyclerView) findViewById(R.id.word_recycler_view);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 设置搜索框
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                if (!query.equals("")) {
                    ReptileSupport.getContent(URL + "1-" + query + ".html", new HttpCallbackListener() {
                        @Override
                        public void onFinish(List<Word> wordList) {
                            Message message = new Message();
                            message.what = GET_SEARCH_RESULT;
                            message.obj = wordList;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }
                return true;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // 设置左划框
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nva_home:
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_collection:
                        Intent intent = new Intent(MainActivity.this, CollectionActivity.class);
                        startActivity(intent);
                        break;
                    default:
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

}
