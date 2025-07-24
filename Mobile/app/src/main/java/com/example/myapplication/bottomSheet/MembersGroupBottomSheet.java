package com.example.myapplication.bottomSheet;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.myapplication.InfoGroupActivity;
import com.example.myapplication.R;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;

public class MembersGroupBottomSheet extends BottomSheetDialogFragment {
    Context context;
    TextView user_name_text, personal, deleteMemberGroup;
    ImageView profile_pic_image_view;
    ImageButton cancel_button;
    int position;
    String groupId;
    String userId, memberName, PositionMember;
    String admin;

    public static final String TAG = "MembersGroupBottomSheet";

    public MembersGroupBottomSheet(String groupID, Context applicationContext, String userId, int position, String PositionMember, String admin) {
        this.context = applicationContext;
        this.groupId = groupID;
        this.userId = userId;
        this.position = position;
        this.PositionMember = PositionMember;
        this.admin = admin;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_members_group, container, false);
        profile_pic_image_view = view.findViewById(R.id.profile_pic_image_view);
        user_name_text = view.findViewById(R.id.user_name_text);
        personal= view.findViewById(R.id.personal);
        deleteMemberGroup = view.findViewById(R.id.deleteMemberGroup);
        cancel_button = view.findViewById(R.id.cancel_button);
        if (userId != null && groupId != null){
            isAdmin();

            setData();
            setListener();

        }
        return view;
    }
    private  void isAdmin(){
        boolean user = PositionMember.contains("Thành viên");
        if (userId.equals(admin) || !user ){
            deleteMemberGroup.setVisibility(View.GONE);
        }else {
            deleteMemberGroup.setVisibility(View.VISIBLE);

        }
    }
    public boolean checkIfUserIsMember() {
        return PositionMember.contains("Thành viên");
    }
    private void setData(){
        FirebaseUtil.getOtherProfilePicStorageRef(userId).getDownloadUrl().
                addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        Uri uri = task1.getResult();
                        AndroidUtil.setProfilePic(context, uri, profile_pic_image_view);
                    }

                });
        FirebaseUtil.groupMemberList(groupId).document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                memberName = documentSnapshot.getString(FirebaseUtil.KEY_USER_NAME);
                user_name_text.setText(documentSnapshot.getString(FirebaseUtil.KEY_USER_NAME));
            }
        });
    }


    private void setListener(){

        deleteMemberGroup.setOnClickListener(v -> {
            deleteMember();
        });

        personal.setOnClickListener(v -> {

        });
        cancel_button.setOnClickListener(v -> {
            dismiss();
        });
    }



    private void deleteMember(){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_leave_group);
        Drawable customBackground  = ContextCompat.getDrawable(context, R.drawable.dialog_backgroud);
        dialog.getWindow().setBackgroundDrawable(customBackground);
        TextView successBtn = dialog.findViewById(R.id.successBtn);
        TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);
        TextView view1 = dialog.findViewById(R.id.view1);
        TextView view2 = dialog.findViewById(R.id.view2);
        view1.setText("Bạn muốn xóa người này ra khỏi nhóm ?");
        view2.setText(memberName);

        cancelBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });

        successBtn.setOnClickListener(v -> {
          updateMemberGroup(dialog);
          dialog.dismiss();
        });

        dialog.show();
    }
    private void updateMemberGroup(Dialog dialog){
        FirebaseUtil.groupMemberList(groupId).document(userId).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FirebaseUtil.documentGroup(groupId).update("userIds", FieldValue.arrayRemove(userId))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                updateNotification();
                                dialog.dismiss();
                                dismiss();
                            }
                        }).addOnFailureListener(e -> {
                            AndroidUtil.showToast(context, "Rời nhóm thất bại"+e.getMessage());
                        });
            }
        }).addOnFailureListener(e -> {
           AndroidUtil.showToast(context, "Xóa thành viên thất bại");
        });
    }
    private void updateNotification(){
        HashMap<String, Object> message = new HashMap<>();
        message.put("senderId", "##########################~~");
        message.put("senderName", "Admin");
        message.put("senderImage", "Admin");
        message.put("message", memberName + " đã bị xóa khỏi nhóm.");
        message.put("dataTime", Timestamp.now());
        message.put("fileName", "null");
        message.put("files", "0");
        message.put("images", "0");
        message.put("sizeFile", "null");
        message.put("videos", "0");
        FirebaseUtil.group_chats(groupId).add(message).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                HashMap<String, Object> messUpdate = new HashMap<>();
                messUpdate.put("lastMessage", memberName+" đã bị xóa khỏi nhóm.");
                messUpdate.put("timestamp", Timestamp.now());
                FirebaseUtil.groups().document(groupId).update(messUpdate);
            }
        }).addOnFailureListener(e -> {
            AndroidUtil.showToast(context ,"rời nhóm thất bại"+e.getMessage());
        });
    }
}
