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
    private void forgotPassword(){
        dialog = new Dialog(SkyChatActivity.this);
        dialog.setContentView(R.layout.custom_dialog_fogot_password);
        Drawable customBackground  = ContextCompat.getDrawable(this, R.drawable.dialog_backgroud);
        dialog.getWindow().setBackgroundDrawable(customBackground);
        successBtn = dialog.findViewById(R.id.successBtn);
        inputEmailForgot = dialog.findViewById(R.id.inputEmailForgot);
        cancelBtn = dialog.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });
        successBtn.setOnClickListener(v -> {
            String inputEmail = inputEmailForgot.getText().toString().trim();
            firebaseAuth.sendPasswordResetEmail(inputEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    showToast("Kiểm tra hộp thư Email của bạn");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showToast("Lỗi khi gửi link tới Email của bạn hoặc sai địa chỉ" + e.getMessage());
                }
            });
            dialog.dismiss();

        });
        dialog.show();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private Boolean isValidSignInDetails() {
        if (inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Nhập email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
            showToast("Hãy nhập email hợp lệ");
            return false;
        } else if (inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Nhập mật khẩu");
            return false;
        }  else if (inputPassword.getText().toString().trim().length() <= 6) {
            showToast("Mật khẩu phải có độ dài trên 6 ký tự");
            return false;
        } else {
            return true;
        }
    }
    private void loading(Boolean isLoading) {
        if (isLoading) {
            loginBtn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }
    void setContent(){
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        loginPhoneBtn = findViewById(R.id.loginPhoneBtn);
        CreateNewAccountBtn = findViewById(R.id.CreateNewAccountBtn);
        seePassword = findViewById(R.id.seePassword);
        loginBtn = findViewById(R.id.loginBtn);
        progressBar = findViewById(R.id.progressBar);
        forgotPassword = findViewById(R.id.forgotPassword);
    }
}