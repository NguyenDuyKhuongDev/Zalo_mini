package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Fragment.GroupFragment;
import com.example.myapplication.model.GroupModel;
import com.example.myapplication.model.PreferenceManager;
import com.example.myapplication.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;

public class InfoGroupActivity extends AppCompatActivity {
    ImageButton backBtn ,popup_delete, add_persion_group;
    String documentId;
    Context context;

    TextView nameInfoTxt, diss_group, leave_group, successBtn, cancelBtn, change_name, membersListSee;
    private PreferenceManager preferenceManager;
    Dialog dialog;
    FragmentManager fragment;
    GroupFragment groupFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_group);
        setContent();
        getDataGroup();

       setListener();
    }



    void setListener(){

        membersListSee.setOnClickListener(v -> {
            Intent intent = new Intent(this, GroupMembersSeeActivity.class);
            intent.putExtra("key_id", documentId);
            startActivity(intent);
        });
        leave_group.setOnClickListener(v -> {
            leaveGroup();
        });
        add_persion_group.setOnClickListener(v -> {

            Intent intent = new Intent(this, GroupAddUserActivity.class);
            intent.putExtra("key_id", documentId);
            startActivity(intent);

        });

        popup_delete.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(InfoGroupActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_info_delete, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                return menuItem.getItemId() == R.id.action_delete_chat;
            });

            popupMenu.show();
        });
        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
    }
    private void setMembersListSee(){




    }
    private void getDataGroup(){
        FirebaseUtil.documentGroup(documentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        GroupModel groupModel = document.toObject(GroupModel.class);
                        nameInfoTxt.setText(groupModel.getGroupName());
                        boolean leaveOrDiss = groupModel.getAdminID().equals(preferenceManager.getString(FirebaseUtil.KEY_USER_ID));
                        if (leaveOrDiss){
                            diss_group.setVisibility(View.VISIBLE);
                            leave_group.setVisibility(View.GONE);
                            change_name.setOnClickListener(v -> {
                                changeNameGroup();
                            });
                        }else {
                            change_name.setOnClickListener(v -> {
                                showToast("Dành cho admin");
                            });
                            leave_group.setVisibility(View.VISIBLE);
                            diss_group.setVisibility(View.GONE);
                        }

                    }
                }
            }
        });
    }
    private void changeNameGroup(){
        dialog = new Dialog(InfoGroupActivity.this);
        dialog.setContentView(R.layout.custom_dialog_change_name_group);
        Drawable customBackground  = ContextCompat.getDrawable(this, R.drawable.dialog_backgroud);
        dialog.getWindow().setBackgroundDrawable(customBackground);
        successBtn = dialog.findViewById(R.id.successBtn);
        cancelBtn = dialog.findViewById(R.id.cancelBtn);
        EditText inputNameGroup = dialog.findViewById(R.id.inputNameGroup);

        cancelBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });
        successBtn.setOnClickListener(v -> {
            String nameGroup = inputNameGroup.getText().toString().trim();
            if (nameGroup.isEmpty()){
                showToast("Không được để trống! ");
            }else {
                FirebaseUtil.documentGroup(documentId).update("groupName", nameGroup).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        showToast("Thay đổi tên nhóm thành"+ nameGroup);
                        nameInfoTxt.setText(nameGroup);
                    }
                });
                dialog.dismiss();
            }

        });


        dialog.show();
    }
    private void leaveGroup(){
        dialog = new Dialog(InfoGroupActivity.this);
        dialog.setContentView(R.layout.custom_leave_group);
        Drawable customBackground  = ContextCompat.getDrawable(this, R.drawable.dialog_backgroud);
        dialog.getWindow().setBackgroundDrawable(customBackground);
        successBtn = dialog.findViewById(R.id.successBtn);
        cancelBtn = dialog.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });
        successBtn.setOnClickListener(v -> {
            FirebaseUtil.groupMemberList(documentId).document(preferenceManager.getString(FirebaseUtil.KEY_USER_ID))
                    .delete().addOnCompleteListener(task -> {
                        FirebaseUtil.documentGroup(documentId).update(FirebaseUtil.KEY_USER_ID, FieldValue.arrayRemove(preferenceManager.getString(FirebaseUtil.KEY_USER_ID)))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        updateNotification();
                                        dialog.dismiss();
                                        onBackPressed();
                                    }
                                }).addOnFailureListener(e -> {
                                    showToast("Rời nhóm thất bại"+e.getMessage());
                                });
                    }).addOnFailureListener(e -> {
                       showToast("Xóa thất bại"+ e.getMessage());
                    });

        });
        dialog.show();
    }


    private void updateNotification(){
        HashMap<String, Object> message = new HashMap<>();
        message.put("senderId", "##########################~~");
        message.put("senderName", "Admin");
        message.put("senderImage", "Admin");
        message.put("message", preferenceManager.getString(FirebaseUtil.KEY_USER_NAME) + " vừa rời nhóm.");
        message.put("dataTime", Timestamp.now());
        message.put("fileName", "null");
        message.put("files", "0");
        message.put("images", "0");
        message.put("sizeFile", "null");
        message.put("videos", "0");
        FirebaseUtil.group_chats(documentId).add(message).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                HashMap<String, Object> messUpdate = new HashMap<>();
                messUpdate.put("lastMessage", preferenceManager.getString(FirebaseUtil.KEY_USER_NAME)+" vừa rời nhóm.");
                messUpdate.put("timestamp", Timestamp.now());
                FirebaseUtil.groups().document(documentId).update(messUpdate);
            }
        }).addOnFailureListener(e -> {
            showToast("rời nhóm thất bại"+e.getMessage());
        });
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void setContent(){
        context = this;
        preferenceManager = new PreferenceManager(getApplicationContext());
        groupFragment = new GroupFragment();
        backBtn = findViewById(R.id.back_btn_group);
        popup_delete = findViewById(R.id.popup_delete);
        add_persion_group = findViewById(R.id.add_persion_group);
        nameInfoTxt = findViewById(R.id.name_info);
        diss_group = findViewById(R.id.diss_group);
        leave_group = findViewById(R.id.leave_group);
        documentId = getIntent().getStringExtra("key_id");
        fragment = getFragmentManager();
        change_name = findViewById(R.id.change_name);
        membersListSee = findViewById(R.id.view_group);
    }
}