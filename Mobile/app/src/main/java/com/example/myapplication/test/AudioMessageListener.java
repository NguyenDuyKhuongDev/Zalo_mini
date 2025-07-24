package com.example.myapplication.test;

import android.net.Uri;
import android.widget.ImageButton;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public interface AudioMessageListener {
    void OnItemAudioClick(Uri uriAudio , TextView timer, ImageButton audioBtn, LottieAnimationView animationView, int position);
}
