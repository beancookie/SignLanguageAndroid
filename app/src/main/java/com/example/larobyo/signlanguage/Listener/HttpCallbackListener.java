package com.example.larobyo.signlanguage.Listener;

import com.example.larobyo.signlanguage.domain.Word;

import java.util.List;

public interface HttpCallbackListener {
    void onFinish(List<Word> wordList);
    void onError(Exception e);
}
