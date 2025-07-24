package com.example.myapplication;
import com.example.myapplication.utils.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.model.*;
import com.example.myapplication.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class LoginUsernameActivity extends AppCompatActivity {

    EditText usernameInput;
    Button letMeInBtn;
    ProgressBar progressBar;
    String phoneNumber;
    UserModel userModel;
    RoundedImageView profileImage;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        phoneNumber = getIntent().getExtras().getString("phone");
        getUsername();

        letMeInBtn.setOnClickListener(view -> {
            setUsername();
        });

    }

    void setUsername(){

        String username = usernameInput.getText().toString();
        if(username.isEmpty() || username.length()<3){
            usernameInput.setError("Tên không hợp lệ");
            return;
        }
        setInProgress(true);

        if(userModel!=null){
            userModel.setUsername(username);
        }else{
            userModel = new UserModel(phoneNumber, username, Timestamp.now(), FirebaseUtil.currentUserID() ,"", "", "", "", "", "", "", "");
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    setDataPreference();
                    Intent intent = new Intent(LoginUsernameActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity(intent);
                }
            }
        });

    }
    void setDataPreference(){
        String userId = FirebaseUtil.currentUserID();
        FirebaseUtil.allUserCollectionReference().document(userId).get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task1.getResult();
                        preferenceManager.putString(FirebaseUtil.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(FirebaseUtil.KEY_USER_NAME, documentSnapshot.getString(FirebaseUtil.KEY_USER_NAME));
                        preferenceManager.putString(FirebaseUtil.KEY_PHONE, documentSnapshot.getString(FirebaseUtil.KEY_PHONE));
                        preferenceManager.putString(FirebaseUtil.KEY_TOKEN, documentSnapshot.getString(FirebaseUtil.KEY_TOKEN));
                        preferenceManager.putString(FirebaseUtil.KEY_EMAIL, documentSnapshot.getString(FirebaseUtil.KEY_EMAIL));
                    }
                });
    }

    void getUsername(){
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if (task.isSuccessful()){
                    userModel = task.getResult().toObject(UserModel.class);
                    if(userModel!=null){
                        usernameInput.setText(userModel.getUsername());
                    }
                }
            }
        });
        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()){

                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(this, uri, profileImage);
                    }

                });
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            letMeInBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            letMeInBtn.setVisibility(View.VISIBLE);
        }
    }
}