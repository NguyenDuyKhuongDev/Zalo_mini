package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.model.PreferenceManager;
import com.example.myapplication.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class SkyChatActivity extends AppCompatActivity {
  EditText inputEmail, inputPassword, inputEmailForgot;
    TextView loginPhoneBtn, CreateNewAccountBtn, forgotPassword,cancelBtn, successBtn;
    AppCompatImageView seePassword;
    Button loginBtn;
    ProgressBar progressBar;
    private PreferenceManager preferenceManager;
    FirebaseAuth firebaseAuth;
    private int seePass = 0;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sky_chat);
        firebaseAuth = FirebaseAuth.getInstance();
        setContent();
        preferenceManager = new PreferenceManager(getApplicationContext());
        listenNer();

    }

    void listenNer(){
        loginBtn.setOnClickListener(v -> {
            loading(true);
            if (isValidSignInDetails()) {

                signIn();
            }else {
                loading(false);
            }

        });
        forgotPassword.setOnClickListener(v -> {
            forgotPassword();
        });
        loginPhoneBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginPhoneNumberActivity.class));
        });
        CreateNewAccountBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
        seePassword.setOnClickListener(v -> {
            if (seePass == 0) {
                inputPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                seePass = 1;
            } else {
                inputPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                seePass = 0;
            }
        });
    }
    private void signIn(){
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    AuthResult authResult = task.getResult();
                    FirebaseUser user_new = authResult.getUser();
                    String userId = user_new.getUid();
                    preferenceManager.putString(FirebaseUtil.KEY_USER_ID, userId);
                    preferenceManager.putString(FirebaseUtil.KEY_EMAIL, inputEmail.getText().toString());
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else {
                    showToast("Sai mật khẩu");
                    loading(false);
                }

            }
        });
   
}