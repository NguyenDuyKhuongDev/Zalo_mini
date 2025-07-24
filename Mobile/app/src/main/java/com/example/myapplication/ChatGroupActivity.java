package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.GroupChatAdapter;
import com.example.myapplication.model.ChatGroupMessage;
import com.example.myapplication.model.GroupModel;
import com.example.myapplication.model.PreferenceManager;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.test.GroupMediaListener;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.BaseActivity;
import com.example.myapplication.utils.FirebaseUtil;
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

import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGroupActivity extends BaseActivity {
    private PreferenceManager preferenceManager;
    private RecyclerView recyclerView;
    GroupChatAdapter adapter;
    TextView groupName;
    ImageButton camera_btn, backBtn, sendBtn, micro_btn, fileGroupBtn;
    RoundedImageView imageProfile1, imageProfile2;
    String documentId, otherIdImage ;
    private EditText messageInput;
    GroupModel groupModel;
    UserModel otherUser;
    Context context;
    RelativeLayout bottom_layout;
    private static final int PICK_DOCUMENT_REQUEST = 1;
    private GroupMediaListener groupMediaListener;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContent();
        setupChatRecyclerView();
        edittextChanged();

        setListener();

    }
    void setupChatRecyclerView(){
        Query query = FirebaseUtil.group_chats(documentId)
                .orderBy("dataTime", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatGroupMessage> options = new FirestoreRecyclerOptions.Builder<ChatGroupMessage>()
                .setQuery(query,ChatGroupMessage.class).build();

        adapter = new GroupChatAdapter(options,documentId,getApplicationContext(), this.groupMediaListener);
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
    void setListener(){
        sendBtn.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        });
        imageProfile1.setOnClickListener(v -> {
            Intent intent = new Intent(this, InfoGroupActivity.class);
            intent.putExtra("key_id", documentId);
            startActivity(intent);
        });
        groupName.setOnClickListener(v -> {
            Intent intent = new Intent(this, InfoGroupActivity.class);
            intent.putExtra("key_id", documentId);
            startActivity(intent);
        });
        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
        camera_btn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*, video/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        fileGroupBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_DOCUMENT_REQUEST);
        });
        FirebaseUtil.groups().document(documentId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                groupName.setText(documentSnapshot.getString("groupName"));

            }
        });
        groupMediaListener = (position, mediaType, media) -> {
            dialog = new Dialog(ChatGroupActivity.this);
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
                videoView.setVideoURI(media);
                videoView.start();
            }

            closeBtn.setOnClickListener(v -> {
                dialog.dismiss();
            });
            dialog.show();
        };
    }

    void sendMessageToUser(String message){
        HashMap<String, Object> groups = new HashMap<>();
        groups.put("lastMessage", message);
        groups.put("timestamp", Timestamp.now());
        groups.put("lastUserIdSend", preferenceManager.getString(FirebaseUtil.KEY_USER_ID));
        groups.put("statusRead", "0");
        FirebaseUtil.getChatGroupReference(documentId).update(groups);

        String senderName = preferenceManager.getString(FirebaseUtil.KEY_USER_NAME);
        ChatGroupMessage chatMessageModel = new ChatGroupMessage( FirebaseUtil.currentUserID(), message, senderName,"0","0", Timestamp.now() , "0", "null", "null");
        FirebaseUtil.group_chats(documentId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            messageInput.setText("");
                           Notification(documentId, message);
                        }

                    }
                });
    }
    private void sendImageMessage(String imageUrl) {
        HashMap<String, Object> lastMessageImage = new HashMap<>();
        lastMessageImage.put("timestamp", Timestamp.now());
        lastMessageImage.put("lastMessage", "###sendImage%&*!");
        lastMessageImage.put("lastUserIdSend", preferenceManager.getString(FirebaseUtil.KEY_USER_ID));
        lastMessageImage.put("statusRead", "0");
        FirebaseUtil.getChatGroupReference(documentId).update(lastMessageImage);

        String senderName = preferenceManager.getString(FirebaseUtil.KEY_USER_NAME);
        ChatGroupMessage chatMessageModel = new ChatGroupMessage( FirebaseUtil.currentUserID(), imageUrl, senderName,"1","0", Timestamp.now() , "0", "null", "null");
        FirebaseUtil.group_chats(documentId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Notification(documentId, "Đã gủi một ảnh");
            }
        });

    }
    private void sendVideoMessage(String videoUrl){
        HashMap<String, Object> lastMessageVideo = new HashMap<>();
        lastMessageVideo.put("timestamp", Timestamp.now());
        lastMessageVideo.put("lastMessage", "###sendVideo%&*!");
        lastMessageVideo.put("lastUserIdSend", preferenceManager.getString(FirebaseUtil.KEY_USER_ID));
        lastMessageVideo.put("statusRead", "0");
        FirebaseUtil.getChatGroupReference(documentId).update(lastMessageVideo);
        String senderName = preferenceManager.getString(FirebaseUtil.KEY_USER_NAME);
        ChatGroupMessage chatMessageModel = new ChatGroupMessage( FirebaseUtil.currentUserID(), videoUrl, senderName,"0","1", Timestamp.now() , "0", "null", "null");
        FirebaseUtil.group_chats(documentId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Notification(documentId, "Đã gửi một video");
            }
        });
    }
    private void sendFileMessage(String fileName, String sizeFile, String fileUrl) {
        HashMap<String, Object> lastMessageFile = new HashMap<>();
        lastMessageFile.put("timestamp", Timestamp.now());
        lastMessageFile.put("lastMessage", "###sendDocument%&*!");
        lastMessageFile.put("lastUserIdSend", preferenceManager.getString(FirebaseUtil.KEY_USER_ID));
        lastMessageFile.put("statusRead", "0");
        FirebaseUtil.getChatGroupReference(documentId).update(lastMessageFile);
        String senderName = preferenceManager.getString(FirebaseUtil.KEY_USER_NAME);
        ChatGroupMessage chatMessageModel = new ChatGroupMessage( FirebaseUtil.currentUserID(), fileUrl, senderName,"0","0", Timestamp.now() , "1", fileName, sizeFile);
        FirebaseUtil.group_chats(documentId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Notification(documentId, "Đã gửi một tệp");
            }
        });
    }

    private void Image(Uri imageUri){
        if (imageUri != null){
            UploadTask uploadTask = FirebaseUtil.putImageGroupChat().putFile(imageUri);
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
                long fileSize = getFileSize(videoUri);
                long maxFileSize = 10 * 1024 * 1024; // 10MB

                if (fileSize > maxFileSize) {
                    showToast("Kích thước video vượt quá 10MB");
                    return;
                }
                UploadTask uploadTask = FirebaseUtil.putVideoGroupChat().putFile(videoUri);
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        //double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());

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
        //setProgressBar(true);
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
        // Chuyển đổi từ byte sang MB và kiểm tra xem có quá 20MB hay không
        double fileSizeInMB = fileSizeInBytes / (1024.0 * 1024.0);
        if (fileSizeInMB > 20) {
            showToast("Dung lượng file quá 20MB");
            return;
        }

        UploadTask uploadTask = FirebaseUtil.putDocumentsGroupChat().putFile(fileUri);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
//                progressBar2.setProgress((int) progress);
//                progressText.setText(String.format(Locale.getDefault(), "%d%%",(int) progress));
            }
        });
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()){
               // setProgressBar(false);
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
        });
    }
    private void Documents(String documentsUrl, String fileName){
        StorageReference fileRef = FirebaseStorage.getInstance().getReferenceFromUrl(documentsUrl);
        fileRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata metadata) {
                long fileSize = metadata.getSizeBytes();
                String fileType = metadata.getContentType();
                String fileToMb = formatFileSize(fileSize, true);

                sendFileMessage(fileName, fileToMb, documentsUrl);
                Log.e("Tên tệp", fileName);
                Log.e("aaaa", fileToMb);
                Log.e("aaaa", fileType);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.err.println("Lỗi khi lấy metadata của tệp: " + e.getMessage());
            }
        });
    }
    private void Notification(String documentId, String message){
        FirebaseUtil.groupsMember(documentId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                List<String> userIds = (List<String>) documentSnapshot.get("userIds");
                if (userIds != null) {
                    sendNotification(message, userIds);
                }
            }
        }).addOnFailureListener(e -> {

            Log.e("Firestore", "Error getting document: ", e);
        });
    }
    void sendNotification(String message, List<String> userIds){

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                UserModel currentUser = task.getResult().toObject(UserModel.class);

                for (String userID: userIds){
                   FirebaseUtil.otherUserDetails(userID).get().addOnCompleteListener(task1 -> {
                       if (task1.isSuccessful()){
                           UserModel OtherUserModel = task1.getResult().toObject(UserModel.class);
                           try{
                               JSONObject jsonObject  = new JSONObject();

                               JSONObject notificationObj = new JSONObject();
                               notificationObj.put("title",currentUser.getUsername());
                               notificationObj.put("body",message);

                               JSONObject dataObj = new JSONObject();
                               dataObj.put("userId",currentUser.getUserId());

                               jsonObject.put("notification",notificationObj);
                               jsonObject.put("data",dataObj);
                               jsonObject.put("to",OtherUserModel.getFcmToken());

                               callApi(jsonObject);


                           }catch (Exception e){

                           }
                       }
                   });
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
                    bottom_layout.setVisibility(View.GONE);
                } else {

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageInput.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_START, 0);
                    messageInput.setLayoutParams(params);
                    bottom_layout.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
    void setContent(){
        groupModel = new GroupModel();
        setContentView(R.layout.activity_chat_group);
        backBtn = findViewById(R.id.back_btn_group_info);
        groupName = findViewById(R.id.name_group);
        messageInput = findViewById(R.id.chat_message_input_group);
        recyclerView = findViewById(R.id.chat_recycler_view_group);
        sendBtn = findViewById(R.id.message_send_btn_group);
        imageProfile1 = findViewById(R.id.imageProfile1);
        imageProfile2 = findViewById(R.id.imageProfile2);
        micro_btn = findViewById(R.id.micro_btn_group);
        camera_btn = findViewById(R.id.camera_btn_group);
        preferenceManager =new PreferenceManager(context);
        documentId = getIntent().getStringExtra("key_id");
        otherIdImage = getIntent().getStringExtra("key_image");
        //Toast.makeText(context, otherIdImage, Toast.LENGTH_SHORT).show();
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        fileGroupBtn = findViewById(R.id.fileGroupBtn);
        bottom_layout = findViewById(R.id.bottom_layout);
        setImageOther(imageProfile2, otherIdImage);
    }
    void setImageOther(RoundedImageView imageView, String sendId){

        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnSuccessListener(uri -> AndroidUtil.setProfilePic(context, uri, imageProfile1))
                .addOnFailureListener(e -> {
                });


        if (sendId != null)
        {
            FirebaseUtil.getOtherProfilePicStorageRef(sendId).getDownloadUrl()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Uri uri = task.getResult();
                            AndroidUtil.setProfilePic(context, uri, imageView);
                        }
                    });
        }



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
       // ZegoUIKitPrebuiltCallInvitationService.unInit();
    }
    private void showToast(String message) {
        Toast.makeText(ChatGroupActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    private long getFileSize(Uri uri) throws IOException {
        ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fd = pfd.getFileDescriptor();
        FileInputStream fis = new FileInputStream(fd);
        long fileSize = fis.getChannel().size();
        fis.close();
        pfd.close();
        return fileSize;
    }
    private String formatFileSize(long bytes, boolean inMB) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        if (inMB) {
            double fileSizeInMB = bytes / (1024.0 * 1024);
            return decimalFormat.format(fileSizeInMB) + " MB";
        } else {

            double fileSizeInKB = bytes / 1024.0;
            return decimalFormat.format(fileSizeInKB) + " KB";
        }
    }
}