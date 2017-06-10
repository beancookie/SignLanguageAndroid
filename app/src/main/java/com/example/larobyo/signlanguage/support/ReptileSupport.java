package com.example.larobyo.signlanguage.support;

import android.util.Log;

import com.example.larobyo.signlanguage.Listener.HttpCallbackListener;
import com.example.larobyo.signlanguage.domain.Word;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReptileSupport {
    private static final String TAG = "ReptileSupport";
    private static final String URL = "http://shouyu.z12345.com";

    public static void getContent(final String searchURL, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Word> wordList = new ArrayList<>();
                try {
                    Document document = Jsoup.connect(searchURL).get();
                    Elements trs = document.getElementsByClass("sign").get(0).getElementsByTag("tr");
                    for (int i = 1; i < trs.size(); ++i) {
                        Word word = new Word();
                        // 爬取中文名称
                        word.setChineseName(trs.get(i).getElementsByClass("t").get(0).text());
                        // 爬取图片url
                        Element img = trs.get(i).getElementsByClass("i").first();
                        if (img != null) {
                            word.setPictureUrl(URL + img.getElementsByClass("i").get(0).getElementsByTag("img").get(0).attr("src"));
                        }
                        // 爬取动作描述
                        String description = trs.get(i).getElementsByTag("td").get(2).text();
                        if (!description.equals("")) {
                            word.setDescription(description);
                        }
                        wordList.add(word);
                    }
                    if (listener != null) {
                        listener.onFinish(wordList);
                    }
                } catch (IOException e) {
                    Log.i(TAG, "解析页面时发生错误,e = " + e.getMessage());
                    listener.onError(e);
                }
            }
        }).start();

    }
}
