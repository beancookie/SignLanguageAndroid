package com.example.larobyo.signlanguage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import com.example.larobyo.signlanguage.Listener.HttpCallbackListener;
import com.example.larobyo.signlanguage.R;
import com.example.larobyo.signlanguage.adapter.WordAdapter;
import com.example.larobyo.signlanguage.domain.Word;
import com.example.larobyo.signlanguage.support.DBSupport;
import com.example.larobyo.signlanguage.support.ReptileSupport;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {
    private static final int GET_SEARCH_RESULT = 1;
    private DBSupport dbSupport;
    private RecyclerView recyclerView;
    private MaterialSearchView searchView;
    private List<Word> wordList = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_SEARCH_RESULT:
                    WordAdapter wordAdapter = new WordAdapter((List<Word>) msg.obj);
                    recyclerView.setAdapter(wordAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    break;
                default:
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        // 加载收藏的手语
        dbSupport = new DBSupport(this, "WordStore.db", null, 2);
        SQLiteDatabase db = dbSupport.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Word WHERE isCollected = 1", null);
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

        // 设置toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.collection_toolbar);
        setSupportActionBar(toolbar);

        // 设置actionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_white_18dp);
        }

        // 设置图片列表
        recyclerView = (RecyclerView) findViewById(R.id.collection_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final WordAdapter wordAdapter = new WordAdapter(wordList);
        recyclerView.setAdapter(wordAdapter);

        // 设置搜索框
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equals("")) {
                    SQLiteDatabase db = dbSupport.getWritableDatabase();
                    Cursor cursor = db.rawQuery("SELECT * FROM Word WHERE chineseName LIKE ? AND isCollected = 1", new String[]{"%" + query + "%"});
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
                    } else {
                        Toast.makeText(CollectionActivity.this, "您还没有收藏这个词汇", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
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
