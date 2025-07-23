package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.myapplication.adapter.GroupAddUserSearchAdapter;
import com.example.myapplication.adapter.GroupAddUserSuRecyclerAdapter;
import com.example.myapplication.adapter.SearchUserRecyclerAdapter;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class GroupAddUserActivity extends AppCompatActivity {
 RecyclerView recyclerView, search_recycler_view;
    ImageButton backBtn, searchBtn;
    EditText searchEditText;
    GroupAddUserSuRecyclerAdapter adapter;
    GroupAddUserSearchAdapter adapterSearch;
    UserModel userModel;
    String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_search);
        setContent();
        setUpRecyclerView();
        edittextChanged();
        listenNer();
    }

    void edittextChanged(){
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 2) {
                    search_recycler_view.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    search_recycler_view.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    void listenNer(){
        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
        searchBtn.setOnClickListener(v -> {
            String searchTerm = searchEditText.getText().toString();
            if(searchTerm.isEmpty() || searchTerm.length()<3){
                searchEditText.setError("Invalid Username");
                return;
            }
            setupSearchRecyclerView(searchTerm);
        });
    }
    void setContent(){
        searchEditText = findViewById(R.id.seach_username_input);
        backBtn = findViewById(R.id.back_btn);
        searchBtn = findViewById(R.id.search_user_btn);
        recyclerView = findViewById(R.id.friend_recycler_view);
        userModel = new UserModel();
        search_recycler_view = findViewById(R.id.search_recycler_view);
        documentId = getIntent().getStringExtra("key_id");
        //Toast.makeText(this, documentId, Toast.LENGTH_SHORT).show();
    }
    void setUpRecyclerView(){
        Query query = FirebaseUtil.friend(FirebaseUtil.currentUserID());
        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query,UserModel.class).build();

        adapter = new GroupAddUserSuRecyclerAdapter(options, documentId, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }
    void setupSearchRecyclerView(String searchTerm){
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username",searchTerm)
                .whereLessThanOrEqualTo("username",searchTerm+'\uf8ff');

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query,UserModel.class).build();

        adapterSearch = new GroupAddUserSearchAdapter(options,documentId, this);
        search_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        search_recycler_view.setAdapter(adapterSearch);
        adapterSearch.startListening();


    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapterSearch!=null)
            adapterSearch.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        if(adapterSearch!=null)
            adapterSearch.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapterSearch!=null)
            adapterSearch.notifyDataSetChanged();

    }
   
}