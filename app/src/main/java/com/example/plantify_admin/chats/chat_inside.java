package com.example.plantify_admin.chats;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.plantify_admin.R;
import com.example.plantify_admin.adapter.chatAdapter;
import com.example.plantify_admin.model.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class chat_inside extends Fragment {

    String userKey ;
    String chatKey ;
    public chat_inside(String userKey,String chatKey) {
        this.userKey = userKey;
        this.chatKey= chatKey;
    }

    private EditText editTextMessage;
    private Button buttonSend;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private ArrayList<ChatModel> chatList;
    private chatAdapter adapter;
    private String currentUserId;
    private String otherUserId = userKey;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageButton buttonUploadImage;
    private FirebaseStorage storage;
    private StorageReference storageReference;// Consider passing this thr


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        currentUserId = "userAdmin" ;
        chatList = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_inside, container, false);
        initializeViews(view);
        setupRecyclerView();
        loadExistingChats();
        setupSendButton();
        setupImageUploadButton();

        return  view;
        }


        private void initializeViews(View view) {
            editTextMessage = view.findViewById(R.id.edit_text_message);
            buttonSend = view.findViewById(R.id.button_send);
            recyclerView = view.findViewById(R.id.recycler_view);
            recyclerView.scrollToPosition(chatList.size() - 1);
            buttonUploadImage = view.findViewById(R.id.button_upload_image);
        }





        private void setupRecyclerView() {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

            // Set the layoutManager to the recyclerView
            recyclerView.setLayoutManager(layoutManager);
            adapter = new chatAdapter(getContext(),chatList, currentUserId);
            recyclerView.setAdapter(adapter);
        }

        private void loadExistingChats() {
            if (currentUserId == null) return;
            setupFirebaseListener(chatKey);

        }

        private void setupSendButton() {
            buttonSend.setOnClickListener(v -> {
                String message = editTextMessage.getText().toString().trim();
                if (message.isEmpty()) {
                    Toast.makeText(getContext(), "Enter a message", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (currentUserId == null) {
                    Toast.makeText(getContext(), "You must be logged in to send messages",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                sendMessage(message);

                editTextMessage.setText("");
            });
        }



        private void sendMessage( String messageContent) {
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("Sender", currentUserId);
            messageData.put("Receiver", otherUserId);
            messageData.put("Messages", messageContent);
            messageData.put("Time", new Date().toString());

            firebaseDatabase.getReference("Chats")
                    .child(chatKey)
                    .child("Messages")
                    .push()
                    .setValue(messageData)
                    .addOnFailureListener(e -> Toast.makeText(getContext(),
                            "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        private void setupFirebaseListener(String chatId) {
            DatabaseReference messagesRef = firebaseDatabase.getReference()
                    .child("Chats")
                    .child(chatId)
                    .child("Messages");

            messagesRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                    ChatModel message = snapshot.getValue(ChatModel.class);
                    if (message != null) {
                        chatList.add(message);
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(chatList.size() - 1);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load messages: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }


        private void setupImageUploadButton() {
            buttonUploadImage.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            });
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                    && data != null && data.getData() != null) {
                uploadImage(data.getData());
            }
        }

        private void uploadImage(Uri imageUri) {
            if (currentUserId == null) return;

            // Show progress dialog
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Create a reference to store the image
            String imageName = "images/" + UUID.randomUUID().toString();
            StorageReference imageRef = storageReference.child(imageName);

            // Upload the image
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            sendImageMessage(imageUrl);
                            progressDialog.dismiss();
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }

        private void sendImageMessage(String imageUrl) {
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("Sender", "userAdmin");
            messageData.put("Receiver", otherUserId);
            messageData.put("Messages", ""); // Empty message for image
            messageData.put("Time", new Date().toString());
            messageData.put("imageUrl", imageUrl);


            DatabaseReference chatsRef = firebaseDatabase.getReference("Chats");
            chatsRef.orderByChild("userid_1")
                    .equalTo("userAdmin")

                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                                String userid1 = chatSnapshot.child("userid_1").getValue(String.class);
                                String userid2 = chatSnapshot.child("userid_2").getValue(String.class);

                                if ((userid1 != null && userid2 != null) &&
                                        ((userid1.equals(currentUserId) && userid2.equals(otherUserId)) ||
                                                (userid1.equals(otherUserId) && userid2.equals(currentUserId)))) {

                                    break;
                                }
                            }

                            if (chatKey != null) {
                                firebaseDatabase.getReference("Chats")
                                        .child(chatKey)
                                        .child("Messages")
                                        .push()
                                        .setValue(messageData)
                                        .addOnFailureListener(e -> Toast.makeText(getContext(),
                                                "Failed to send image: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show());
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Failed to send image: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }



