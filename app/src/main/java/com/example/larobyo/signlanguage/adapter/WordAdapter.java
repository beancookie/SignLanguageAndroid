package com.example.larobyo.signlanguage.adapter;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.larobyo.signlanguage.R;
import com.example.larobyo.signlanguage.domain.Word;
import com.example.larobyo.signlanguage.support.DBSupport;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {
    private static final String TAG = "WordAdapter";
    private DBSupport dbSupport;
    private static OkHttpClient client = new OkHttpClient();
    private List<Word> wordList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView chineseName;
        TextView description;
        ImageView picture;
        ImageButton feedback;
        ImageButton collection;

        public ViewHolder(final View itemView) {
            super(itemView);
            chineseName = (TextView) itemView.findViewById(R.id.chinese_name);
            description = (TextView) itemView.findViewById(R.id.description);
            picture = (ImageView) itemView.findViewById(R.id.picture);
            feedback = (ImageButton) itemView.findViewById(R.id.feedback_button);
            feedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(itemView.getContext());
                    dialog.setTitle("问题反馈");
                    dialog.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(itemView.getContext(), "提交成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    EditText questionEdit = new EditText(itemView.getContext());
                    questionEdit.setHint("发现的问题:");
                    dialog.setView(questionEdit);
                    dialog.show();
                }
            });
            collection = (ImageButton) itemView.findViewById(R.id.collection_button);
        }
    }

    public WordAdapter(List<Word> wordList) {
        this.wordList = wordList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.word_item, parent, false);
        dbSupport = new DBSupport(parent.getContext(), "WordStore.db", null, 2);
        return new ViewHolder(view);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Void> {
        private ImageView picture;
        private Bitmap bitmap;

        public DownloadImageTask(ImageView picture) {
            this.picture = picture;
        }

        @Override
        protected Void doInBackground(String... params) {
            String pictureURL = params[0];
            if (pictureURL != null) {
                Request request = new Request.Builder().url(pictureURL).build();
                InputStream in = null;
                try {
                    in = client.newCall(request).execute().body().byteStream();
                    bitmap = BitmapFactory.decodeStream(in);
                } catch (IOException e) {
                    Log.i(TAG, "下载图片失败, e = " + e.getMessage());
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException e) {
                        Log.i(TAG, "下载图片失败, e = " + e.getMessage());
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            picture.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Word word = wordList.get(position);
        holder.chineseName.setText(word.getChineseName());
        holder.description.setText(word.getDescription());
        new DownloadImageTask(holder.picture).execute(word.getPictureUrl());
        final SQLiteDatabase db = dbSupport.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put("chineseName", word.getChineseName());
        values.put("description", word.getDescription());
        values.put("pictureUrl", word.getPictureUrl());
        try {
            db.insert("Word", null, values);
        } catch (SQLiteConstraintException e) {
            Log.i(TAG, word + "已经存在");
        }
        values.clear();
        Log.i(TAG, "保存" + word + "成功");
        holder.collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.execSQL("UPDATE Word SET isCollected = 1 WHERE chineseName = ?", new String[]{word.getChineseName()});
                Log.i(TAG, "收藏" + word + "成功");
                Toast.makeText(holder.itemView.getContext(), "收藏成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }
}
