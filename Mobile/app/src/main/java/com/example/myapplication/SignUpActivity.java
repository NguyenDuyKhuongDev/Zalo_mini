package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.model.PreferenceManager;
import com.example.myapplication.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Field;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    ImageButton back_btn;
    TextView LoginAccountBtn;
    Button CreateAccountBtn;
    ProgressBar progressBar;
    EditText inputEmail, inputPassword, inputConfirmPassword, inputName;
    AppCompatImageView seeConfirmPassword, seePassword;
    private PreferenceManager preferenceManager;
    FirebaseAuth firebaseAuth;
    private int seePass = 0, seeConfirmPass = 0;

    // khai báo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), SkyChatActivity.class));
            finish();
        }
        setContent();
        listenNer();
    }
    void listenNer(){
        CreateAccountBtn.setOnClickListener(v -> {
            loading(true);
            if (isValidSignUpDetails()){
                signUp();
            }else
            {
                loading(false);
            }

        });
        back_btn.setOnClickListener(v -> {
            onBackPressed();
        });
        LoginAccountBtn.setOnClickListener(v -> {
            onBackPressed();
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
        seeConfirmPassword.setOnClickListener(v -> {
            if (seeConfirmPass == 0) {
                inputConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                seeConfirmPass = 1;
            } else {
                inputConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                seeConfirmPass = 0;
            }
        });
    }

    private void signUp(){
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String name = inputName.getText().toString();
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        AuthResult authResult = task.getResult();
                        FirebaseUser user_new = authResult.getUser();
                        String userId = user_new.getUid();

                        HashMap<String, Object> user = new HashMap<>();
                        user.put(FirebaseUtil.KEY_USER_ID, userId);
                        user.put(FirebaseUtil.KEY_EMAIL, email);
                        user.put(FirebaseUtil.KEY_USER_NAME, name);

                        FirebaseUtil.allUserCollectionReference().document(userId).set(user).addOnSuccessListener(documentReference -> {

                            preferenceManager.putString(FirebaseUtil.KEY_USER_ID, userId);
                            preferenceManager.putString(FirebaseUtil.KEY_USER_NAME, inputName.getText().toString());
                            preferenceManager.putString(FirebaseUtil.KEY_EMAIL, inputEmail.getText().toString());
                        });
                        showToast("Đăng ký thành công");
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }else {
                        showToast("Đăng ký thất bại"+ task.getException().getMessage());
                    }
                }
            });

    }
    private Boolean isValidSignUpDetails() {
        if (inputName.getText().toString().trim().isEmpty()) {
            showToast("Nhập tên tài khoản");
            return false;
        } else if (inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Nhập email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
            showToast("Hãy nhập email hợp lệ");
            return false;
        } else if (inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Nhập mật khẩu");
            return false;
        }
        else if (inputPassword.getText().toString().trim().length() <= 6) {
            showToast("Mật khẩu phải có độ dài trên 6 ký tự");
            return false;
        }else if (inputConfirmPassword.getText().toString().trim().isEmpty()) {
            showToast("Nhập lại mật khẩu");
            return false;
        } else if (!inputPassword.getText().toString().equals(inputConfirmPassword.getText().toString())) {
            showToast("Mật khẩu nhập lại không khớp với mật khẩu đã nhập trước đó!");
            return false;
        } else {
            return true;
        }

    }
    void setContent(){
        back_btn = findViewById(R.id.back_btn);
        LoginAccountBtn = findViewById(R.id.LoginAccountBtn);
        CreateAccountBtn = findViewById(R.id.CreateAccountBtn);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        inputName = findViewById(R.id.inputName);
        seeConfirmPassword = findViewById(R.id.seeConfirmPassword);
        seePassword = findViewById(R.id.seePassword);
        progressBar = findViewById(R.id.progressBar);
        preferenceManager = new PreferenceManager(getApplicationContext());
    }
    private void loading(Boolean isLoading) {
        if (isLoading) {
            CreateAccountBtn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            CreateAccountBtn.setVisibility(View.VISIBLE);
        }
    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}