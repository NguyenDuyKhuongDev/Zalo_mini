package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Fragment.ChatFragment;
import com.example.myapplication.Fragment.GroupFragment;
import com.example.myapplication.Fragment.ProfileFragment;
import com.example.myapplication.FriendFragment.FriendFragment;
import com.example.myapplication.adapter.GroupChatRecyclerAdapter;
import com.example.myapplication.model.GroupModel;
import com.example.myapplication.model.PreferenceManager;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;

import com.example.myapplication.utils.Permission;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import BotAi.BotActivity;

public class MainActivity extends BaseActivity  {
    private PreferenceManager preferenceManager;
    BottomNavigationView bottomNavigationView;
    ImageButton searchButton, menuBtn, groupBtn, group_addBtn;
    DrawerLayout drawerLayout;
    ChatFragment chatFragment;
    GroupFragment groupFragment;
    ProfileFragment profileFragment;
    FriendFragment phonebook;
    NavigationView navigationView;
    TextView textView, text_status_internet;
    ImageView  internet_status;
    RoundedImageView imageView;
    UserModel userModel;
    NotificationBadge badgeGroup;
    GroupChatRecyclerAdapter adapter;
    Context context;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int CHECK_INTERVAL = 3000;
    Permission permission;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatFragment = new ChatFragment();
        context = this;
        preferenceManager =new PreferenceManager(context);
        profileFragment = new ProfileFragment();
        phonebook = new FriendFragment();
        groupFragment = new GroupFragment();
        searchButton = findViewById(R.id.main_search_btn);
        menuBtn = findViewById(R.id.main_menu_btn);
        drawerLayout =findViewById(R.id.drawrlayout);
        groupBtn = findViewById(R.id.main_groupBtn);
        badgeGroup = findViewById(R.id.badgeGroup);
        group_addBtn = findViewById(R.id.main_group_addBtn);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        internet_status = findViewById(R.id.internet_status);
        text_status_internet = findViewById(R.id.text_status_internet);
        permission = new Permission(context);


        searchButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, SearchUserActivity.class));

        });
        groupBtn.setOnClickListener(v -> {displayFragment(groupFragment);});
        group_addBtn.setOnClickListener(v -> {showDialogCreateGroup();});
        menuBtn.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        permission.checkShowNotificationPermission(MainActivity.this);
        permission.checkConnectInternet(internet_status, text_status_internet);
        navigationView();
        bottomNavigationView();
        getDataHeaderNav();
        getFCMToken();

        checkData();

    }

    void checkData(){
        String[] keys = {
                FirebaseUtil.KEY_USER_ID,
                FirebaseUtil.KEY_USER_NAME,
                FirebaseUtil.KEY_PHONE,
                FirebaseUtil.KEY_TOKEN,
                FirebaseUtil.KEY_EMAIL
        };

        for (String key : keys) {
            String value = preferenceManager.getString(key);

            Log.d("PreferenceManager", key + ": " + value);
        }
    }
    private void showDialogCreateGroup(){
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dialog_create_group_layout);
        Drawable customBackground  = ContextCompat.getDrawable(this, R.drawable.dialog_backgroud);
        dialog.getWindow().setBackgroundDrawable(customBackground);
        TextView no = dialog.findViewById(R.id.btn_no);
        TextView yes = dialog.findViewById(R.id.btn_yes);
        EditText text = dialog.findViewById(R.id.text);
        no.setOnClickListener(v -> {
            dialog.dismiss();
        });

        yes.setOnClickListener(v -> {
            if (text.getText().toString().isEmpty()) {
                showToast("Bạn chưa đặt tên cho nhóm chat!");
            }else {

                HashMap<String, Object> group = new HashMap<>();
                group.put("lastUserIdSend", preferenceManager.getString(FirebaseUtil.KEY_USER_ID));
                group.put("groupName", text.getText().toString());
                group.put("lastMessage", preferenceManager.getString(FirebaseUtil.KEY_USER_NAME) + " vừa tạo một nhóm mới");
                group.put("timestamp", Timestamp.now());
                group.put("adminID", preferenceManager.getString(FirebaseUtil.KEY_USER_ID));
                List<String> userIds = new ArrayList<>();
                userIds.add(preferenceManager.getString(FirebaseUtil.KEY_USER_ID));
                group.put("userIds", userIds);

                FirebaseUtil.groups().add(group).addOnSuccessListener(documentReference -> {

                    HashMap<String, Object> myUser = new HashMap<>();
                    myUser.put(FirebaseUtil.KEY_USER_ID, preferenceManager.getString(FirebaseUtil.KEY_USER_ID));
                    myUser.put(FirebaseUtil.KEY_USER_NAME, preferenceManager.getString(FirebaseUtil.KEY_USER_NAME));
                    myUser.put(FirebaseUtil.KEY_PHONE, preferenceManager.getString(FirebaseUtil.KEY_PHONE));
                    myUser.put(FirebaseUtil.KEY_TOKEN, preferenceManager.getString(FirebaseUtil.KEY_TOKEN));
                    myUser.put("positionMember", "Trưởng nhóm");
                    myUser.put("admin", preferenceManager.getString(FirebaseUtil.KEY_USER_ID));

                    FirebaseUtil.groups().document(documentReference.getId()).collection("members").document(preferenceManager.getString(FirebaseUtil.KEY_USER_ID)).set(myUser).addOnSuccessListener(documentReference1 -> {
                        dialog.dismiss();
                        HashMap<String, Object> message = new HashMap<>();
                        message.put("senderId", "##########################~~");
                        message.put("senderName", "Admin");
                        message.put("senderImage", "Admin");
                        message.put("message", preferenceManager.getString(FirebaseUtil.KEY_USER_NAME) + " vừa tạo một nhóm mới.");
                        message.put("dataTime", Timestamp.now());
                        message.put("fileName", "null");
                        message.put("files", "0");
                        message.put("images", "0");
                        message.put("sizeFile", "null");
                        message.put("videos", "0");

                        FirebaseUtil.group_chats(documentReference.getId()).add(message);

                        GroupModel groupModel = new GroupModel();

                        Intent intent = new Intent(this, ChatGroupActivity.class);
                        intent.putExtra("groups", groupModel);
                        intent.putExtra("key_id", documentReference.getId());
                        startActivity(intent);


                    });
                });

            }
        });
        dialog.show();
    }
    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    void getDataHeaderNav(){
        navigationView = findViewById(R.id.navigationview);
        View headerView = navigationView.getHeaderView(0);
        imageView = headerView.findViewById(R.id.imageViewUser);
        textView = headerView.findViewById(R.id.nameUser);


        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {

            userModel = task.getResult().toObject(UserModel.class);
            textView.setText(userModel.getUsername());


        });
        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(MainActivity.this, uri, imageView);
                    }
                });

    }

    void navigationView(){

        navigationView = findViewById(R.id.navigationview);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (drawerLayout != null){
                    drawerLayout.closeDrawers();
                }
                if (item.getItemId() == R.id.navChat){
                    displayFragment(chatFragment);

                }
                if (item.getItemId() == R.id.navChatGpt){
                    Intent intent = new Intent(MainActivity.this, BotActivity.class);
                    startActivity(intent);

                }
                if (item.getItemId() == R.id.navAbout){
                }
                return true;

            }

        });


    }
    private void displayFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, fragment);
        transaction.hide(phonebook);
        transaction.hide(profileFragment);
        transaction.hide(groupFragment);
        transaction.hide(chatFragment);
        transaction.show(fragment);
        transaction.commit();
    }

    void bottomNavigationView(){


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_chat){
                    groupBtn.setVisibility(View.VISIBLE);
                   // getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment).commit();
                    displayFragment(chatFragment);
                    getFriendRequest();
                }
                if (item.getItemId() == R.id.menu_phonebook){
                    displayFragment(phonebook);
                    getFriendRequest();
                }

                if (item.getItemId() == R.id.menu_profile){
                  //  getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, profileFragment).commit();
                    displayFragment(profileFragment);
                    getFriendRequest();
                }

                return true;

            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);
        getFriendRequest();

    }
    void getFriendRequest(){
        if (FirebaseUtil.currentUserID() == null){
            return;
        }
        FirebaseUtil.checkRequestFriend(FirebaseUtil.currentUserID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()){
                int countRequests = 0;
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                    countRequests++;
                }
                if (countRequests !=0){
                    BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.menu_phonebook);
                    badgeDrawable.setVisible(true);
                    badgeDrawable.setNumber(countRequests);



                }

            }else {
                bottomNavigationView.removeBadge(R.id.menu_phonebook);
            }
        });
    }
    private final Runnable netWork = new Runnable() {
        @Override
        public void run() {
            permission.checkConnectInternet(internet_status, text_status_internet);
            handler.postDelayed(this, CHECK_INTERVAL);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        handler.post(netWork);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.post(netWork);
    }

    void getFCMToken(){

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            String token = task.getResult();
            Log.i("My token", token);
            FirebaseUtil.currentUserDetails().update("fcmToken", token);
        });
    }
}