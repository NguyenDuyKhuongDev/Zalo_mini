package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zalo_mini.R;

public class SignUpActivity extends AppCompatActivity {
    EditText edtEmail, edtPhone;
    Button btnSignUp;
    TextView tvGoToLogin;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_signup);

        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnSignUp.setOnClickListener(view -> {
            String email = edtEmail.getText().toString();
            String phoneNumber = edtPhone.getText().toString();
            //thêm logic xử lý với jwwt và api ỏ đay
        });

        tvGoToLogin.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}


