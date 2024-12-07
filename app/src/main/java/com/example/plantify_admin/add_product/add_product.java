package com.example.plantify_admin.add_product;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.plantify_admin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class add_product extends Fragment {

    public add_product() {
        // Required empty public constructor
    }
    private ImageView ImageProduct;
    private MaterialButton uploadPhoto,sendProduct;
    private TextInputEditText productName,productPrice,Quantity,productDescr;
    private Bitmap bitmap;
    private Uri uri;
    private Spinner productCategory;

    private int CameraRequestCode = 100;
    private int GalleryRequestCode = 101;
    private int MediaRequestCode = 102;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;

    private FirebaseStorage firebaseStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_add_product, container, false);
        //Initialize Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Uploading product...");
        progressDialog.setCancelable(false);

        productName = view.findViewById(R.id.productName);
        productCategory = view.findViewById(R.id.productCategory);
        productPrice = view.findViewById(R.id.productPrice);
        Quantity = view.findViewById(R.id.productQuantity);
        productDescr = view.findViewById(R.id.Description);
        ImageProduct = view.findViewById(R.id.ImageProduct);
        uploadPhoto = view.findViewById(R.id.UploadPhoto);
        sendProduct = view.findViewById(R.id.SubmitProduct);
        uploadPhoto.setOnClickListener( v ->{
                ChooseOptionDialog();
        });
        sendProduct.setOnClickListener(v -> {
            String description = productDescr.getText().toString();
            String Pname = productName.getText().toString();
            String Pprice = productPrice.getText().toString();
            String PQty = Quantity.getText().toString();
            String selectedCategory = productCategory.getSelectedItem().toString();

            if (Pname.isEmpty() || Pprice.isEmpty() || PQty.isEmpty() || description.isEmpty() || bitmap == null) {
                Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else {
                uploadProduct(Pname, Pprice, PQty, description, selectedCategory, bitmap);
            }
        });

        return view;
    }
    private void uploadProduct(String Pname, String Pprice, String Pqty, String description, String category, Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        String imageid = UUID.randomUUID().toString();
        String localTime = String.valueOf(System.currentTimeMillis());

        StorageReference imageRef = firebaseStorage.getReference().child("Products/" + imageid);

        // Show the progress dialog
        progressDialog.show();

        imageRef.putBytes(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    Map<String, Object> uploadProduct = new HashMap<>();
                    uploadProduct.put("ImageUrl", imageUrl);
                    uploadProduct.put("Price", Pprice);
                    uploadProduct.put("Quantity", Pqty);
                    uploadProduct.put("ProductName", Pname);
                    uploadProduct.put("ProductDescription", description);
                    uploadProduct.put("Category", category);
                    uploadProduct.put("ProductRating", "");
                    uploadProduct.put("TotalRating", "");

                    firebaseDatabase.getReference().child("Products").child(localTime).setValue(uploadProduct).addOnSuccessListener(unused -> {
                        // Dismiss the progress dialog
                        progressDialog.dismiss();

                        Toast.makeText(getActivity(), "Product Added", Toast.LENGTH_SHORT).show();
                        productName.setText("");
                        productPrice.setText("");
                        Quantity.setText("");
                    }).addOnFailureListener(e -> {
                        // Dismiss the progress dialog and handle errors
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Failed to add product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    // Dismiss the progress dialog and handle errors
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                // Dismiss the progress dialog and handle errors
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


            if( requestCode == GalleryRequestCode && data != null  && data.getData() != null){
              uri = data.getData();
              try{
                  bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri);
                  ImageProduct.setImageBitmap(bitmap);

              }catch (Exception e){
                  throw new RuntimeException(e);
              }
        }else if(requestCode == CameraRequestCode && data != null  && data.getExtras() != null){
                        bitmap = (Bitmap) data.getExtras().get("data");
                        ImageProduct.setImageBitmap(bitmap);

            }else{
                Toast.makeText(getActivity(), "Cancelled Upload ", Toast.LENGTH_SHORT).show();

        }
    }
    private void ChooseOptionDialog(){

        AlertDialog.Builder Options = new AlertDialog.Builder(getActivity());

        Options.setTitle("Choose Method ");
        CharSequence option [] = {"Camera","Gallery"};

        Options.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == 0){

                  if(CheckCameraPermission()){
                      OpenCamera();
                  }else{
                      RequestCameraPermission();
                  }
                }else if(which == 1){

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        if(CheckMediaPermission()){
                            OpenGallery();
                        }else{
                            MediaPermission();
                        }

                    }else{
                        if(CheckGalleryPermission()){
                            OpenGallery();
                        }else{
                            Gallerypermission();
                        }
                    }
                }

            }
        });
        Options.show();

    }
    private boolean CheckCameraPermission (){

     return ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ;
    }
    private boolean CheckMediaPermission () {
        return ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
    }
    private boolean CheckGalleryPermission () {
        return ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    private void RequestCameraPermission(){
        ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.CAMERA},CameraRequestCode);
    }
    private void MediaPermission(){
        ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.READ_MEDIA_IMAGES},MediaRequestCode);
    }

    private void Gallerypermission(){
        ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},GalleryRequestCode);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == CameraRequestCode){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else{
                Toast.makeText(getActivity(), "Camera Permission is Required", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == MediaRequestCode){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else{
                Toast.makeText(getActivity(), "Media Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == GalleryRequestCode){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else{
                Toast.makeText(getActivity(), "Gallery Permission Denied", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void OpenCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CameraRequestCode);
    }

    private void OpenGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,GalleryRequestCode);
    }
}