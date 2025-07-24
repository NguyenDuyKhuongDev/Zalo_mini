package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.myapplication.R.id;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.example.myapplication.utils.Permission;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;

public class StoryPostActivity extends AppCompatActivity {

    FloatingActionButton addImagePostBtn;
    ImageView imagePost,backBtn;
    EditText writeStory;
    TextView postBtn;
    CardView imageBackgroundCardView, videoBackgroundCardView;
    private Uri ImageUri, videoUri, mediaUri;
    HashMap<String, Object> myUser;
    UserModel userModel;
    String mimeType;
    VideoView videoStory;
    Permission permission;
    Context context;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permission = new Permission(context);
        setData();

        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
        addImagePostBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*, video/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });


        postBtn.setOnClickListener(v -> {

            mimeType = getContentResolver().getType(mediaUri);
            if (mimeType != null && mimeType.startsWith("video/")){
                try {
                    Video(videoUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else if (mimeType != null && mimeType.startsWith("image/")){
                Image(ImageUri);
            }else {
                showToast("Lỗi định dạng");
            }
        });
    }


    private void sendImageMessage(String mediaUrl) {
        if (writeStory.getText().toString().isEmpty()){
            showToast("Hãy viết gì đó hoặc chọn ảnh để tạo bài viết mới!");
        }else {
            HashMap<String, Object> post = new HashMap<>();
            post.put("idAuthor", userModel.getUserId());
            post.put("nameAuthor", userModel.getUsername());
            post.put("postTimestamp", Timestamp.now());
            post.put("postText", writeStory.getText().toString());
            post.put("postMedia", mediaUrl);
            post.put("imageStatus", "1");
            post.put("videoStatus", "0");

            FirebaseUtil.postStory().add(post).addOnSuccessListener(documentReference -> {
                showToast("Bài viết đã được đăng lên trang cá nhân");
                onBackPressed();
            }).addOnFailureListener(e -> {
                showToast(e.getMessage());
            });

        }


    }
    private void sendVideoMessage(String mediaUrl) {
        if (writeStory.getText().toString().isEmpty()){
            showToast("Hãy viết gì đó hoặc chọn ảnh để tạo bài viết mới!");
        }else {
            HashMap<String, Object> post = new HashMap<>();
            post.put("idAuthor", userModel.getUserId());
            post.put("nameAuthor", userModel.getUsername());
            post.put("postTimestamp", Timestamp.now());
            post.put("postText", writeStory.getText().toString());
            post.put("postMedia", mediaUrl);
            post.put("imageStatus", "0");
            post.put("videoStatus", "1");


            FirebaseUtil.postStory().add(post).addOnSuccessListener(documentReference -> {
                showToast("Bài viết đã được đăng lên trang cá nhân");
                onBackPressed();
            }).addOnFailureListener(e -> {
                showToast(e.getMessage());
            });

        }


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
                    userModel = new UserModel(myUser);


                } else {
                    showToast("Error");

                }
            }
        });
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                         mediaUri = result.getData().getData();
                        try {
                             mimeType = getContentResolver().getType(mediaUri);

                            if (mimeType != null && mimeType.startsWith("video/")) {
                                videoBackgroundCardView.setVisibility(View.VISIBLE);
                                imageBackgroundCardView.setVisibility(View.GONE);
                                videoUri = mediaUri;
                                videoStory.setVideoURI(mediaUri);
                                videoStory.start();
                            } else if (mimeType != null && mimeType.startsWith("image/")) {
                                imageBackgroundCardView.setVisibility(View.VISIBLE);
                                videoBackgroundCardView.setVisibility(View.GONE);
                                AndroidUtil.setImagePic(this, mediaUri, imagePost);
                                ImageUri = mediaUri;
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
    private void Image(Uri imageUri){
        if (imageUri != null){
            UploadTask uploadTask = FirebaseUtil.putMediaImagesStory().putFile(imageUri);
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
            sendImageMessage("null");

        }
    }
    private void Video(Uri videoUri) throws IOException {
        if (videoUri != null){
            try {
                long fileSize = permission.getFileSize(context, videoUri);
                long maxFileSize = 10 * 1024 * 1024; // 10MB

                if (fileSize > maxFileSize) {
                    showToast("Kích thước video vượt quá 10MB");
                    return;
                }
                UploadTask uploadTask = FirebaseUtil.putMediaVideosStory().putFile(videoUri);
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
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}