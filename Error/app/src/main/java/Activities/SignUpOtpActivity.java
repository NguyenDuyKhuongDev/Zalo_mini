package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zalo_mini.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import Helpers.SharedPrefTokenManager;
import Models.AuthRequest;
import Models.AuthResponse;
import Services.ApiClient;
import Services.ApiServices;
import retrofit2.Call;
import retrofit2.Response;

public class SignUpOtpActivity extends AppCompatActivity {
    EditText edtEmailOtp;
    EditText edtPhoneOtp;
    Button btnVerifyOtp;
    private String verificationId, phoneNumber, email;
    private FirebaseAuth firebaseAuth;
    TextView tvGoToLogin;
    SharedPrefTokenManager sharedPrefTokenManager;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.actitvity_signup_otp);

        firebaseAuth = FirebaseAuth.getInstance();
        edtEmailOtp = findViewById(R.id.edtEmailOtp);
        edtPhoneOtp = findViewById(R.id.edtPhoneOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);


        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");
        email = intent.getStringExtra("email");
        sharedPrefTokenManager = new SharedPrefTokenManager(this,phoneNumber);
        sendPhoneVerification(phoneNumber);

        btnVerifyOtp.setOnClickListener(v -> {
            String phoneOtp = edtPhoneOtp.getText().toString();
            String emailOtp = edtEmailOtp.getText().toString();
            if (phoneOtp.isEmpty() || phoneOtp.length() < 6 || emailOtp.isEmpty()) {
                edtPhoneOtp.setError("Otp không hợp lệ");
                return;
            }

            verifyPhoneOtp(phoneOtp);

        });


        tvGoToLogin.setOnClickListener(view -> {
            Intent intentReturn = new Intent(SignUpOtpActivity.this, LoginActivity.class);
            startActivity(intentReturn);
            finish();
        });
    }

    private void sendPhoneVerification(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    edtPhoneOtp.setText(phoneAuthCredential.getSmsCode());
                    signInWithCredential(phoneAuthCredential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(SignUpOtpActivity.this, "FireBase phone auth error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCodeSent(@NonNull String verId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    verificationId = verId;
                    Toast.makeText(SignUpOtpActivity.this, "Đã gửi OTP tới: " + phoneNumber, Toast.LENGTH_SHORT).show();
                }
            };

    private void verifyPhoneOtp(String code) {
        if (verificationId == null) {
            Toast.makeText(this, "Otp chưa được gửi ,hãy chờ 1 chút..", Toast.LENGTH_SHORT).show();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);

    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                       String emailOtp =edtPhoneOtp.getText().toString();
                        AuthRequest request = new AuthRequest(phoneNumber, email, emailOtp);
                        register(request);
                    } else {
                        Toast.makeText(SignUpOtpActivity.this, "Xác thực số điện thoại thất bại", Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void register(AuthRequest request) {
        ApiServices apiServices = ApiClient.getClient(this).create(ApiServices.class);
        Call<AuthResponse> call = apiServices.Register(request);

        call.enqueue(new retrofit2.Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String refreshToken = response.body().getRefreshToken();
                    String accessToken = response.body().getAccessToken();
                    long acsessTokenExp = response.body().getAccessTokenExp();
                    long refreshTokenExp = response.body().getRefreshTokenExp();
                    sharedPrefTokenManager.saveTokens(accessToken, refreshToken, acsessTokenExp, refreshTokenExp);

                    Intent intent = new Intent(SignUpOtpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignUpOtpActivity.this, "Lỗi đăng ký", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(SignUpOtpActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


