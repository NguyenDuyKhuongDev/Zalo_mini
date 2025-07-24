package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserPhoneNumberAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserPhoneNumberAdapter.UserModelViewHolder> {
    Context context;
    String nameText = "(Tôi)";
    String currentUserID = FirebaseUtil.currentUserID();
    TextView notification;
    public SearchUserPhoneNumberAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context, TextView notification) {
        super(options);
        this.context = context;
        this.notification = notification;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        holder.usernameText.setText(model.getUsername());
        holder.phoneText.setText(model.getPhone());
        if (model.getUserId().equals(currentUserID)){
            holder.usernameText.setText(model.getUsername()+nameText);
            holder.addFriendBtn.setVisibility(View.GONE);

        }
        FirebaseUtil.getOtherProfilePicStorageRef(model.getUserId()).getDownloadUrl().
                addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        Uri uri = task1.getResult();
                        AndroidUtil.setProfilePic(context, uri, holder.profilePic);
                    }

                });
        holder.itemView.setOnClickListener(view -> {

            Intent intent = new Intent(context, ProfileActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        });
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (getItemCount() == 0) {
            notification.setVisibility(View.VISIBLE);
        } else {
            notification.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_search_user_row, parent, false);
        return new SearchUserPhoneNumberAdapter.UserModelViewHolder(view);
    }

    static class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView phoneText, friendBtn;
        ImageView profilePic;
        Button addFriendBtn;
        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            phoneText = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
            addFriendBtn = itemView.findViewById(R.id.btn_addFriend);
            friendBtn = itemView.findViewById(R.id.btn_Friend);
        }
    }
}
