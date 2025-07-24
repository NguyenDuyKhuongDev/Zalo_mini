package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.example.myapplication.adapter.ChatRecyclerAdapter;
import com.example.myapplication.model.ChatMessageModel;
import com.example.myapplication.model.ChatroomModel;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.test.AudioMessageListener;
import com.example.myapplication.test.OnButtonClickListener;
import com.example.myapplication.bottomSheet.RecordingBottomSheet;
import com.example.myapplication.test.VideoClickListener;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.example.myapplication.utils.Permission;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends BaseActivity  {

    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;
    EditText messageInput;
    UserModel currentUserModel;
    TextView addFriendBtn, waitFriendBtn, requestFriendBtn, progressText, otherUsername;
    RecyclerView recyclerView;
    ImageView imageView, friendStatus;
    ImageButton camera_btn,microBtn,  file_btn, sendMessageBtn, backBtn;
    RoundedImageView statusOnline;
    String currentUserID = FirebaseUtil.currentUserID();
    HashMap<String, Object> myUser;
    RelativeLayout bottomItemLayout;
    private OnButtonClickListener listener;
    private VideoClickListener videoClickListener;
    private AudioMessageListener audioMessageListener;
    Dialog dialog;
    private static final int PICK_DOCUMENT_REQUEST = 1;
    Context context;
    ProgressBar progressBar2;
    Permission permissions;
    MediaPlayer mediaPlayer;

    String audioPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        context = this;
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(currentUserID,otherUser.getUserId());

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        imageView = findViewById(R.id.profile_pic_image_view);
        camera_btn = findViewById(R.id.camera_btn);
        addFriendBtn = findViewById(R.id.friend_add);
        waitFriendBtn = findViewById(R.id.friend_wait);
        requestFriendBtn = findViewById(R.id.friend_request);
        friendStatus = findViewById(R.id.friend_status);
        statusOnline = findViewById(R.id.onlineStatus);
        file_btn = findViewById(R.id.file_btn);
        bottomItemLayout = findViewById(R.id.bottom_layout);
        progressText = findViewById(R.id.progressText);
        progressBar2 = findViewById(R.id.progressBar2);
        permissions = new Permission(context);
        microBtn = findViewById(R.id.microBtn);

        messageInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);// cho phep xuong nhiefu dong
        setListenNer();
        addFriend();
        callVideo();
        initVoiceButton();
        initVideoButton();
        getOrCreateChatroomModel();
        setupChatRecyclerView();
        edittextChanged();
        status_AVAILABLILITY();
    }


    void setListenNer(){
        videoClickListener = new VideoClickListener() {
            @Override
            public void showDialogMedia(int position,String mediaType, Uri media) {
                permissions.dialogVideo(context, position, mediaType, media, ChatActivity.this);
            }
        };

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl().
                addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        Uri uri = task1.getResult();
                        AndroidUtil.setProfilePic(this, uri, imageView);
                    }

                });

        backBtn.setOnClickListener(view -> {
            onBackPressed();
        });
        otherUsername.setText(otherUser.getUsername());
        sendMessageBtn.setOnClickListener(view -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);

        });
        camera_btn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*, video/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);


        });
        file_btn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*"); // Chọn bất kỳ loại tệp nào
            startActivityForResult(intent, PICK_DOCUMENT_REQUEST);
        });
        microBtn.setOnClickListener(v -> {
            RecordingBottomSheet modalBottomSheet = new RecordingBottomSheet(getApplicationContext(), chatroomId, otherUser, chatroomModel);
            modalBottomSheet.show(getSupportFragmentManager(), RecordingBottomSheet.TAG);
        });

    }
    private void addFriend(){
        setData();
        FirebaseUtil.friend(currentUserID).whereEqualTo(FirebaseUtil.KEY_USER_ID, otherUser.getUserId()).get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful() && ! task1.getResult().isEmpty()){
                friendStatus.setVisibility(View.GONE);
                addFriendBtn.setVisibility(View.GONE);
                requestFriendBtn.setVisibility(View.GONE);
                waitFriendBtn.setVisibility(View.GONE);

            }else {
                FirebaseUtil.checkWaitFriend(currentUserID).whereEqualTo(FirebaseUtil.KEY_USER_ID, otherUser.getUserId()).get().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful() && !task2.getResult().isEmpty()){
                        friendStatus.setVisibility(View.VISIBLE);
                        addFriendBtn.setVisibility(View.GONE);
                        requestFriendBtn.setVisibility(View.GONE);
                        waitFriendBtn.setVisibility(View.VISIBLE);
                    }else {
                        FirebaseUtil.checkRequestFriend(currentUserID).whereEqualTo(FirebaseUtil.KEY_USER_ID, otherUser.getUserId()).get().addOnCompleteListener(task3 -> {
                            if (task3.isSuccessful() && ! task3.getResult().isEmpty()){
                                friendStatus.setVisibility(View.VISIBLE);
                                addFriendBtn.setVisibility(View.GONE);
                                requestFriendBtn.setVisibility(View.VISIBLE);
                                waitFriendBtn.setVisibility(View.GONE);
                            }else {
                                friendStatus.setVisibility(View.VISIBLE);
                                addFriendBtn.setVisibility(View.VISIBLE);
                                requestFriendBtn.setVisibility(View.GONE);
                                waitFriendBtn.setVisibility(View.GONE);


                                addFriendBtn.setOnClickListener(v -> {
                                    HashMap<String, Object> arrFriend = new HashMap<>();
                                    arrFriend.put(FirebaseUtil.KEY_USER_ID, otherUser.getUserId());
                                    arrFriend.put(FirebaseUtil.KEY_USER_NAME, otherUser.getUsername());
                                    arrFriend.put(FirebaseUtil.KEY_PHONE, otherUser.getPhone());
                                    arrFriend.put(FirebaseUtil.KEY_TOKEN, otherUser.getFcmToken());

                                    FirebaseUtil.waitFriend(currentUserID).add(arrFriend).addOnSuccessListener(documentReference -> {
                                    }).addOnFailureListener(e -> {
                                        showToast(e.getMessage());
                                    });
                                    FirebaseUtil.requestFriend(otherUser.getUserId()).add(myUser).addOnSuccessListener(documentReference -> {
                                        showToast("Đã gửi lời mời kết bạn đến " + otherUser.getUsername());
                                    }).addOnFailureListener(e -> {
                                        showToast(e.getMessage());
                                    });
                                    friendStatus.setVisibility(View.VISIBLE);
                                    addFriendBtn.setVisibility(View.GONE);
                                    requestFriendBtn.setVisibility(View.GONE);
                                    waitFriendBtn.setVisibility(View.VISIBLE);
                                });

                            }
                        });
                    }
                });
            }
        });
    }
    void setData(){
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    myUser = new HashMap<>();
                    myUser.put(FirebaseUtil.KEY_USER_ID, document.getString(FirebaseUtil.KEY_USER_ID));
                    myUser.put(FirebaseUtil.KEY_USER_NAME, document.getString(FirebaseUtil.KEY_USER_NAME));
                    myUser.put(FirebaseUtil.KEY_PHONE, document.getString(FirebaseUtil.KEY_PHONE));
                    myUser.put(FirebaseUtil.KEY_TOKEN, document.getString(FirebaseUtil.KEY_TOKEN));


                } else {
                    showToast("Error");

                }
            }
        });
    }
    private void showToast(String message) {
        Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    private void initVideoButton() {
        ZegoSendCallInvitationButton newVideoCall = findViewById(R.id.video_call_btn);
        newVideoCall.setIsVideoCall(true);
        newVideoCall.setOnClickListener(v -> {

            String targetUserID = otherUser.getUserId();
            String[] split = targetUserID.split(",");
            List<ZegoUIKitUser> users = new ArrayList<>();
            for (String userID : split) {
                String userName = otherUser.getUsername();
                users.add(new ZegoUIKitUser(userID, userName));
            }
            newVideoCall.setInvitees(users);
        });
    }
    private void initVoiceButton() {
        ZegoSendCallInvitationButton newVoiceCall = findViewById(R.id.call_btn);
        newVoiceCall.setIsVideoCall(false);
        newVoiceCall.setOnClickListener(v -> {

            String targetUserID = otherUser.getUserId();
            String[] split = targetUserID.split(",");
            List<ZegoUIKitUser> users = new ArrayList<>();
            for (String userID : split) {
                String userName = otherUser.getUsername();
                users.add(new ZegoUIKitUser(userID, userName));
            }
            newVoiceCall.setInvitees(users);
        });
    }
    void setupChatRecyclerView(){
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, getSupportFragmentManager(),chatroomId,getApplicationContext(), this.listener, this.videoClickListener, this.audioMessageListener);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });

    }
    void getOrCreateChatroomModel(){
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel==null){
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserID(),otherUser.getUserId()),
                            Timestamp.now(),
                            "","", ""


                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

                }
            }
        });
    }
    void sendMessageToUser(String message){
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserID());
        chatroomModel.setLastMessage(message);
        chatroomModel.setStatusRead("0");

        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserID(), Timestamp.now(),"0", "0", "0", "null", "null", "0");
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    messageInput.setText("");
                    sendNotification(message);
                }

            }
        });
    }
    private void sendImageMessage(String imageUrl) {
        HashMap<String, Object> message = new HashMap<>();
        message.put("timestamp", Timestamp.now());
        message.put("message", imageUrl);
        message.put("senderId", currentUserID);
        message.put("images", "1");
        message.put("videos", "0");
        message.put("files", "0");
        message.put("audios", "0");
        chatroomModel.setLastMessage("###sendImage%&*!");
        chatroomModel.setLastMessageSenderId(currentUserID);
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setStatusRead("0");

        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(message).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    sendNotification("Đã gửi một hình ảnh");
                }
            }
        });

    }

    private void sendVideoMessage(String videoUrl){

        HashMap<String, Object> message = new HashMap<>();
        message.put("timestamp", Timestamp.now());
        message.put("senderId", currentUserID);
        message.put("message", videoUrl);
        message.put("images", "0");
        message.put("videos", "1");
        message.put("files", "0");
        message.put("audios", "0");
        chatroomModel.setLastMessage("###sendVideo%&*!");
        chatroomModel.setLastMessageSenderId(currentUserID);
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setStatusRead("0");
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(message).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    sendNotification("Đã gửi một video");
                }
            }
        })  .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                showToast("Failed to save video URL to Firestore");
            }
        });
    }
    private void sendFileMessage(String fileName, String sizeFile, String fileUrl){
        HashMap<String, Object> message = new HashMap<>();
        message.put("timestamp", Timestamp.now());
        message.put("senderId", currentUserID);
        message.put("message", fileUrl);
        message.put("fileName", fileName);
        message.put("sizeFile", sizeFile);
        message.put("images", "0");
        message.put("videos", "0");
        message.put("files", "1");
        message.put("audios", "0");
        chatroomModel.setLastMessage("###sendDocument%&*!");
        chatroomModel.setLastMessageSenderId(currentUserID);
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setStatusRead("0");
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(message).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    sendNotification("Đã gửi một tệp");
                }
            }
        })  .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                showToast("Failed to save file URL to Firestore");
            }
        });
    }
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri mediaUri = result.getData().getData();
                        try {
                            String mimeType = getContentResolver().getType(mediaUri);

                            if (mimeType != null && mimeType.startsWith("video/")) {

                                Video(mediaUri);

                            } else if (mimeType != null && mimeType.startsWith("image/")) {

                                Image(mediaUri);
                            } else {
                                Log.e("MediaaaaPicker", "Tệp không phải là ảnh hoặc video");
                            }
                        } catch (Exception e) {
                            Log.e("FilePicker", "Lỗi khi xử lý tệp", e);
                        }
                    }
                }
            }

    );
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_DOCUMENT_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();

            if (fileUri != null) {
                String filename = fileUri.getLastPathSegment();

                uploadDocumentToFirebaseStorage(fileUri, filename);
            }
        }
    }
    private void uploadDocumentToFirebaseStorage(Uri fileUri, String fileName) {

        long fileSizeInBytes = 0;
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(fileUri, "r");
            fileSizeInBytes = pfd.getStatSize();
            pfd.close();
        } catch (IOException e) {
            e.printStackTrace();
            showToast("Không thể đọc dung lượng file");
            return;
        }
        // Chuyển đổi từ byte sang MB
        double fileSizeInMB = fileSizeInBytes / (1024.0 * 1024.0);
        if (fileSizeInMB > 20) {
            showToast("Dung lượng file quá 20MB");
            return;
        }
        setProgressBar(true);
        UploadTask uploadTask = FirebaseUtil.putDocuments().putFile(fileUri);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressBar2.setProgress((int) progress);
                progressText.setText(String.format(Locale.getDefault(), "%d%%",(int) progress));
            }
        });
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    setProgressBar(false);
                    task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String documentsUrl = uri.toString();
                            Log.e("aaaa", documentsUrl);

                            Documents(documentsUrl, fileName);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast("Failed get URL");
                        }
                    });

                }else {
                    Log.e("FirebaseStorage", "Tải lên tệp thất bại", task.getException());
                }
            }
        });
    }
    private void Documents(String documentsUrl, String fileName){
        StorageReference fileRef = FirebaseStorage.getInstance().getReferenceFromUrl(documentsUrl);
        fileRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata metadata) {
                long fileSize = metadata.getSizeBytes();
                String fileType = metadata.getContentType();
                String fileToMb = permissions.formatFileSize(fileSize, true);

                sendFileMessage(fileName, fileToMb, documentsUrl);
                Log.e("Tên tệp", fileName);
                Log.e("Tomb: ", fileToMb);
                Log.e("Type: ", fileType);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.err.println("Lỗi khi lấy metadata của tệp: " + e.getMessage());
            }
        });
    }
    private void Image(Uri imageUri){
      if (imageUri != null){
          UploadTask uploadTask = FirebaseUtil.putImageChat().putFile(imageUri);
          uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                  if (task.isSuccessful()){
                      task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                          @Override
                          public void onSuccess(Uri uri) {
                              String imageUrl = uri.toString();
                              sendImageMessage(imageUrl);

                          }
                      }).addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {
                              showToast("Failed get URL");
                          }
                      });
                  }else {
                      showToast("tải image thất bại");
                  }
              }
          });
      }else {
          showToast("upload failed");
      }
    }
    private void Video(Uri videoUri) throws IOException {

        if (videoUri != null){
            try {
                long fileSize = permissions.getFileSize(context, videoUri);
                long maxFileSize = 10 * 1024 * 1024; // 10MB

                if (fileSize > maxFileSize) {
                    showToast("Kích thước video vượt quá 10MB");
                    return;
                }
                setProgressBar(true);
                UploadTask uploadTask = FirebaseUtil.putVideoChat().putFile(videoUri);
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressBar2.setProgress((int) progress);
                        progressText.setText(String.format(Locale.getDefault(), "%d%%",(int) progress));
                    }
                });
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String videoUrl = uri.toString();
                                    sendVideoMessage(videoUrl);
                                    setProgressBar(false);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showToast("Failed get URL");
                                }
                            });
                        }else {
                            showToast("tải video thất bại");
                        }
                    }
                });
            }catch (Exception e){
                showToast("Tải lên thất bại");
            }
        }else {
            showToast("upload failed");
        }
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
    void status_AVAILABLILITY(){
        FirebaseUtil.status(otherUser.getUserId()).addSnapshotListener(((value, error) -> {
            if (value != null) {
                String statusStr = value.getString("status");
                if (statusStr != null && !statusStr.isEmpty()) {
                    int availability = Integer.parseInt(statusStr);
                    if (availability == 0) {
                        statusOnline.setVisibility(View.GONE);
                    } else {
                        statusOnline.setVisibility(View.VISIBLE);
                    }
                }
            }
        }));
    }
    void callVideo(){
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {

            currentUserModel = task.getResult().toObject(UserModel.class);
            assert currentUserModel != null;
            String userName = currentUserModel.getUsername();
            String userId = currentUserModel.getUserId();

            signIn(userId, userName);
        });
    }
    private void signIn(String userID, String userName) {
        if (TextUtils.isEmpty(userID) || TextUtils.isEmpty(userName)) {
            return;
        }
        try {
            long appID = 1501464303;
            String appSign = "91780e99fdff8c5aebabb78c907301172bb60f8b4f8c858b6189375dc1b28dc0";
            initCallInviteService(appID, appSign, userID, userName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public void initCallInviteService(long appID, String appSign, String userID, String userName) {

        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();

        ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, userID, userName,
                callInvitationConfig);

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
    void setProgressBar(boolean checkBoolean){
        if (checkBoolean)
        {
            progressBar2.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.VISIBLE);
        }else {
            progressBar2.setVisibility(View.GONE);
            progressText.setVisibility(View.GONE);
        }

    }
    void edittextChanged(){
        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageInput.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_END, 0); // 0 là ID của view không tồn tại
                    messageInput.setLayoutParams(params);
                    bottomItemLayout.setVisibility(View.GONE);
                } else {

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageInput.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_START, 0);
                    messageInput.setLayoutParams(params);
                    bottomItemLayout.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}