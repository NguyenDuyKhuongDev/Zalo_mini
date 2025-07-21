package com.example.myapplication.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.FileUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.MainActivity;
import com.example.myapplication.PersonalProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.SplashActivity;
import com.example.myapplication.UpdatePersonalActivity;
import com.example.myapplication.model.PreferenceManager;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class ProfileFragment extends Fragment {

    RoundedImageView profilePic;


    TextView logoutBtn, change_info_user;
    PreferenceManager preferenceManager;
    UserModel currentUserModel;
    RelativeLayout pagePersonalBtn;
    RelativeLayout change_info;
    TextView userName;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_profile, container, false);
            setContent(view);
            getUserData();
            listenNer();
            return view;
    }


    void listenNer(){
        pagePersonalBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PersonalProfileActivity.class);
            startActivity(intent);
        });
        change_info.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UpdatePersonalActivity.class);
            startActivity(intent);

        });
        logoutBtn.setOnClickListener(view12 -> {
            sigOutDialog();
        });
    }
    void getUserData(){
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            currentUserModel = task.getResult().toObject(UserModel.class);
            userName.setText(currentUserModel.getUsername());


        });
        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Uri uri = task.getResult();
                        Context context = getContext();
                        if (context != null){
                            AndroidUtil.setProfilePic(context, uri, profilePic);
                        }else {
                            return;
                        }

                    }

                });
    }


    void sigOutDialog(){
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog_sign_out);
        Drawable customBackground  = ContextCompat.getDrawable(getContext(), R.drawable.dialog_backgroud);
        dialog.getWindow().setBackgroundDrawable(customBackground);
        TextView no = dialog.findViewById(R.id.cancelBtn);
        TextView yes = dialog.findViewById(R.id.successBtn);

        no.setOnClickListener(v -> {
            dialog.dismiss();
        });
        yes.setOnClickListener(v -> {
            FirebaseMessaging.getInstance().deleteToken()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()){
                            dialog.dismiss();
                            preferenceManager.clear();
                            FirebaseUtil.logout();
                            Intent intent = new Intent(getContext(), SplashActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });

        });
        dialog.show();

    }
    void setContent(View view){
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        logoutBtn = view.findViewById(R.id.logout_btn);
        profilePic = view.findViewById(R.id.avatar);
        change_info_user = view.findViewById(R.id.change_info_user);
        pagePersonalBtn = view.findViewById(R.id.pagePersonal);
        userName = view.findViewById(R.id.usernameTextView);
        change_info = view.findViewById(R.id.change_info);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ImageButton groupBtn = getActivity().findViewById(R.id.main_groupBtn);
            ImageButton group_addBtn = getActivity().findViewById(R.id.main_group_addBtn);
            if (groupBtn != null && group_addBtn != null) {
                groupBtn.setVisibility(View.VISIBLE);
                group_addBtn.setVisibility(View.GONE);
            }
        }
    }
}