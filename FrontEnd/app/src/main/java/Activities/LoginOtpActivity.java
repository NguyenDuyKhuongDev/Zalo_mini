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
import Models.LoginRequest;
import Models.LoginResponse;
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
        sharedPrefTokenManager = new SharedPrefTokenManager(this);

        btnVerifyOtp.setOnClickListener(view -> {
            String emailOtp = edtEmailOtp.getText().toString();
            String phoneNumber = getIntent().getStringExtra("phoneNumber");
            String email = getIntent().getStringExtra("email");

            if (emailOtp.isEmpty()) edtEmailOtp.setError("Vui lòng nhập mã OTP");
            if (phoneNumber.isEmpty() || email.isEmpty())
                Toast.makeText(this, "Error Occur ", Toast.LENGTH_SHORT).show();

            LoginRequest request = new LoginRequest(email, phoneNumber, emailOtp);

            ApiServices apiServices = ApiClient.getClient(this).create(ApiServices.class);
            Call<LoginResponse> call = apiServices.Login(request);

            call.enqueue(new retrofit2.Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String refreshToken = response.body().getRefreshToken();
                        String acessToken = response.body().getAcessToken();

                        sharedPrefTokenManager.saveTokens(acessToken, refreshToken);

                        Intent intent = new Intent(LoginOtpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(LoginOtpActivity.this, "Otp sai hoặc hết hạn", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
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
