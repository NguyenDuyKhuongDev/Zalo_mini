package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zalo_mini.R;

import Helpers.SharedPrefTokenManager;
import Models.AuthRequest;
import Models.AuthResponse;
import Services.ApiClient;
import Services.ApiServices;
import retrofit2.Call;

public class LoginOtpActivity extends AppCompatActivity {
    EditText edtEmailOtp;
    Button btnVerifyOtp;
    TextView tvGoToLogin;
    SharedPrefTokenManager sharedPrefTokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        edtEmailOtp = findViewById(R.id.edtEmailOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);


        btnVerifyOtp.setOnClickListener(view -> {
            String emailOtp = edtEmailOtp.getText().toString();
            String phoneNumber = getIntent().getStringExtra("phoneNumber");
            String email = getIntent().getStringExtra("email");

            if (emailOtp.isEmpty()) {
                edtEmailOtp.setError("Vui lòng nhập mã OTP");
                return;
            }
            if (phoneNumber.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Error Occur ", Toast.LENGTH_SHORT).show();
                return;
            }
            sharedPrefTokenManager = new SharedPrefTokenManager(this,phoneNumber);

            AuthRequest request = new AuthRequest(email, phoneNumber, emailOtp);

            ApiServices apiServices = ApiClient.getClient(this,phoneNumber).create(ApiServices.class);
            Call<AuthResponse> call = apiServices.Login(request);

            call.enqueue(new retrofit2.Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, retrofit2.Response<AuthResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String refreshToken = response.body().getRefreshToken();
                        String accessToken = response.body().getAccessToken();
                        long accessTokenExp = response.body().getAccessTokenExp();
                        long refreshTokenExp = response.body().getRefreshTokenExp();
                        sharedPrefTokenManager.saveTokens(accessToken, refreshToken, accessTokenExp, refreshTokenExp);

                        Intent intent = new Intent(LoginOtpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(LoginOtpActivity.this, "Otp sai hoặc hết hạn", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    Toast.makeText(LoginOtpActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });

        });
        tvGoToLogin.setOnClickListener(view -> {
            Intent intent = new Intent(LoginOtpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });


    }

}
