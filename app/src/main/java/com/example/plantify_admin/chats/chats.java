package com.example.plantify_admin.chats;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plantify_admin.R;
import com.example.plantify_admin.adapter.ChatCardAdapter;
import com.example.plantify_admin.model.userChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class chats extends Fragment {


    public chats() {
        // Required empty public constructor
    }

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private RecyclerView chatCardList;
    private ChatCardAdapter adapter;
    private ArrayList<userChatModel> chatList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);


        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        chatCardList = view.findViewById(R.id.chatCardList);
        chatList = new ArrayList<>();

        chatCardList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatCardAdapter(getContext(),chatList);

        chatCardList.setAdapter(adapter);
        firebaseDatabase.getReference("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot ds: snapshot.getChildren()){
                   userChatModel userChatModel = ds.getValue(userChatModel.class);
                   chatList.add(userChatModel);
                   userChatModel.setKey(ds.getKey());
               }
               adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter.setOnItemClickListener(new ChatCardAdapter.onItemClickListener() {
            @Override
            public void onclick(userChatModel userChatModel) {
                setFragment(new chat_inside(userChatModel.getUserid_2(), userChatModel.getKey()));
            }
        });


        return view;
    }



    private void setFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main,fragment);
        fragmentTransaction.commit();

    }
}