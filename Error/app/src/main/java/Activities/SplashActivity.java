package Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zalo_mini.R;

import java.util.ArrayList;
import java.util.List;

import Helpers.CurrentAccountAdapter;
import Helpers.SharedCurrentAccountManager;
import Helpers.SharedPrefTokenManager;

public class SplashActivity extends AppCompatActivity {
    private RecyclerView recyclerViewAccounts;
    private Button btnAddAccount;
    private List<String> phoneNumbers;
    private CurrentAccountAdapter currentAccountAdapter;
    private SharedCurrentAccountManager currentAccountManager;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_account_selector);

        recyclerViewAccounts = findViewById(R.id.recyclerAccounts);
        currentAccountManager = new SharedCurrentAccountManager(this);
        phoneNumbers = currentAccountManager.loadCurrentPhoneNumbers();

        if (phoneNumbers.isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        currentAccountAdapter = new CurrentAccountAdapter(phoneNumbers, phoneNumber -> {
            Intent intent = new Intent(this, CheckTokenActivity.class);
            intent.putExtra("phoneNumber", phoneNumber);
            startActivity(intent);
            finish();
        });

        recyclerViewAccounts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAccounts.setAdapter(currentAccountAdapter);

        findViewById(R.id.btnAddAccount).setOnClickListener(v->{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

    }
}
