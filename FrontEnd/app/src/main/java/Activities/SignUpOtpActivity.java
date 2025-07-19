package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zalo_mini.R;

public class SignUpOtpActivity extends AppCompatActivity {
    EditText edtEmailOtp;
    EditText edtPhoneOtp;
    Button btnVerifyOtp;
    TextView tvGoToLogin;

    @Override
    protected  void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.actitvity_signup_otp);

        edtEmailOtp =findViewById(R.id.edtEmailOtp);
        edtPhoneOtp = findViewById(R.id.edtPhoneOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

    btnVerifyOtp.setOnClickListener(view->{
        String emailOtp =edtEmailOtp.getText().toString();
        String phoneOtp = edtPhoneOtp.getText().toString();
        //xử lý logic jwt và api ở dây
    });

    tvGoToLogin.setOnClickListener(view->{
        Intent intent = new Intent(SignUpOtpActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    });
    }
}
