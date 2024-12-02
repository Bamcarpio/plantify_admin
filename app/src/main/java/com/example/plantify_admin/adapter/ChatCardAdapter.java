package com.example.plantify_admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantify_admin.R;
import com.example.plantify_admin.model.userChatModel;

import java.util.ArrayList;

public class ChatCardAdapter extends RecyclerView.Adapter<ChatCardAdapter.ItemHolder> {

    private Context context;
    private ArrayList<userChatModel> chatList;

    private onItemClickListener onItemClickListener;


    public interface  onItemClickListener{
        void onclick(userChatModel userChatModel);
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }


    public ChatCardAdapter( Context context,ArrayList<userChatModel> chatList){
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatCardAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_chat,parent,false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatCardAdapter.ItemHolder holder, int position) {
            userChatModel userChatModel = chatList.get(position);
            holder.onBind(userChatModel);
            holder.itemView.setOnClickListener(v-> onItemClickListener.onclick(chatList.get(position)));

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder{
        TextView chatid;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            chatid = itemView.findViewById(R.id.chatid);

        }
        public void onBind(userChatModel userChatModel){
            chatid.setText(userChatModel.getKey());
        }
    }


}
