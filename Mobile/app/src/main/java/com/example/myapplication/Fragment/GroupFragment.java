package com.example.myapplication.Fragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.GroupChatRecyclerAdapter;
import com.example.myapplication.adapter.RecentChatRecyclerAdapter;
import com.example.myapplication.model.ChatroomModel;
import com.example.myapplication.model.GroupModel;
import com.example.myapplication.model.PreferenceManager;
import com.example.myapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import BotAi.MessageAdapter;


public class GroupFragment extends Fragment {
    RecyclerView recyclerView;
    GroupChatRecyclerAdapter adapter;
    private PreferenceManager preferenceManager;
    private String currentUserID = FirebaseUtil.currentUserID();
    private  List<GroupModel> groupsList ;
    SwipeRefreshLayout containerSwipe;
    public GroupFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_groupragment, container, false);
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        recyclerView = view.findViewById(R.id.recyler_viewChatGroup);
        containerSwipe = view.findViewById(R.id.swipe_container);
        containerSwipe.setOnRefreshListener(this::refreshData);
        containerSwipe.setColorSchemeColors(getResources().getColor(R.color.my_primary));
        refreshData();
       //Toast.makeText(getContext(), preferenceManager.getString(FirebaseUtil.KEY_USER_ID), Toast.LENGTH_SHORT).show();
        setupRecyclerView();
        return view;
    }

    void setupRecyclerView() {
        Query query = FirebaseUtil.groups()
                .whereArrayContains("userIds", preferenceManager.getString(FirebaseUtil.KEY_USER_ID));
        FirestoreRecyclerOptions<GroupModel> options = new FirestoreRecyclerOptions.Builder<GroupModel>()
                .setQuery(query, GroupModel.class).build();
        adapter = new GroupChatRecyclerAdapter(options, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    void refreshData() {
        containerSwipe.setRefreshing(true); // Bắt đầu refresh
        setupRecyclerView(); // Cập nhật dữ liệu
        containerSwipe.setRefreshing(false); // Kết thúc refresh
    }
    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ImageButton groupBtn = getActivity().findViewById(R.id.main_groupBtn);
            ImageButton group_addBtn = getActivity().findViewById(R.id.main_group_addBtn);
            if (groupBtn != null && group_addBtn != null) {
                groupBtn.setVisibility(View.GONE);
                group_addBtn.setVisibility(View.VISIBLE);
            }
        }
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }

}
