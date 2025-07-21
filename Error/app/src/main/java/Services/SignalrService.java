package Services;

import android.util.Log;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

public class SignalrService {
    private static HubConnection hubConnection;

    public static HubConnection GetInstance() {
        if (hubConnection == null) {
            hubConnection = HubConnectionBuilder
                    //đây là setting đường link cho máy ảo nên dùng 10.0.2.2
                    //nó yêu cầu https thay vì http
                    .create("http://10.0.2.2:5285/chatHub")
                    .build();

            hubConnection.start().subscribe(
                    () -> Log.d("SignalR", "Connected"),
                    error -> Log.e("SignalR", "Connection failed", error)
            );


        }
        return hubConnection;
    }
}
