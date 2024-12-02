package com.example.plantify_admin.forDelivery;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.plantify_admin.adapter.DeliveryAdapter;
import com.example.plantify_admin.model.OrderModel;
import com.example.plantify_admin.orderDeliveries.Delivery_product_info;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class for_delivery extends Fragment {

   public for_delivery(){

   }

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DeliveryAdapter adapter;
    private ArrayList<OrderModel> orderList;
    private RecyclerView deliveryList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_for_delivery, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        String userid = firebaseAuth.getCurrentUser().getUid();

        deliveryList = view.findViewById(R.id.deliveryList);

        deliveryList.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = new ArrayList<>();
        adapter = new DeliveryAdapter(getContext(),orderList);

        deliveryList.setAdapter(adapter);

        firebaseDatabase.getReference("Orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    OrderModel orderModel = ds.getValue(OrderModel.class);
                    if(orderModel.getStatus().equals("Order Confirm") ||
                    orderModel.getStatus().equals("For Delivery") || orderModel.getStatus().equals("Delivered")
                    ){
                        orderList.add(orderModel);
                        orderModel.setKey(ds.getKey());
                    }


                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        adapter.setOnItemClickListener(new DeliveryAdapter.onItemClickListener() {
            @Override
            public void onClick(OrderModel orderModel) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

                alertDialog.setTitle("Choose an Action");
                CharSequence [] optionsAction  = {"For Delivery","Delivered","View Product Order"};



                alertDialog.setItems(optionsAction, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if(which == 0){
                            Map<String,Object> UpdateOrder = new HashMap<>();
                            UpdateOrder.put("status","For Delivery");

                            firebaseDatabase.getReference("Orders").child(orderModel.getKey()).updateChildren(UpdateOrder);
                        }else if (which == 1){
                            Map<String,Object> UpdateOrder = new HashMap<>();
                            UpdateOrder.put("status","Delivered");

                            firebaseDatabase.getReference("Orders").child(orderModel.getKey()).updateChildren(UpdateOrder);

                        }else if(which == 2){
                            ProductInfo(new Delivery_product_info(orderModel.getKey()));
                        }

                    }
                });
                alertDialog.show();


            }
        });


        return view;
    }

    private void ProductInfo(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main,fragment);
        fragmentTransaction.commit();

    }
}