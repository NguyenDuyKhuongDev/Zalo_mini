package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.adapter.MembersGroupSeeRecyclerAdapter;
import com.example.myapplication.model.MembersGroupModel;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.test.MemberGroupOnClickListener;
import com.example.myapplication.bottomSheet.MembersGroupBottomSheet;
import com.example.myapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class GroupMembersSeeActivity extends AppCompatActivity {

    TextView quantityMembers;
    RecyclerView members_recycler_view;
    ImageButton back_btn;
    MembersGroupSeeRecyclerAdapter adapter;
    UserModel userModel;
    String documentId;
    MemberGroupOnClickListener listener;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members_see);
        setContent();
        setListener();
        setRecyclerView();



    }


    private void setListener(){
        listener = (position, userId, PositionMember, admin) -> {
            MembersGroupBottomSheet lBottomSheet = new MembersGroupBottomSheet( documentId, context, userId, position, PositionMember, admin);
            lBottomSheet.show(getSupportFragmentManager(), MembersGroupBottomSheet.TAG);
        };




        back_btn.setOnClickListener(v -> {
            onBackPressed();
        });
    }
    private void setRecyclerView(){
        Query query = FirebaseUtil.groupMemberList(documentId);
        FirestoreRecyclerOptions<MembersGroupModel> options = new FirestoreRecyclerOptions.Builder<MembersGroupModel>()
                .setQuery(query,MembersGroupModel.class).build();
        adapter = new MembersGroupSeeRecyclerAdapter(options ,context, listener, getSupportFragmentManager());
        members_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        members_recycler_view.setAdapter(adapter);
        adapter.startListening();
    }


    private void setContent(){
        quantityMembers = findViewById(R.id.quantityMembers);
        members_recycler_view = findViewById(R.id.members_friend_recycler_view);
        back_btn = findViewById(R.id.back_btn);
        documentId = getIntent().getStringExtra("key_id");
        userModel = new UserModel();
        context =this;
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}