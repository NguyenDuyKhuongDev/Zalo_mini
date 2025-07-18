package Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zalo_mini.R;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import Models.Message;
import Services.SignalrService;

public class MessageActivity extends AppCompatActivity {
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private RecyclerView recyclerViewMessages;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private HubConnection hubConnection;

    @RequiresApi(api = 36) // nếu dưới 36 thì lỗi LocalDateTime.now()
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_chat);

        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        adapter = new MessageAdapter(messageList);
        recyclerViewMessages.setAdapter(adapter);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this
        ));

        LocalDateTime now = LocalDateTime.now();
        String formatted = now.format(DateTimeFormatter.ofPattern("ss:mm:HH dd/MM/yyyy"));

        hubConnection = SignalrService.GetInstance();
        if (hubConnection == null) Log.e("MessageActivity", "hubConnection is null ");

        hubConnection.on("ReceiveMessage", (userId, messageContent) -> {
            runOnUiThread(() -> {
                Message msg = new Message();
                msg.setSenderId(userId);
                msg.setContent(messageContent);
                msg.setSendAt(formatted);
                messageList.add(msg);
                adapter.notifyItemInserted(messageList.size() - 1);
                recyclerViewMessages.scrollToPosition(messageList.size() - 1);
            });
        }, String.class, String.class);

        buttonSend.setOnClickListener(v -> {
            String content = editTextMessage.getText().toString().trim();
            if (!content.isEmpty()) {
                hubConnection.send("SendMessage", "user_123", content);
                editTextMessage.setText("");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (hubConnection != null && hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
            Log.d("MessageActivity", "hubConnection is disconnected");
            hubConnection.start().subscribe(
                    () -> Log.d("MessageActivity", "hubConnection reconnect sucessfully in onStart()"),
                    error -> Log.e("MessageActivity", "hubConnection reconnect failed in onStart(): " + error.getMessage())
            );
        }
    }
}
