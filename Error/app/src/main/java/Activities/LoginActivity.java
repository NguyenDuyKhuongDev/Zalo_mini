package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zalo_mini.R;

public class LoginActivity extends AppCompatActivity {
    EditText edtPhone, edtEmail;
    Button btnLogin;
    TextView tvGoToSignup;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_login);

        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToSignup = findViewById(R.id.tvGoToSignUp);

        btnLogin.setOnClickListener(view -> {
            String phoneNumber = edtPhone.getText().toString();
            String email = edtEmail.getText().toString();

            if (phoneNumber.isEmpty()) {
                edtPhone.setError("Vui lòng nhập số điện thoại");
                return;
            }
            if (email.isEmpty()) {
                edtEmail.setError("Vui lòng nhập email");
                return;
            }
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && phoneNumber.length() >= 9) {
                Intent intent = new Intent(LoginActivity.this, LoginOtpActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("email", email);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Vui lòng nhập đúng định dạng Email và số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }

            //thêm logic xử lý với jwwt và paopi ỏ đay
        });

        tvGoToSignup.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}
