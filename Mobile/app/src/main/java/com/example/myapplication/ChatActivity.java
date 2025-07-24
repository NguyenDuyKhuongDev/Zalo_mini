package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.example.myapplication.adapter.ChatRecyclerAdapter;
import com.example.myapplication.model.ChatMessageModel;
import com.example.myapplication.model.ChatroomModel;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.test.ModalBottomSheet;
import com.example.myapplication.test.OnButtonClickListener;
import com.example.myapplication.test.VideoClickListener;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.example.myapplication.utils.Permissions;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
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
   }
  ModalBottomSheet ModalBottomSheet;
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
    Dialog dialog;
    private static final int PICK_DOCUMENT_REQUEST = 1;
    Context context;
    ProgressBar progressBar2;
    Permissions permissions;

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
        permissions = new Permissions();
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
        listener = new OnButtonClickListener() {
            @Override
            public void onButtonClick(int position, String textMessage) {
                ModalBottomSheet modalBottomSheet = new ModalBottomSheet(textMessage, getApplicationContext(), chatroomId);
                modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG);
            }
        };
        videoClickListener = new VideoClickListener() {
            @Override
            public void showDialogMedia(int position,String mediaType, Uri media) {
                dialog = new Dialog(ChatActivity.this);
                dialog.setContentView(R.layout.custom_dialog_mediaview);
                Window dialogWindow = dialog.getWindow();

                if (dialogWindow != null) {
                    dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    dialogWindow.setGravity(Gravity.CENTER);
                }
                Drawable customBackground  = ContextCompat.getDrawable(context, R.drawable.dialog_backgroud);
                dialog.getWindow().setBackgroundDrawable(customBackground);
                ImageButton closeBtn = dialog.findViewById(R.id.close_btn_dialog);
                VideoView videoView = dialog.findViewById(R.id.videoView_dialog);
                ImageView imageView1  = dialog.findViewById(R.id.imageView_dialog);
                if (mediaType.equals("image"))
                {
                    imageView1.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                    AndroidUtil.setImagePic(context, media, imageView1);

                }else if (mediaType.equals("video")){
                    imageView1.setVisibility(View.GONE);
                    videoView.setVisibility(View.VISIBLE);
                    MediaController mediaController = new MediaController(ChatActivity.this);
                    videoView.setMediaController(mediaController);
                    mediaController.setAnchorView(videoView);
                    videoView.setVideoURI(media);
                    videoView.start();
                }

                closeBtn.setOnClickListener(v -> {
                    dialog.dismiss();
                });
                dialog.show();
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

        adapter = new ChatRecyclerAdapter(options, getSupportFragmentManager(),chatroomId,getApplicationContext(), this.listener, this.videoClickListener);
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
        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserID(), Timestamp.now(),"0", "0", "0", "null", "null");
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
   


}