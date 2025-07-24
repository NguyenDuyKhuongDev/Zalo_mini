package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

public class InfoGroupActivity extends AppCompatActivity {


    //update thông tin nhóm chát
    ImageButton backBtn ,popup_delete, add_persion_group;
    String documentId;
    Context context;
    TextView nameInfoTxt, diss_group, leave_group, successBtn, cancelBtn;
    private PreferenceManager preferenceManager;
    Dialog dialog;
    FragmentManager fragment;
    GroupFragment groupFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
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
        Toast.makeText(context, documentId, Toast.LENGTH_SHORT).show();

        getDataGroup();


//        FirebaseUtil.groups().document(documentId).get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()){
//                DocumentSnapshot documentSnapshot = task.getResult();
//                nameInfoTxt.setText(documentSnapshot.getString("groupName"));
//
//            }
//        });
        setListener();
    }

    void setListener(){
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
                switch (menuItem.getItemId()) {
                    case R.id.action_delete_chat:
                        return true;
                    default:
                        return false;
                }
            });

            popupMenu.show();
        });
        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
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
                        }else {
                            leave_group.setVisibility(View.VISIBLE);
                            diss_group.setVisibility(View.GONE);
                        }

                    }
                }
            }
        });
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

            FirebaseUtil.documentGroup(documentId).update("userIds", FieldValue.arrayRemove(preferenceManager.getString(FirebaseUtil.KEY_USER_ID)))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dialog.dismiss();

                            onBackPressed();

                        }
                    }).addOnFailureListener(e -> {
                        showToast("Rời nhóm thất bại"+e.getMessage());
                    });
        });
        dialog.show();
    }
    private void displayFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, fragment);

        transaction.show(fragment);
        transaction.commit();
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}