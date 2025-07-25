package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.PreferenceManager;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class SplashActivity extends AppCompatActivity {
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferenceManager = new PreferenceManager(getApplicationContext());


        if (FirebaseUtil.isLoggedIn() && getIntent().getExtras()!=null){
            //notification
//            String userId = getIntent().getExtras().getString("userId");
            String userId = FirebaseUtil.currentUserID();
                FirebaseUtil.allUserCollectionReference().document(userId).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                preferenceManager.putString(FirebaseUtil.KEY_USER_ID, documentSnapshot.getId());
                                preferenceManager.putString(FirebaseUtil.KEY_USER_NAME, documentSnapshot.getString(FirebaseUtil.KEY_USER_NAME));
                                preferenceManager.putString(FirebaseUtil.KEY_PHONE, documentSnapshot.getString(FirebaseUtil.KEY_PHONE));
                                preferenceManager.putString(FirebaseUtil.KEY_TOKEN, documentSnapshot.getString(FirebaseUtil.KEY_TOKEN));
                                UserModel model = task.getResult().toObject(UserModel.class);
                                Intent mainIntent = new Intent(this, MainActivity.class);
                                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(mainIntent);
                                                                              //ChatActivity
                                Intent intent = new Intent(this, MainActivity.class);
                                AndroidUtil.passUserModelAsIntent(intent, model);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
            }else
            {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (FirebaseUtil.isLoggedIn()){
                        String userId = FirebaseUtil.currentUserID();
                        FirebaseUtil.allUserCollectionReference().document(userId).get()
                                .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                preferenceManager.putString(FirebaseUtil.KEY_USER_ID, documentSnapshot.getId());
                                                preferenceManager.putString(FirebaseUtil.KEY_USER_NAME, documentSnapshot.getString(FirebaseUtil.KEY_USER_NAME));
                                                preferenceManager.putString(FirebaseUtil.KEY_PHONE, documentSnapshot.getString(FirebaseUtil.KEY_PHONE));
                                                preferenceManager.putString(FirebaseUtil.KEY_TOKEN, documentSnapshot.getString(FirebaseUtil.KEY_TOKEN));
                                            }
                                        });
                        startActivity(new Intent(SplashActivity.this , MainActivity.class));
                    }else {
                        startActivity(new Intent(SplashActivity.this , SkyChatActivity.class));

                    }
                    finish();
                }
            }, 1500);
        }
    }


}
