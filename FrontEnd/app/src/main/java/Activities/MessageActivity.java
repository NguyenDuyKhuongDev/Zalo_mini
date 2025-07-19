package Activities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/*import com.bumptech.glide.Glide;*/
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

    // UI Elements
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private RecyclerView recyclerViewMessages;
    private ImageView imageAvatar;
    private TextView textUsername;
    private TextView textStatus;

    // Adapter & data
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();

    // SignalR
    private HubConnection hubConnection;

    // Fake user data (có thể truyền qua Intent sau)
    private final String currentUserId = "user_123";
    private final String recipientId = "user_456";
    private final String recipientName = "Anny Peter";
    private final String recipientAvatarUrl = "https://i.pravatar.cc/150?img=5";  // demo avatar
    private final String recipientStatus = "Online";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Bind view
        bindViews();

        // Set user info to header
        textUsername.setText(recipientName);
        textStatus.setText(recipientStatus);
        Glide.with(this).load(recipientAvatarUrl).into(imageAvatar);

        // Setup RecyclerView
        adapter = new MessageAdapter(messageList);
        recyclerViewMessages.setAdapter(adapter);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));

        // Setup SignalR
        hubConnection = SignalrService.GetInstance();
        if (hubConnection == null) {
            Log.e("MessageActivity", "hubConnection is null ");
        }

        // Listen for incoming messages
        hubConnection.on("ReceiveMessage", (userId, messageContent) -> {
            runOnUiThread(() -> {
                Message msg = new Message();
                msg.setSenderId(userId);
                msg.setUsername(recipientName);
                msg.setAvatarUrl(recipientAvatarUrl);
                msg.setContent(messageContent);
                msg.setSendAt(getCurrentTime());

                messageList.add(msg);
                adapter.notifyItemInserted(messageList.size() - 1);
                recyclerViewMessages.scrollToPosition(messageList.size() - 1);
            });
        }, String.class, String.class);

        // Send button
        buttonSend.setOnClickListener(v -> {
            String content = editTextMessage.getText().toString().trim();
            if (!content.isEmpty()) {
                // Gửi tin nhắn lên server
                hubConnection.send("SendMessage", currentUserId, content);

                // Hiển thị ngay trên UI (phản hồi nhanh)
                Message msg = new Message();
                msg.setSenderId(currentUserId);
                msg.setUsername("You");
                msg.setAvatarUrl(null); // avatar của bạn (nếu có)
                msg.setContent(content);
                msg.setSendAt(getCurrentTime());

                messageList.add(msg);
                adapter.notifyItemInserted(messageList.size() - 1);
                recyclerViewMessages.scrollToPosition(messageList.size() - 1);

                editTextMessage.setText("");
            }
        });
    }

    private void bindViews() {
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        imageAvatar = findViewById(R.id.imageAvatar);
        textUsername = findViewById(R.id.textUsername);
        textStatus = findViewById(R.id.textStatus);
    }

    @RequiresApi(36)
    private String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (hubConnection != null && hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
            hubConnection.start().subscribe(
                    () -> Log.d("MessageActivity", "hubConnection reconnect successfully in onStart()"),
                    error -> Log.e("MessageActivity", "hubConnection reconnect failed: " + error.getMessage())
            );
        }
    }
}
