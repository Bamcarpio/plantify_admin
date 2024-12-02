package com.example.plantify_admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.plantify_admin.R;
import com.example.plantify_admin.model.OrderModel;

import java.util.ArrayList;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.ItemHolder> {


    ArrayList<OrderModel> orderList;
    Context context;

    onItemClickListener onItemClickListener;

    public void setOnItemClickListener(onItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public interface  onItemClickListener {

         void onClick(OrderModel orderModel);

    }

    public DeliveryAdapter(  Context context, ArrayList<OrderModel> orderList){
        this.orderList = orderList;
        this.context = context;
    }


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_card,parent,false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        OrderModel orderModel = orderList.get(position);
        holder.itemView.setOnClickListener(v-> onItemClickListener.onClick(orderList.get(position)));
        holder.onBind(orderModel);

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


    public static  class ItemHolder extends RecyclerView.ViewHolder{

        TextView orderId,orderStatus;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.orderId);
            orderStatus = itemView.findViewById(R.id.orderStatus);
        }
        public void onBind(OrderModel orderModel){
                orderId.setText(orderModel.getKey());
                orderStatus.setText(orderModel.getStatus());
        }

    }
}
