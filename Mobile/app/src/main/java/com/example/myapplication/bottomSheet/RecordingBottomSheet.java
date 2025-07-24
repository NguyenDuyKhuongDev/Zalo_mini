package com.example.myapplication.bottomSheet;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.myapplication.ChatActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.ChatroomModel;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecordingBottomSheet extends BottomSheetDialogFragment {

    LottieAnimationView animationRecord, animationPlayRecord;
    ImageButton playRecord, pauseRecord, recording, deleteRecord, sendRecording;
    TextView timer, text3, text2, text1;
    RelativeLayout stopRecording;
    ChatroomModel chatroomModel;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    Context context;
    String chatroomId;
    UserModel otherUser;
    String currentUserID = FirebaseUtil.currentUserID();

    public RecordingBottomSheet(Context context, String chatroomId, UserModel otherUser, ChatroomModel chatroomModel) {
        this.context = context;
        this.chatroomId = chatroomId;
        this.otherUser = otherUser;
        this.chatroomModel = chatroomModel;
    }

    String path = null;
    private int seconds = 0;
    private int minutes = 0;

    private  boolean isRecording = false;
    private static final int REQUEST_PERMISSION_CODE = 1000;
    public static final String TAG = "RecordingBottomSheet";


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_recording, container, false);
        animationRecord = view.findViewById(R.id.animationRecord);
        animationPlayRecord = view.findViewById(R.id.animationPlayRecord);
        playRecord = view.findViewById(R.id.playRecord);
        pauseRecord = view.findViewById(R.id.pauseRecord);
        recording = view.findViewById(R.id.recording);
        timer = view.findViewById(R.id.timer);
        deleteRecord = view.findViewById(R.id.delete_Record);
        stopRecording = view.findViewById(R.id.stopRecording);
        text2 = view.findViewById(R.id.text2);
        text3 = view.findViewById(R.id.text3);
        text1 = view.findViewById(R.id.text1);
        sendRecording = view.findViewById(R.id.sendRecording);
        mediaPlayer = new MediaPlayer();

        listenNer();
        return view;
    }


    void listenNer() {
        if (!checkPermissionFromDevice())
        {
            requestPermission();
        }
        recording.setOnClickListener(v -> {
            if (checkPermissionFromDevice()) {
                path = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/" +
                        System.currentTimeMillis() + ".mp3";
                setupMediaRecorder();
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    isRecording = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                requestPermission();
            }
            startTimer();
            recording.setVisibility(View.GONE);
            deleteRecord.setVisibility(View.GONE);
            stopRecording.setVisibility(View.VISIBLE);
            animationRecord.setVisibility(View.VISIBLE);
            playRecord.setVisibility(View.GONE);
            pauseRecord.setVisibility(View.GONE);
            sendRecording.setVisibility(View.GONE);

            text1.setVisibility(View.GONE);
            text2.setVisibility(View.VISIBLE);
            text3.setVisibility(View.GONE);
        });

        sendRecording.setOnClickListener(v -> {
            uploadAudio();
        });
        stopRecording.setOnClickListener(v -> {
            if (isRecording){
                mediaRecorder.stop();
                isRecording = false;
                mediaRecorder.release();

                stopTimer();
                text1.setVisibility(View.VISIBLE);
                text2.setVisibility(View.GONE);
                text3.setVisibility(View.GONE);

                recording.setVisibility(View.GONE);
                deleteRecord.setVisibility(View.VISIBLE);
                stopRecording.setVisibility(View.GONE);
                animationRecord.setVisibility(View.GONE);
                playRecord.setVisibility(View.VISIBLE);
                pauseRecord.setVisibility(View.GONE);
                sendRecording.setVisibility(View.VISIBLE);
            }


        });
        deleteRecord.setOnClickListener(v -> {
            File file = new File(path);
            if (file.exists()) {
                file.delete();

            }
            resetTimer();
            Toast.makeText(context, "Đã xóa bản ghi", Toast.LENGTH_SHORT).show();
            sendRecording.setVisibility(View.GONE);
            recording.setVisibility(View.VISIBLE);
            deleteRecord.setVisibility(View.GONE);
            stopRecording.setVisibility(View.GONE);
            animationRecord.setVisibility(View.GONE);
            playRecord.setVisibility(View.GONE);
            pauseRecord.setVisibility(View.GONE);


            text1.setVisibility(View.GONE);
            text2.setVisibility(View.GONE);
            text3.setVisibility(View.VISIBLE);
        });
        playRecord.setOnClickListener(v -> {
            if (mediaPlayer != null){

                pauseRecord.setVisibility(View.VISIBLE);
                animationRecord.setVisibility(View.VISIBLE);
                playRecord.setVisibility(View.GONE);
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        pauseRecord.setVisibility(View.GONE);
                        playRecord.setVisibility(View.VISIBLE);
                        animationRecord.setVisibility(View.GONE);
                    }
                });
            }

        });

        pauseRecord.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    pauseRecord.setVisibility(View.GONE);
                    playRecord.setVisibility(View.VISIBLE);
                    animationRecord.setVisibility(View.GONE);
                    stopTimer();
                } else {
                    mediaPlayer.start();
                    pauseRecord.setVisibility(View.VISIBLE);
                    playRecord.setVisibility(View.GONE);
                    animationRecord.setVisibility(View.VISIBLE);
                    startTimer();
                }
            }

        });
    }

    private void sendAudios(String audioUrl){
        HashMap<String, Object> message = new HashMap<>();
        message.put("timestamp", Timestamp.now());
        message.put("senderId", currentUserID);
        message.put("message", audioUrl);
        message.put("images", "0");
        message.put("videos", "0");
        message.put("files", "0");
        message.put("audios", "1");
        chatroomModel.setLastMessage("###sendAudio%&*!");
        chatroomModel.setLastMessageSenderId(currentUserID);
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setStatusRead("0");
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(message).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    sendNotification("Đã gửi một tin nhắn thoại cho bạn");
                    dismiss();
                }
            }
        })  .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                showToast("Failed to save video URL to Firestore");
            }
        });
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(path);
    }

    private void uploadAudio(){
        if ( path != null){
            UploadTask uploadTask = FirebaseUtil.putAudios(path).putFile(Uri.fromFile(new File(path)));
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String audioUrl = uri.toString();
                                sendAudios(audioUrl);
                            }
                        });
                    }
                }
            }).addOnFailureListener(e -> {

            });

        }else {
            showToast("Không có bản ghi thoại nào!");
        }


    }
    private final Handler timerHandler = new Handler();

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            seconds++;
            if (seconds == 60) {
                seconds = 0;
                minutes++;
            }
            String time = String.format("%02d:%02d", minutes, seconds);
            timer.setText(time);
            timerHandler.postDelayed(this, 1000);
        }
    };
    private final Runnable updateTimeTask = new Runnable() {
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                timer.setText(formatDuration(currentPosition));
                timerHandler.postDelayed(this, 1000);
            }
        }
    };

    private void startTimer() {
        timerHandler.postDelayed(timerRunnable, 0);
    }
    private void resetTimer() {
        seconds = 00;
        minutes = 00;
        String time = String.format("%02d:%02d", minutes, seconds);
        timer.setText(time);
        timerHandler.removeCallbacks(timerRunnable);
    }
    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    private String formatDuration(int duration) {
        int minutes = duration / 60000;
        int seconds = (duration % 60000) / 1000;
        return String.format("%02d:%02d", minutes, seconds);
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }
    private boolean checkPermissionFromDevice() {
        int writeExternalStorageResult = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int recordAudioResult = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        return writeExternalStorageResult == PackageManager.PERMISSION_GRANTED &&
                recordAudioResult == PackageManager.PERMISSION_GRANTED;
    }
    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    void sendNotification(String message){

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try{
                    JSONObject jsonObject  = new JSONObject();

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title",currentUser.getUsername());
                    notificationObj.put("body",message);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("userId",currentUser.getUserId());

                    jsonObject.put("notification",notificationObj);
                    jsonObject.put("data",dataObj);
                    jsonObject.put("to",otherUser.getFcmToken());

                    callApi(jsonObject);


                }catch (Exception e){

                }

            }
        });

    }
    void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer AAAASqJTAdQ:APA91bGSYqemCbqkUD2hSM7HFMNRdDl-HN0EDxcrXKaCjNQJx5CL5qKXCZRYCNbItzQTVbOHhJHbhqMK3w74jfXfkqYNtHhzGiSbYfB39wZ_CQVDCeSdX4O-sXQiU9WZADotgzKhjUMK")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        timerHandler.removeCallbacks(updateTimeTask);
    }
}
