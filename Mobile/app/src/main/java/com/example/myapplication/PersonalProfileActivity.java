package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.myapplication.adapter.PersonalProfileRecyclerAdapter;
import com.example.myapplication.model.PostStoryModel;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class PersonalProfileActivity extends AppCompatActivity {
    ImageView avatarImg,coverPhotoImageView;
    UserModel currentUserModel;
    AppCompatImageView backBtn;
    TextView userNameTxt, infoTxt, addressTxt, schoolTxt, birthTxt, genderTxt, relationshipTxt;
    Dialog dialog;
    EditText inputInformation;
    RecyclerView recycler_view_profile;
    PersonalProfileRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        setContent();
        getData();
        setupRecyclerView();
        listenNer();
    }
    void listenNer(){
        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
        infoTxt.setOnClickListener(v -> {
            infoDialog();
        });
    }


    void setupRecyclerView(){
        Query query = FirebaseUtil.postStory()
                .whereEqualTo("idAuthor", FirebaseUtil.currentUserID())
                .orderBy("postTimestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<PostStoryModel> options = new FirestoreRecyclerOptions.Builder<PostStoryModel>()
                .setQuery(query,PostStoryModel.class).build();

        adapter = new PersonalProfileRecyclerAdapter(options,this);
        recycler_view_profile.setLayoutManager(new LinearLayoutManager(this));
        recycler_view_profile.setAdapter(adapter);
        adapter.startListening();
    }


    void getData(){
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {

            currentUserModel = task.getResult().toObject(UserModel.class);
            userNameTxt.setText(currentUserModel.getUsername());
            birthTxt.setText(currentUserModel.getBirthDay());
            addressTxt.setText(currentUserModel.getAddress());
            schoolTxt.setText(currentUserModel.getSchool());
            genderTxt.setText(currentUserModel.getGender());
            relationshipTxt.setText(currentUserModel.getRelationship());
            infoTxt.setText(currentUserModel.getInformation());



        });
        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()){

                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(this, uri, avatarImg);
                    }

                });
    }

    void infoDialog(){
        dialog = new Dialog(PersonalProfileActivity.this);
        dialog.setContentView(R.layout.custom_dialog_infomation);
        Drawable customBackground  = ContextCompat.getDrawable(this, R.drawable.dialog_backgroud);
        dialog.getWindow().setBackgroundDrawable(customBackground);
        TextView no = dialog.findViewById(R.id.cancelBtn);
        TextView yes = dialog.findViewById(R.id.successBtn);
        inputInformation = dialog.findViewById(R.id.inputInformation);
        no.setOnClickListener(v -> {
            dialog.dismiss();
        });

        yes.setOnClickListener(v -> {
            String information = inputInformation.getText().toString();
            FirebaseUtil.currentUserDetails().update("information", information);
            dialog.dismiss();
        });

        dialog.show();
    }



    void setContent(){
        coverPhotoImageView = findViewById(R.id.coverPhotoImageView);
        backBtn = findViewById(R.id.imageBack);
        avatarImg = findViewById(R.id.profilePhotoImageView);
        userNameTxt = findViewById(R.id.usernameTextView);
        infoTxt = findViewById(R.id.info);
        addressTxt = findViewById(R.id.addressTxt);
        schoolTxt = findViewById(R.id.schooTxt);
        birthTxt = findViewById(R.id.birthTxt);
        genderTxt = findViewById(R.id.genderTxt);
        relationshipTxt = findViewById(R.id.loveTxt);
        recycler_view_profile = findViewById(R.id.recycler_view_profile);
    }

}