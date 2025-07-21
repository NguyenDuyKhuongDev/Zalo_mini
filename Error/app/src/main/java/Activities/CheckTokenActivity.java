package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import Helpers.SharedCurrentAccountManager;
import Helpers.SharedPrefTokenManager;
import Models.AuthResponse;
import Services.ApiClient;
import Services.ApiServices;
import retrofit2.Call;
import retrofit2.Response;

public class CheckTokenActivity extends AppCompatActivity {
    private String phoneNumber;
    private SharedPrefTokenManager sharedPrefTokenManager;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        phoneNumber = getIntent().getStringExtra("phone_number");
        if (phoneNumber == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        sharedPrefTokenManager = new SharedPrefTokenManager(this, phoneNumber);
        checkTokenValidity();
    }

    private void checkTokenValidity() {
        long currentTime = System.currentTimeMillis();
        long accessTokenExp = sharedPrefTokenManager.getTokenExpKey();
        long refreshTokenExp = sharedPrefTokenManager.getRefreshTokenExpKey();

        if (currentTime < accessTokenExp) {
            startMainActivity();
        } else if (currentTime < refreshTokenExp) {
            refreshAccessToken();
        } else {
            goToLogin();
        }
    }

    private void refreshAccessToken() {
        String refreshToken = sharedPrefTokenManager.getRefreshToken();

        ApiServices apiServices = ApiClient.getClient(this, phoneNumber).create(ApiServices.class);
        Call<AuthResponse> call = apiServices.RefreshToken(refreshToken);

        call.enqueue(new retrofit2.Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    sharedPrefTokenManager.saveTokens(
                            authResponse.getAccessToken(),
                            authResponse.getRefreshToken(),
                            authResponse.getAccessTokenExp(),
                            authResponse.getRefreshTokenExp()
                    );
                    startMainActivity();
                } else {
                    goToLogin();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                goToLogin();
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToLogin() {
        sharedPrefTokenManager.clearTokens();
        SharedCurrentAccountManager currentAccountManager = new SharedCurrentAccountManager(this);
        currentAccountManager.removePhoneNumber(phoneNumber);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}

