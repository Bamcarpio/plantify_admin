package com.example.plantify_admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.plantify_admin.model.ProductModel;
import com.example.plantify_admin.R;

import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {


    private ArrayList<ProductModel> productList;
    private Context context;

    public ProductAdapter(Context context, ArrayList<ProductModel> productList){

        this.productList = productList;
        this.context = context;

    }

    public void updateList(ArrayList<ProductModel> filteredList) {
        this.productList.clear();
        this.productList.addAll(filteredList);
        notifyDataSetChanged();
    }


    onItemClickListener onItemClickListener;
    public void setOnItemClickListener(onItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    public interface onItemClickListener{
        void OnClick(ProductModel productModel);
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items,parent,false);

        ImageView imageView = convertView.findViewById(R.id.productImg);
        TextView productname = convertView.findViewById(R.id.productname);
        TextView productprice = convertView.findViewById(R.id.productprice);

        ProductModel productModel = productList.get(position);

        Glide.with(parent.getContext()).load(productModel.getImageUrl()).into(imageView);
        productname.setText(productModel.getProductName());
        productprice.setText("₱" +productModel.getPrice());

        convertView.setOnClickListener(v -> {onItemClickListener.OnClick(productModel);});


        return convertView;
    }
}
