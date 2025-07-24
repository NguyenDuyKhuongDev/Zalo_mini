package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ChatGroupActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.GroupModel;
import com.example.myapplication.model.PreferenceManager;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;
import java.util.Map;

public class GroupChatRecyclerAdapter extends FirestoreRecyclerAdapter<GroupModel, GroupChatRecyclerAdapter.GroupModelViewHolder> {
    Context context;
    private PreferenceManager preferenceManager;
    String otherIDImage;
    public GroupChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<GroupModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull GroupModelViewHolder holder, int position, @NonNull GroupModel model) {
        preferenceManager =new PreferenceManager(context);
        String documentId = getSnapshots().getSnapshot(position).getId();
        boolean lastMessageSentByMe = model.getLastUserIdSend().equals(preferenceManager.getString(FirebaseUtil.KEY_USER_ID));

        if (lastMessageSentByMe){
            if (model.getLastMessage().contains("###sendImage%&*!")) {
                holder.lastMessageText_group.setText("Bạn : Đã gửi một hình ảnh");
            }else if (model.getLastMessage().contains("###sendVideo%&*!")){
                holder.lastMessageText_group.setText("Bạn : Đã gửi một video");
            }else if (model.getLastMessage().contains("###sendDocument%&*!")){
                holder.lastMessageText_group.setText("Bạn : Đã gửi một tệp");
            }else {
                holder.lastMessageText_group.setText("Bạn : "+model.getLastMessage());
            }
        }else {
            if (model.getLastMessage().contains("###sendImage%&*!")) {
                holder.lastMessageText_group.setText("Đã gửi một hình ảnh");
            }else if (model.getLastMessage().contains("###sendVideo%&*!")){
                holder.lastMessageText_group.setText("Đã gửi một video");
            }else if (model.getLastMessage().contains("###sendDocument%&*!")){
                holder.lastMessageText_group.setText("Đã gửi một tệp");
            }else {
                holder.lastMessageText_group.setText(model.getLastMessage());
            }
        }

        holder.usernameText_group.setText(model.getGroupName());
        holder.lastMessageTime_group.setText(FirebaseUtil.timestampToString(model.getTimestamp()));

        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnSuccessListener(uri -> AndroidUtil.setProfilePic(context, uri, holder.imageProfile1))
                .addOnFailureListener(e -> {
                });

        StatusRead(model.getStatusRead(), holder.usernameText_group,  holder.lastMessageText_group, holder.lastMessageTime_group, lastMessageSentByMe );
        holder.itemView.setOnClickListener(v -> {

                if (lastMessageSentByMe){
                    GroupModel groupModel = new GroupModel();
                    Intent intent = new Intent(context, ChatGroupActivity.class);
                    intent.putExtra("groups", groupModel);
                    intent.putExtra("key_id", documentId);
                    intent.putExtra("key_image", otherIDImage);
                    context.startActivity(intent);
                }else{
                    FirebaseUtil.updateLastReadGroup(documentId).update("statusRead", "1");
                    GroupModel groupModel = new GroupModel();
                    Intent intent = new Intent(context, ChatGroupActivity.class);
                    intent.putExtra("groups", groupModel);
                    intent.putExtra("key_id", documentId);
                    intent.putExtra("key_image", otherIDImage);
                    context.startActivity(intent);
                }


        });
        getIdOtherImage(documentId, holder.imageProfile2);

    }

    private void StatusRead (String Status, TextView name,TextView message, TextView timeTamp, Boolean lastMessageSentByMe){
        if (Status != null)
        {
            if (Status.equals("0") && !lastMessageSentByMe)
            {
                name.setTypeface(null, Typeface.BOLD);
                name.setTextColor(Color.BLACK);
                message.setTextColor(Color.rgb(40,167,241));
                timeTamp.setTextColor(Color.rgb(40,167,241));

            }else {
                name.setTypeface(null, Typeface.BOLD);
                name.setTextColor(Color.rgb(117, 117, 117));
                message.setTextColor(Color.rgb(117, 117, 117));
                timeTamp.setTextColor(Color.rgb(117, 117, 117));
            }
        }
    }

    void getIdOtherImage(String groupId ,RoundedImageView imageView){
        FirebaseUtil.groups().document(groupId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){
                        List<String> userIds = (List<String>) documentSnapshot.get("userIds");
                        if (userIds != null && !userIds.isEmpty()) {
                            if (userIds.size() > 1) {
                                String Id = userIds.get(1);
                                setImageOther(imageView, Id);
                                otherIDImage = Id;
                            }else {
                                System.out.println("cần phải có 2 phân tủ tro len");
                                return;
                            }

                        } else {
                            System.out.println("array null");
                        }
                    }else {
                        System.out.println("ko thay document");
                    }
                }else {
                    System.out.println("Lỗi" + task.getException());
                }
            }
        });
    }

    void setImageOther(RoundedImageView imageView, String sendId){
        if (sendId != null)
        {
            FirebaseUtil.getOtherProfilePicStorageRef(sendId).getDownloadUrl()
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                Uri uri = task.getResult();
                                AndroidUtil.setProfilePic(context, uri, imageView);
                            }
                        }
                    });
        }



    }

    @NonNull
    @Override
    public GroupModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_list_group_row,parent,false);
        return new GroupModelViewHolder(view);
    }

    static class GroupModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText_group;
        TextView lastMessageText_group;
        TextView lastMessageTime_group;
        RoundedImageView imageProfile1, imageProfile2;
        RoundedImageView statusOnline_group;
        public GroupModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText_group = itemView.findViewById(R.id.textViewName_group);
            lastMessageText_group = itemView.findViewById(R.id.last_message_text_group);
            lastMessageTime_group = itemView.findViewById(R.id.last_message_time_text_group);
            imageProfile1 = itemView.findViewById(R.id.imageProfile1);
            imageProfile2 = itemView.findViewById(R.id.imageProfile2);
            statusOnline_group = itemView.findViewById(R.id.imageOnline_group);
        }
    }
}
