package com.example.plantify_admin;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantify_admin.adapter.ProductAdapter;
import com.example.plantify_admin.model.ProductModel;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class home_layout extends Fragment {

    public home_layout() {
        // Required empty public constructor
    }

    private TextView bestSellingText, housePlantText, outdoorGardenText;
    private ArrayList<ProductModel> productList = new ArrayList<>();
    private ArrayList<ProductModel> fullProductList = new ArrayList<>();
    private ArrayList<ProductModel> housePlantsList, bestSellingList, outdoorGardenList;
    private ProductAdapter housePlantsAdapter, bestSellingAdapter, outdoorGardenAdapter, searchAdapter;

    private RecyclerView housePlantsRecyclerView, bestSellingRecyclerView, outdoorGardenRecyclerView, productListedRecyclerView;
    private FirebaseDatabase firebaseDatabase;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_layout, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        searchView = view.findViewById(R.id.searchItem);
        bestSellingText = view.findViewById(R.id.bestSelling);
        outdoorGardenText = view.findViewById(R.id.outdoorGarden);
        housePlantText = view.findViewById(R.id.housePlant);


        housePlantsRecyclerView = view.findViewById(R.id.housePlantsRecyclerView);
        bestSellingRecyclerView = view.findViewById(R.id.bestSellingRecyclerView);
        outdoorGardenRecyclerView = view.findViewById(R.id.outdoorGardenRecyclerView);
        productListedRecyclerView = view.findViewById(R.id.productListedRecyclerView);

        housePlantsList = new ArrayList<>();
        bestSellingList = new ArrayList<>();
        outdoorGardenList = new ArrayList<>();
        fullProductList = new ArrayList<>();
        productList = new ArrayList<>();

        housePlantsAdapter = new ProductAdapter(getContext(), housePlantsList);
        bestSellingAdapter = new ProductAdapter(getContext(), bestSellingList);
        outdoorGardenAdapter = new ProductAdapter(getContext(), outdoorGardenList);
        searchAdapter = new ProductAdapter(getContext(), productList);

        housePlantsRecyclerView.setAdapter(housePlantsAdapter);
        bestSellingRecyclerView.setAdapter(bestSellingAdapter);
        outdoorGardenRecyclerView.setAdapter(outdoorGardenAdapter);
        productListedRecyclerView.setAdapter(searchAdapter);

        housePlantsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        bestSellingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        outdoorGardenRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        productListedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        housePlantsAdapter.setOnItemClickListener(product -> SelectOption(product.getKey()));
        bestSellingAdapter.setOnItemClickListener(product -> SelectOption(product.getKey()));
        outdoorGardenAdapter.setOnItemClickListener(product -> SelectOption(product.getKey()));
        searchAdapter.setOnItemClickListener(product -> SelectOption(product.getKey()));

        firebaseDatabase.getReference("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                housePlantsList.clear();
                bestSellingList.clear();
                outdoorGardenList.clear();
                fullProductList.clear();
                productList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ProductModel product = ds.getValue(ProductModel.class);
                    if (product != null) {
                        product.setKey(ds.getKey());
                        fullProductList.add(product);

                        switch (product.getCategory()) {
                            case "House Plants":
                                housePlantsList.add(product);
                                break;
                            case "Best Selling Plants":
                                bestSellingList.add(product);
                                break;
                            case "Outdoor Garden Plants":
                                outdoorGardenList.add(product);
                                break;
                            default:
                                break;
                        }
                    }
                }

                housePlantsAdapter.notifyDataSetChanged();
                bestSellingAdapter.notifyDataSetChanged();
                outdoorGardenAdapter.notifyDataSetChanged();
                productList.addAll(fullProductList);
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProductList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProductList(newText);
                return true;
            }
        });

        return view;
    }

    private void filterProductList(String query) {
        if (query.isEmpty()) {
            productList.clear();
            productList.addAll(fullProductList);
            searchAdapter.notifyDataSetChanged();
            housePlantText.setVisibility(View.VISIBLE);
            bestSellingText.setVisibility(View.VISIBLE);
            outdoorGardenText.setVisibility(View.VISIBLE);
            housePlantsRecyclerView.setVisibility(View.VISIBLE);
            bestSellingRecyclerView.setVisibility(View.VISIBLE);
            outdoorGardenRecyclerView.setVisibility(View.VISIBLE);
            productListedRecyclerView.setVisibility(View.GONE);
        } else {
            ArrayList<ProductModel> filteredList = new ArrayList<>();
            for (ProductModel product : fullProductList) {
                if (product.getProductName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(product);
                }
            }
            productList.clear();
            productList.addAll(filteredList);
            searchAdapter.notifyDataSetChanged();
            housePlantText.setVisibility(View.GONE);
            bestSellingText.setVisibility(View.GONE);
            outdoorGardenText.setVisibility(View.GONE);
            housePlantsRecyclerView.setVisibility(View.GONE);
            bestSellingRecyclerView.setVisibility(View.GONE);
            outdoorGardenRecyclerView.setVisibility(View.GONE);

            productListedRecyclerView.setVisibility(View.VISIBLE);
        }
    }


    private void SelectOption(String id) {
        AlertDialog.Builder OptionSelect = new AlertDialog.Builder(getContext());

        CharSequence options[] = {"Edit", "Delete"};

        OptionSelect.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        EditProduct(id);
                        break;
                    case 1:
                        firebaseDatabase.getReference("Products").child(id).removeValue();
                        break;
                    default:
                }
            }
        });
        OptionSelect.show();
    }

    private void EditProduct(String id) {
        AlertDialog.Builder EditProducts = new AlertDialog.Builder(getContext());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.edit_product_layout, null, false);

        TextInputEditText productName, productPrice, productQuantity;

        productName = view.findViewById(R.id.productName);
        productPrice = view.findViewById(R.id.productPrice);
        productQuantity = view.findViewById(R.id.productQuantity);

        EditProducts.setView(view);

        firebaseDatabase.getReference("Products").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProductModel productModel = snapshot.getValue(ProductModel.class);
                if (productModel != null) {
                    productName.setText(productModel.getProductName());
                    productPrice.setText(productModel.getPrice());
                    productQuantity.setText(productModel.getQuantity());
                } else {
                    Log.e("home_layout", "ProductModel is null for id: " + id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        EditProducts.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Map<String, Object> UpdateProducts = new HashMap<>();
                UpdateProducts.put("Price", productPrice.getText().toString());
                UpdateProducts.put("ProductName", productName.getText().toString());
                UpdateProducts.put("Quantity", productQuantity.getText().toString());

                firebaseDatabase.getReference("Products").child(id).updateChildren(UpdateProducts);
                Toast.makeText(getContext(), "Product Updated", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        EditProducts.show();
    }
}


