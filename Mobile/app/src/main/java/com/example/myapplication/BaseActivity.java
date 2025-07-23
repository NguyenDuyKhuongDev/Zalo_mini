package com.example.myapplication;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.model.PreferenceManager;
import com.example.myapplication.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

public class BaseActivity extends AppCompatActivity {
    private DocumentReference documentReference;
    private final String UserId = FirebaseUtil.currentUserID();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (UserId == null){
            return;
        }
        documentReference = FirebaseUtil.status(UserId);
    }




    @Override
    protected void onPause() {
        super.onPause();

        documentReference.update("status", "0");
    }

    @Override
    protected void onResume() {
        super.onResume();

        documentReference.update("status", "1");
    }
}
