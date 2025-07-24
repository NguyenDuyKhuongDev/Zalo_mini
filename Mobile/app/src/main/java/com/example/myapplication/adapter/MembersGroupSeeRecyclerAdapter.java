package com.example.myapplication.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.MembersGroupModel;
import com.example.myapplication.test.MemberGroupOnClickListener;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MembersGroupSeeRecyclerAdapter extends FirestoreRecyclerAdapter<MembersGroupModel, MembersGroupSeeRecyclerAdapter.MembersGroupSeeViewHolder> {
    Context context;
    private final MemberGroupOnClickListener listener;
    FragmentManager fragmentManager;

    public MembersGroupSeeRecyclerAdapter(@NonNull FirestoreRecyclerOptions<MembersGroupModel> options, Context context, MemberGroupOnClickListener listener, FragmentManager fragmentManager) {
        super(options);
        this.context = context;
        this.listener = listener;
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected void onBindViewHolder(@NonNull MembersGroupSeeViewHolder holder, int position, @NonNull MembersGroupModel model) {

        holder.itemView.setOnClickListener(v -> {
            if (listener != null){
                listener.onButtonClick(position, model.getUserId(), model.getPositionMember(), model.getAdmin());
            }
        });
        holder.user_name_text.setText(model.getUsername());
        holder.positionMembers.setText(model.getPositionMember());

        FirebaseUtil.getOtherProfilePicStorageRef(model.getUserId()).getDownloadUrl().
                addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        Uri uri = task1.getResult();
                        AndroidUtil.setProfilePic(context, uri, holder.profile_pic_image_view);
                    }

                });
    }

    @NonNull
    @Override
    public MembersGroupSeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_members_group,parent,false);
        return new MembersGroupSeeRecyclerAdapter.MembersGroupSeeViewHolder(view);
    }

    static class MembersGroupSeeViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_pic_image_view;
        TextView user_name_text, positionMembers;

        public MembersGroupSeeViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_pic_image_view = itemView.findViewById(R.id.profile_pic_image_view);
            user_name_text = itemView.findViewById(R.id.user_name_text);
            positionMembers = itemView.findViewById(R.id.positionMembers);
        }
    }
}
